package com.github.xiaoyao9184.eproject.filestorage.mvc;

import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.file.FileSystemException;

/**
 * name
 * Created by xy on 2020/3/13.
 */
@RestController
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(value = "/v1/names")
@Api(value = "/v1/names")
public class FileNameController {

    @Autowired
    private FileStorage<URI> storage;

    private String extractPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE); // /elements/CATEGORY1/CATEGORY1_1/ID
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE); // /elements/**

        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path); // CATEGORY1/CATEGORY1_1/ID
    }


    @ApiOperation(
            value = "rename file",
            notes = "rename file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "not found"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(
            value = "/{path:.*}/**",
            method = {RequestMethod.PUT})
    public ResponseEntity<Void> rename(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @ApiParam(value = "new file name", required = true)
            @RequestParam("name") String name,
            HttpServletRequest request) throws Exception {
        path = extractPath(request);

        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        if(storage.rename(uri,name)){
            return ResponseEntity.ok(null);
        }else{
            throw new FileSystemException("cant rename!");
        }
    }


}
