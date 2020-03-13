package com.github.xiaoyao9184.eproject.filestorage.http;

import com.github.xiaoyao9184.eproject.filestorage.core.FilePointer;
import com.github.xiaoyao9184.eproject.filestorage.io.FixedLengthInputStreamResource;
import com.github.xiaoyao9184.eproject.filestorage.io.ThrottlingInputStream;
import com.google.common.collect.Streams;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.springframework.http.HttpStatus.*;

/**
 * Created by xy on 2020/2/14.
 */

public class ExistingFile {
    private static final Logger log = LoggerFactory.getLogger(ExistingFile.class);

    private final HttpMethod method;
    private final FilePointer filePointer;
    private final Optional<MediaType> mediaType;

    public ExistingFile(HttpMethod method, FilePointer filePointer) {
        this.method = method;
        this.filePointer = filePointer;
        this.mediaType = Optional.empty();
    }

    public ExistingFile(HttpMethod method, FilePointer pointer, MediaType mediaType) {
        this.method = method;
        this.filePointer = pointer;
        this.mediaType = Optional.of(mediaType);
    }

    public ResponseEntity<ResourceRegion> handle(
            Optional<String> requestEtagOpt,
            Optional<Date> ifModifiedSinceOpt,
            Optional<String> ranges) {
        Optional<List<HttpRange>> httpRanges = ranges.map(HttpRange::parseRanges);
        return serveWithCaching(requestEtagOpt, ifModifiedSinceOpt, httpRanges, this::serveDownload);
    }

    public ResponseEntity<Resource> handle(
            Optional<String> requestEtagOpt,
            Optional<Date> ifModifiedSinceOpt) {
        return serveWithCaching(requestEtagOpt, ifModifiedSinceOpt, this::serveDownload);
    }

    private ResponseEntity serveWithCaching(
            Optional<String> requestEtagOpt,
            Optional<Date> ifModifiedSinceOpt,
            Optional<List<HttpRange>> range,
            BiFunction<Optional<List<HttpRange>>, FilePointer, ResponseEntity<Resource>> notCachedResponse) {
        if (cached(requestEtagOpt, ifModifiedSinceOpt))
            return notModified(filePointer);
        return notCachedResponse.apply(range, filePointer);
    }

    private ResponseEntity<Resource> serveWithCaching(
            Optional<String> requestEtagOpt,
            Optional<Date> ifModifiedSinceOpt,
            Function<FilePointer, ResponseEntity<Resource>> notCachedResponse) {
        if (cached(requestEtagOpt, ifModifiedSinceOpt))
            return notModified(filePointer);
        return notCachedResponse.apply(filePointer);
    }

    private boolean cached(Optional<String> requestEtagOpt, Optional<Date> ifModifiedSinceOpt) {
        final boolean matchingEtag = requestEtagOpt
                .map(filePointer::matchesEtag)
                .orElse(false);
        final boolean notModifiedSince = ifModifiedSinceOpt
                .map(Date::toInstant)
                .map(filePointer::modifiedAfter)
                .orElse(false);
        return matchingEtag || notModifiedSince;
    }

    private ResponseEntity serveDownload(Optional<List<HttpRange>> ranges, FilePointer filePointer) {
        log.debug("Serving {} '{}'", method, filePointer);
        InputStreamResource resource = resourceToReturn(filePointer);

        //@see ResourceHttpRequestHandler
        return ranges.map(httpRanges -> {
            if (httpRanges.size() == 1) {
                long contentLength = filePointer.getSize();
                long start = httpRanges.get(0).getRangeStart(contentLength);
                long end = httpRanges.get(0).getRangeEnd(contentLength);
                ResourceRegion resourceRegion = new ResourceRegion(resource, start, end - start + 1);
                return responseAny(filePointer, PARTIAL_CONTENT, resourceRegion);
            }else{
                //TODO
                return responseAny(filePointer, PARTIAL_CONTENT, HttpRange.toResourceRegions(httpRanges, resource));
            }
        })
                .orElse((ResponseEntity)response(filePointer, OK, resource));
    }

    private ResponseEntity<Resource> serveDownload(FilePointer filePointer) {
        log.debug("Serving {} '{}'", method, filePointer);
        InputStreamResource resource = resourceToReturn(filePointer);
        return response(filePointer, OK, resource);
    }

    private InputStreamResource resourceToReturn(FilePointer filePointer) {
        if (method == HttpMethod.GET)
            return buildResource(filePointer);
        else
            return null;
    }

    private InputStreamResource buildResource(FilePointer filePointer) {
        try {
            InputStream inputStream = filePointer.open();
            final RateLimiter throttler = RateLimiter.create(FileUtils.ONE_MB);
            final ThrottlingInputStream throttlingInputStream = new ThrottlingInputStream(inputStream, throttler);
            return new FixedLengthInputStreamResource(throttlingInputStream, filePointer.getSize());
        } catch (Exception e) {
            e.printStackTrace();
            //TODO FileNotFoundException
            return null;
        }
    }

    private ResponseEntity notModified(FilePointer filePointer) {
        log.trace("Cached on client side {}, returning 304", filePointer);
        return response(filePointer, NOT_MODIFIED);
    }

    private ResponseEntity response(FilePointer filePointer, HttpStatus status) {
        final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity
                .status(status)
                .eTag(filePointer.getEtag())
                .contentLength(filePointer.getSize())
                .lastModified(filePointer.getLastModified().toEpochMilli());

        Stream.concat(
                Streams.stream(mediaType),
                Streams.stream(filePointer.getMediaType())
                        .map(this::toMediaType)
        )
                .findFirst()
                .ifPresent(responseBuilder::contentType);
        return responseBuilder.body(null);
    }

    private ResponseEntity<Object> responseAny(FilePointer filePointer, HttpStatus status, Object body) {
        final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity
                .status(status)
                .eTag(filePointer.getEtag())
                .contentLength(filePointer.getSize())
                .lastModified(filePointer.getLastModified().toEpochMilli());

        Stream.concat(
                Streams.stream(mediaType),
                Streams.stream(filePointer.getMediaType())
                        .map(this::toMediaType)
        )
                .findFirst()
                .ifPresent(responseBuilder::contentType);
        return responseBuilder.body(body);
    }

    private ResponseEntity<Resource> response(FilePointer filePointer, HttpStatus status, Resource body) {
        final ResponseEntity.BodyBuilder responseBuilder = ResponseEntity
                .status(status)
                .eTag(filePointer.getEtag())
                .contentLength(filePointer.getSize())
                .lastModified(filePointer.getLastModified().toEpochMilli());

        Stream.concat(
                Streams.stream(mediaType),
                Streams.stream(filePointer.getMediaType())
                        .map(this::toMediaType)
        )
                .findFirst()
                .ifPresent(responseBuilder::contentType);
        return responseBuilder.body(body);
    }

    private MediaType toMediaType(com.google.common.net.MediaType input) {
        return input.charset()
                .transform(c -> new MediaType(input.type(), input.subtype(), c))
                .or(new MediaType(input.type(), input.subtype()));
    }

}
