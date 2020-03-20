package com.github.xiaoyao9184.eproject.filetable.core.handle;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableRepositoryProvider;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.repository.FileTableRepository;
import com.github.xiaoyao9184.eproject.filetable.util.URIUtil;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static com.github.xiaoyao9184.eproject.filetable.util.UUIDUtil.uuidToHierarchyIdNodeString;

/**
 * Use the repository {@link FileTableRepository } to implement operations.
 * repository provided by the provider {@link FileTableRepositoryProvider }.
 *
 * Created by xy on 2020/1/15.
 */
public class DatabaseFileTableHandler implements FileTableHandler {

    private FileTableRepository fileTableRepository;

    @Autowired
    private BaseFileTableProperties fileTableProperties;

    @Autowired
    private FileTableRepositoryProvider fileTableRepositoryProvider;

    @Autowired
    private FileTableNameProvider fileTableNameProvider;

    @PostConstruct
    public void initRepository(){
        this.fileTableRepository = fileTableRepositoryProvider.provide();
    }

    @Override
    public AbstractFileTable create(MultipartFile multipartFile, URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public AbstractFileTable create(File file, URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public AbstractFileTable create(InputStream stream, URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public AbstractFileTable create(byte[] bytes, URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public AbstractFileTable create(URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public File readFile(URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant get File, but you can get byte array of stream");
    }

    @Override
    public InputStream readStream(URI uri) throws Exception {
        String fileNamespacePath = toFileNamespacePath(uri);

        return fileTableRepository.getBlobByPath(fileNamespacePath)
                .getBinaryStream();
    }

    @Override
    public byte[] readBytes(URI uri) throws Exception {
        String fileNamespacePath = toFileNamespacePath(uri);

        return fileTableRepository.getBytesByPath(fileNamespacePath);
    }

    @Override
    public AbstractFileTable read(URI uri) throws Exception {
        String fileNamespacePath = toFileNamespacePath(uri);

        return fileTableRepository.getByPath(fileNamespacePath);
    }

    /**
     * Note: cant remove dir include files by FK of [parent_path_locator] column
     * @param uri
     * @return
     * @throws Exception
     */
    @Override
    public boolean delete(URI uri) throws Exception {
        String fileNamespacePath = toFileNamespacePath(uri);

        return fileTableRepository.deleteByPath(fileNamespacePath) == 1;
    }

    @Override
    public List<AbstractFileTable> readChild(URI uri) throws Exception {
        if(uri.getPath().length() == 0 ||
                "/".equals(uri.getPath())){
            return fileTableRepository.getChildByRootLocator();
        }else{
            String fullFileNamespacePath = toFullFileNamespacePath(uri);

            return fileTableRepository.getChildByPathLocator(fullFileNamespacePath);
        }
    }

    @Override
    public boolean rename(URI uri, String name){
        String fullFileNamespacePath = toFullFileNamespacePath(uri);

        return fileTableRepository.updateNameByPathLocator(fullFileNamespacePath,name) == 1;
    }

    @Override
    public List<AbstractFileTable> search(URI uri, String search) {
        String fileNamespacePath = toFileNamespacePath(uri);

        return fileTableRepository.findByFileNamespacePathStartsWithAndNameContains(fileNamespacePath,search);
    }


    /**
     * file namespace path is \table-name\{uri}
     * @param uri
     * @return
     */
    private String toFileNamespacePath(URI uri){
        URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                .path(fileTableNameProvider.provide())
                .path(uri.getPath())
                .build()
                .toUri();
        return "\\" +
                fileNamespacePathUri.getPath().replace("/","\\");
    }

    /**
     * full file namespace path is \\servername\instance-share\database-directory\FileTable-directory\{uri}
     * it can transform PathLocator
     * @param uri
     * @return
     */
    private String toFullFileNamespacePath(URI uri){
        //use root because maybe servername use ip
        //and SQLServer use hostname
        URI base = UriComponentsBuilder.fromUriString(
                "smb:" +
                        fileTableProperties.getRootPath()
                                .replace("\\","/")
        )
                .build()
                .toUri();

        //path locator is full path with smb share path
        URI fullFileNamespacePathUri = UriComponentsBuilder.fromUri(base)
                .pathSegment(fileTableNameProvider.provide())
                .path(uri.toString())
                .build()
                .toUri();
        String fullFileNamespacePath = "\\\\" +
                fullFileNamespacePathUri.getHost() +
                fullFileNamespacePathUri.getPath().replace("/","\\");

        //PathLocator Not included last '/'
        if(fullFileNamespacePath.endsWith("/")){
            fullFileNamespacePath = fullFileNamespacePath.substring(0,fullFileNamespacePath.length() - 1);
        }

        return fullFileNamespacePath;
    }

    private String makeDirectoryRecursion(URI uri) throws FileNotFoundException {
        Tuple2<String,String> pathName = URIUtil.separatePathAndNameFromURI(uri);

        //default is root
        String parentDirectoryPathLocator = "/";
        if(!StringUtils.isEmpty(pathName.v1())) {
            URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                    .path(fileTableNameProvider.provide())
                    .path(pathName.v1())
                    .build()
                    .toUri();
            String fileNamespacePath = "\\" +
                    fileNamespacePathUri.getPath().replace("/","\\");
            parentDirectoryPathLocator = fileTableRepository.getPathLocatorStringByPath(fileNamespacePath);
        }

        if(parentDirectoryPathLocator == null &&
                fileTableProperties.isAutoCreateDirectory()){
            URI pUri = UriComponentsBuilder.fromUriString(pathName.v1()).build().toUri();
            parentDirectoryPathLocator = makeDirectoryRecursion(pUri);
        }else if(parentDirectoryPathLocator == null) {
            throw new FileNotFoundException("Parent directory not exist: " + pathName.v1());
        }

        String pathLocator = parentDirectoryPathLocator
                + uuidToHierarchyIdNodeString(UUID.randomUUID());

        assert fileTableRepository.insertDirectoryByNameAndPathLocator(pathLocator,pathName.v2()) == 1;

        return pathLocator;
    }



    public boolean makeFile(URI uri) throws FileNotFoundException {
        Tuple2<String,String> pathName = URIUtil.separatePathAndNameFromURI(uri);
        URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                .path(fileTableNameProvider.provide())
                .path(pathName.v1())
                .build()
                .toUri();
        String fileNamespacePath = "\\" +
                fileNamespacePathUri.getPath().replace("/","\\");

        String parentDirectoryPathLocator = fileTableRepository.getPathLocatorStringByPath(fileNamespacePath);

        if(parentDirectoryPathLocator == null){
            throw new FileNotFoundException("Parent directory not exist: " + pathName.v1());
        }

        String pathLocator = parentDirectoryPathLocator
                + uuidToHierarchyIdNodeString(UUID.randomUUID());

        return fileTableRepository.insertFileByNameAndPathLocator(pathLocator,pathName.v2()) == 1;
    }

    public boolean makeDirectory(URI uri) throws FileNotFoundException {
        Tuple2<String,String> pathName = URIUtil.separatePathAndNameFromURI(uri);
        URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                .path(fileTableNameProvider.provide())
                .path(pathName.v1())
                .build()
                .toUri();
        String fileNamespacePath = "\\" +
                fileNamespacePathUri.getPath().replace("/","\\");

        String parentDirectoryPathLocator = fileTableRepository.getPathLocatorStringByPath(fileNamespacePath);

        if(parentDirectoryPathLocator == null){
            throw new FileNotFoundException("Parent directory not exist: " + pathName.v1());
        }

        String pathLocator = parentDirectoryPathLocator
                + uuidToHierarchyIdNodeString(UUID.randomUUID());

        return fileTableRepository.insertDirectoryByNameAndPathLocator(pathLocator,pathName.v2()) == 1;
    }

    public boolean mkdirs(URI uri) throws FileNotFoundException {
        Tuple2<String,String> pathName = URIUtil.separatePathAndNameFromURI(uri);
        URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                .path(fileTableNameProvider.provide())
                .path(pathName.v1())
                .build()
                .toUri();
        String fileNamespacePath = "\\" +
                fileNamespacePathUri.getPath().replace("/","\\");

        String parentDirectoryPathLocator = fileTableRepository.getPathLocatorStringByPath(fileNamespacePath);

        if(parentDirectoryPathLocator == null &&
                fileTableProperties.isAutoCreateDirectory()){
            URI pUri = UriComponentsBuilder.fromUriString(pathName.v1()).build().toUri();
            parentDirectoryPathLocator = makeDirectoryRecursion(pUri);
        }else if(parentDirectoryPathLocator == null) {
            throw new FileNotFoundException("Parent directory not exist: " + pathName.v1());
        }

        String pathLocator = parentDirectoryPathLocator
                + uuidToHierarchyIdNodeString(UUID.randomUUID());

        return fileTableRepository.insertDirectoryByNameAndPathLocator(pathLocator,pathName.v2()) == 1;
    }
}
