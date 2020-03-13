package com.github.xiaoyao9184.eproject.filestorage.mvc;

import com.github.xiaoyao9184.eproject.filestorage.http.ExistingFile;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Date;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;

/**
 * get file by mime (only mapping range header request)
 * Created by xy on 2020/2/14.
 */
@RestController
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(value = "/v1/mimes")
@Api(value = "/v1/mimes")
public class MimeStreamRangeController {

    @Autowired
    private FileStorage<URI> storage;

    private String extractPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE); // /elements/CATEGORY1/CATEGORY1_1/ID
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE); // /elements/**

        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path); // CATEGORY1/CATEGORY1_1/ID
    }

    private Optional<ExistingFile> findExistingFile(HttpMethod method, URI path) {
        return storage
                .findFile(path)
                .map(pointer -> new ExistingFile(method, pointer));
    }

    private Optional<ExistingFile> findExistingFile(HttpMethod method, URI path, MediaType mediaType) {
        return storage
                .findFile(path)
                .map(pointer -> new ExistingFile(method, pointer, mediaType));
    }

    @ApiOperation(
            value = "download file as mime application/octet-stream",
            notes = "download file as mime application/octet-stream")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 206, message = "part"),
            @ApiResponse(code = 304, message = "not change"),
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(
            value = "/application/octet-stream/{path:.*}/**",
            headers = RANGE,
            method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ResourceRegion> mime_application_octet_stream(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @RequestHeader(IF_NONE_MATCH) Optional<String> requestEtagOpt,
            @RequestHeader(IF_MODIFIED_SINCE) Optional<Date> ifModifiedSinceOpt,
            @RequestHeader(RANGE) Optional<String> range,
            HttpMethod method,
            HttpServletRequest request){
        path = extractPath(request);

        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        return findExistingFile(method, uri, APPLICATION_OCTET_STREAM)
                .map(file -> file.handle(requestEtagOpt, ifModifiedSinceOpt, range))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }


    @ApiOperation(
            value = "download file as mime application/?",
            notes = "download file as mime application/?")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 206, message = "part"),
            @ApiResponse(code = 304, message = "not change"),
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(
            value = "/application/{type}/{path:.*}/**",
            headers = RANGE,
            method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ResourceRegion> mime_application(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @ApiParam(value = "sub type", required = true)
            @PathVariable("type") String type,
            @RequestHeader(IF_NONE_MATCH) Optional<String> requestEtagOpt,
            @RequestHeader(IF_MODIFIED_SINCE) Optional<Date> ifModifiedSinceOpt,
            @RequestHeader(RANGE) Optional<String> range,
            HttpMethod method,
            HttpServletRequest request){
        path = extractPath(request);

        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        return findExistingFile(method, uri, MediaType.parseMediaType("application/" + type))
                .map(file -> file.handle(requestEtagOpt, ifModifiedSinceOpt, range))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @ApiOperation(
            value = "download file as mime image/?",
            notes = "download file as mime image/?")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 206, message = "part"),
            @ApiResponse(code = 304, message = "not change"),
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(
            value = "/image/{type}/{path:.*}/**",
            headers = RANGE,
            method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ResourceRegion> mime_image(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @ApiParam(value = "sub type", required = true)
            @PathVariable("type") String type,
            //TODO
            @RequestParam(value = "height", required = false) Integer height,
            @RequestParam(value = "width", required = false) Integer width,
            @RequestHeader(IF_NONE_MATCH) Optional<String> requestEtagOpt,
            @RequestHeader(IF_MODIFIED_SINCE) Optional<Date> ifModifiedSinceOpt,
            @RequestHeader(RANGE) Optional<String> range,
            HttpMethod method,
            HttpServletRequest request){
        path = extractPath(request);

        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        return findExistingFile(method, uri, MediaType.parseMediaType("image/" + type))
                .map(file -> file.handle(requestEtagOpt, ifModifiedSinceOpt, range))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }


    @ApiOperation(
            value = "download file as mime ?/?",
            notes = "download file as mime ?/?")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 206, message = "part"),
            @ApiResponse(code = 304, message = "not change"),
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(
            value = "/{type}/{subType}/{path:.*}/**",
            headers = RANGE,
            method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ResourceRegion> mime_any(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @ApiParam(value = "type", required = true)
            @PathVariable("type") String type,
            @ApiParam(value = "sub type", required = true)
            @PathVariable("subType") String subType,
            @RequestHeader(IF_NONE_MATCH) Optional<String> requestEtagOpt,
            @RequestHeader(IF_MODIFIED_SINCE) Optional<Date> ifModifiedSinceOpt,
            @RequestHeader(RANGE) Optional<String> range,
            HttpMethod method,
            HttpServletRequest request){
        path = extractPath(request);

        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        return findExistingFile(method, uri, MediaType.parseMediaType(type + "/" + subType))
                .map(file -> file.handle(requestEtagOpt, ifModifiedSinceOpt, range))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }


    @ApiOperation(
            value = "download file as mime auto by probes file",
            notes = "download file as mime auto by probes file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 206, message = "part"),
            @ApiResponse(code = 304, message = "not change"),
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(
            value = "/auto/auto/{path:.*}/**",
            headers = RANGE,
            method = {RequestMethod.GET, RequestMethod.HEAD})
    public ResponseEntity<ResourceRegion> mime_auto(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @RequestHeader(IF_NONE_MATCH) Optional<String> requestEtagOpt,
            @RequestHeader(IF_MODIFIED_SINCE) Optional<Date> ifModifiedSinceOpt,
            @RequestHeader(RANGE) Optional<String> range,
            HttpMethod method,
            HttpServletRequest request){
        path = extractPath(request);

        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        return findExistingFile(method, uri)
                .map(file -> file.handle(requestEtagOpt, ifModifiedSinceOpt, range))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }
}
