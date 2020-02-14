package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filetable.core.DatabaseFileTableHandler;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;

/**
 * search file info
 * Created by xy on 2020/2/14.
 */
@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(value = "/v1/files-search")
@Api(value = "/v1/files-search")
public class FileInfoSearchController {

    private static Logger logger = LoggerFactory.getLogger(FileInfoSearchController.class);

    @Autowired
    private DatabaseFileTableHandler databaseFileTableHandler;

    private String extractPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE); // /elements/CATEGORY1/CATEGORY1_1/ID
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE); // /elements/**

        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path); // CATEGORY1/CATEGORY1_1/ID
    }

    @ApiOperation(
            value = "search file",
            notes = "search file")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 204, message = "no content"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(value = {"/{path:.*}/**", "/"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<FileInfo>> get(
            @ApiParam(value = "file path", required = false)
            @PathVariable(value = "path", required = false) String path,
            @ApiParam(value = "search key", required = false)
            @RequestParam(value = "search", required = false) String search,
            HttpServletRequest request){
        path = extractPath(request);
        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();
        List<FileInfo> attachList = databaseFileTableHandler.search(uri, search);
        if(attachList.size() == 0){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        logger.debug("File path:{}", path);

        return new ResponseEntity<>(attachList, HttpStatus.OK);
    }

}
