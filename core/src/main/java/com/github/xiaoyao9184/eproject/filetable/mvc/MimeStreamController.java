package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import com.github.xiaoyao9184.eproject.filetable.service.FileTableService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * get file by mime
 * Created by xy on 2020/1/15.
 */
@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(value = "/v1/mimes")
@Api(value = "/v1/mimes")
public class MimeStreamController {

    private static Logger logger = LoggerFactory.getLogger(MimeStreamController.class);

    @Autowired
    private FileTableService fileTableService;

    private String extractPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE); // /elements/CATEGORY1/CATEGORY1_1/ID
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE); // /elements/**

        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path); // CATEGORY1/CATEGORY1_1/ID
    }


    @ApiOperation(
            value = "download file as mime application/octet-stream",
            notes = "download file as mime application/octet-stream")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 204, message = "empty"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(value = "/application/octet-stream/{path:.*}/**", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> mime_application_octet_stream(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            HttpServletRequest request) throws Exception {
        path = extractPath(request);

        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        FileInfo fileInfo = fileTableService.read(uri);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        String fileName = new String(fileInfo.getName().getBytes("UTF-8"),"iso-8859-1");
        headers.setContentDispositionFormData("attachment", fileName);
        byte[] data = fileTableService.readBytes(uri);

        logger.debug("File path:{}", path);

        return new ResponseEntity<>(
                data,
                headers,
                HttpStatus.OK);
    }

    @ApiOperation(
            value = "download file as mime application/?",
            notes = "download file as mime application/?")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 204, message = "empty"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(value = "/application/{type}/{path:.*}/**", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> mime_application(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @ApiParam(value = "sub type", required = true)
            @PathVariable("type") String type,
            HttpServletRequest request) throws Exception {
        path = extractPath(request);
        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        FileInfo fileInfo = fileTableService.read(uri);
        HttpHeaders headers = new HttpHeaders();
        MediaType mt = MediaType.parseMediaType("application/" + type);
        headers.setContentType(mt);
        String fileName = new String(fileInfo.getName().getBytes("UTF-8"),"iso-8859-1");
        headers.setContentDispositionFormData("attachment", fileName);
        byte[] data = fileTableService.readBytes(uri);

        logger.debug("File path:{}", path);

        return new ResponseEntity<>(
                data,
                headers,
                HttpStatus.OK);
    }

    @ApiOperation(
            value = "download file as mime image/?",
            notes = "download file as mime image/?")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 204, message = "empty"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(value = "/image/{type}/{path:.*}/**", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> mime_image(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @ApiParam(value = "sub type", required = true)
            @PathVariable("type") String type,
            HttpServletRequest request) throws Exception {
        path = extractPath(request);
        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        FileInfo fileInfo = fileTableService.read(uri);
        HttpHeaders headers = new HttpHeaders();
        MediaType mt = MediaType.parseMediaType("image/" + type);
        headers.setContentType(mt);
        String fileName = new String(fileInfo.getName().getBytes("UTF-8"),"iso-8859-1");
        headers.setContentDispositionFormData("attachment", fileName);

        //TODO height width
        byte[] data = fileTableService.readBytes(uri);

        logger.debug("File path:{}", path);

        return new ResponseEntity<>(
                data,
                headers,
                HttpStatus.OK);
    }

    @ApiOperation(
            value = "download file as mime ?/?",
            notes = "download file as mime ?/?")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 204, message = "empty"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(value = "/{type}/{subType}/{path:.*}/**", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseEntity<byte[]> mime_any(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @ApiParam(value = "type", required = true)
            @PathVariable("type") String type,
            @ApiParam(value = "sub type", required = true)
            @PathVariable("subType") String subType,
            HttpServletRequest request) throws Exception {
        path = extractPath(request);
        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        FileInfo fileInfo = fileTableService.read(uri);
        HttpHeaders headers = new HttpHeaders();
        MediaType mt = MediaType.parseMediaType(type + "/" + subType);
        headers.setContentType(mt);
        String fileName = new String(fileInfo.getName().getBytes("UTF-8"),"iso-8859-1");
        headers.setContentDispositionFormData("attachment", fileName);

        byte[] data = fileTableService.readBytes(uri);

        logger.debug("File path:{}", path);

        return new ResponseEntity<>(
                data,
                headers,
                HttpStatus.OK);
    }
}
