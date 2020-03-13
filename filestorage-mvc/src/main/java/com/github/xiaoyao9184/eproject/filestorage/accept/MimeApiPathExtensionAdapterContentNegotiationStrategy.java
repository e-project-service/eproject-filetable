package com.github.xiaoyao9184.eproject.filestorage.accept;

import com.google.common.collect.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodProcessor;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by xy on 2020/3/13.
 */
public class MimeApiPathExtensionAdapterContentNegotiationStrategy
        extends PathExtensionContentNegotiationStrategy
        implements ContentNegotiationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(MimeApiPathExtensionAdapterContentNegotiationStrategy.class);

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private Pattern pattern = Pattern.compile("\\/v1\\/mimes\\/(.*?)\\/(.*?)\\/");

    public MimeApiPathExtensionAdapterContentNegotiationStrategy() {
        this.urlPathHelper.setUrlDecode(false);
    }


    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest webRequest) throws HttpMediaTypeNotAcceptableException {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            logger.warn("An HttpServletRequest is required to determine the media type key");
            return null;
        }
        String path = this.urlPathHelper.getLookupPathForRequest(request);
        Matcher matcher = pattern.matcher(path);
        if(matcher.find()){
            MediaType mediaType = MediaType.parseMediaType(matcher.group(1) + "/" + matcher.group(2));
            return Streams.stream(Optional.ofNullable(mediaType))
                    .collect(Collectors.toList());
        }else{
            return super.resolveMediaTypes(webRequest);
        }
    }

    /**
     * ContentDisposition header will add when file name extension is not safe at
     * {@link AbstractMessageConverterMethodProcessor } addContentDispositionHeader
     * and if {@link ContentNegotiationManager } has a {@link PathExtensionContentNegotiationStrategy}
     * it will resolve {@link MediaType } used file name extension to check safe again
     * {@link AbstractMessageConverterMethodProcessor } safeMediaTypesForExtension
     *
     * @param webRequest
     * @param fileNameExtension
     * @return
     * @throws HttpMediaTypeNotAcceptableException
     */
    @Override
    public List<MediaType> resolveMediaTypeKey(NativeWebRequest webRequest, String fileNameExtension)
            throws HttpMediaTypeNotAcceptableException {
        if(webRequest == null){
            //+XML is safe
            return Collections.singletonList(MediaType.APPLICATION_XHTML_XML);
        }
        return Collections.emptyList();
    }
}
