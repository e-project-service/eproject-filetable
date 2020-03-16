package com.github.xiaoyao9184.eproject.filetable.mvc;

import com.github.xiaoyao9184.eproject.filestorage.core.FileInfoStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.FilePointer;
import com.github.xiaoyao9184.eproject.filestorage.core.FileStorage;
import com.github.xiaoyao9184.eproject.filestorage.core.MultipartFilePointer;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * upload file and get file info
 * Created by xy on 2020/1/15.
 */
@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@RequestMapping(value = "/v1/files")
@Api(value = "/v1/files")
public class FileInfoController {

    private static Logger logger = LoggerFactory.getLogger(FileInfoController.class);

    @Autowired
    private FileInfoStorage<URI, FileInfo> infoStorage;

    @Autowired
    private FileStorage<URI> storage;

    private String extractPath(HttpServletRequest request) {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE); // /elements/CATEGORY1/CATEGORY1_1/ID
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE); // /elements/**

        return new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path); // CATEGORY1/CATEGORY1_1/ID
    }

    /**
     * Create FilePointer to MultipartFile
     * @param multipartFile
     * @param path
     * @return
     */
    private Optional<FilePointer> createFilePointer(MultipartFile multipartFile, URI path) {
        return Optional.ofNullable(multipartFile)
                .map(file -> new MultipartFilePointer(path, file));
    }

    @ApiOperation(
            value = "upload file",
            notes = "upload file")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(value = "/{path:.*}/**", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<FileInfo> add(
            @ApiParam(value = "file path", required = true)
            @PathVariable("path") String path,
            @ApiParam(value = "file", required = false)
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) throws Exception {
        path = extractPath(request);
        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();

        FileInfo fileInfo = infoStorage.storageInfo(createFilePointer(file,uri),uri);

        logger.debug("File path:{}", path);

        return new ResponseEntity<>(fileInfo,HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "delete file",
            notes = "delete file")
    @ApiResponses(value = {
            @ApiResponse(code = 202, message = "accepted"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(value = "/{path:.*}/**", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(
            @ApiParam(value = "file path", required = true)
            @PathVariable String path,
            HttpServletRequest request) throws Exception {
        path = extractPath(request);
        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();
        storage.delete(uri);

        logger.debug("File path:{}", path);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @ApiOperation(
            value = "get file info",
            notes = "get file info")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 204, message = "no content"),
            @ApiResponse(code = 500, message = "error")}
    )
    @RequestMapping(value = {"/{path:.*}/**", "/"}, method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> get(
            @ApiParam(value = "file path", required = false)
            @PathVariable(value = "path", required = false) String path,
            HttpServletRequest request) throws Exception {
        path = extractPath(request);
        URI uri = UriComponentsBuilder.newInstance()
                .pathSegment(path)
                .build()
                .toUri();
        if(StringUtils.isEmpty(path) ||
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString().endsWith("/")){
            List<FileInfo> fileInfos = infoStorage.listInfo(uri);
            if(fileInfos.size() == 0){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            logger.debug("File path:{}", path);

            return new ResponseEntity<>(fileInfos, HttpStatus.OK);
        }else{
            FileInfo fileInfo = infoStorage.findInfo(uri);
            if(fileInfo == null){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            logger.debug("File path:{}", path);

            return new ResponseEntity<>(fileInfo, HttpStatus.OK);
        }
    }

}
