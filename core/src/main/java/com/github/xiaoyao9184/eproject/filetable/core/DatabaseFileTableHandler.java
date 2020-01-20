package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.model.BaseFileTableProperties;
import com.github.xiaoyao9184.eproject.filetable.model.FileInfo;
import com.github.xiaoyao9184.eproject.filetable.model.FileTableInfo;
import com.github.xiaoyao9184.eproject.filetable.model.MappingLocalhostFileTableInfo;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by xy on 2020/1/15.
 */
public class DatabaseFileTableHandler implements FileTableHandler {

    @Autowired
    @Qualifier("databaseFileTableHandlerRepository")
    private AbstractFileTableRepository<? extends AbstractFileTable> abstractFileTableRepository;

    @Autowired
    private BaseFileTableProperties attachFileTableProperties;

    @Autowired
    private TableNameProvider tableNameProvider;

    @Override
    public FileInfo create(MultipartFile multipartFile, URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public FileInfo create(File file, URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public FileInfo create(InputStream stream, URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public FileInfo create(byte[] bytes, URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public FileInfo create(URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant save File");
    }

    @Override
    public File readFile(URI uri) throws Exception {
        throw new UnsupportedOperationException("FileTable database cant get File, but you can get byte array of stream");
    }

    @Override
    public InputStream readStream(URI uri) throws Exception {
        URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                .path(tableNameProvider.provide())
                .path(uri.getPath())
                .build()
                .toUri();
        return abstractFileTableRepository.getBlobByPath(fileNamespacePathUri.getPath())
                .getBinaryStream();
    }

    @Override
    public byte[] readBytes(URI uri) throws Exception {
        URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                .path(tableNameProvider.provide())
                .path(uri.getPath())
                .build()
                .toUri();
        String fileNamespacePath = "\\" +
                fileNamespacePathUri.getPath().replace("/","\\");
        return abstractFileTableRepository.getBytesByPath(fileNamespacePath);
    }

    @Override
    public FileInfo read(URI uri) throws Exception {
        URI base = UriComponentsBuilder.fromUriString(
                    "smb:" +
                    attachFileTableProperties.getRootPath().replace("\\","/")
                )
                .build()
                .toUri();
        URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                .path(tableNameProvider.provide())
                .path(uri.getPath())
                .build()
                .toUri();
        String fileNamespacePath = "\\" +
                fileNamespacePathUri.getPath().replace("/","\\");

        AbstractFileTable aft = abstractFileTableRepository.getByPath(fileNamespacePath);


        MappingLocalhostFileTableInfo info = new MappingLocalhostFileTableInfo();
        info.setName(aft.getName());
        info.setPath(uri.toString());
        info.setParentPath(aft.getFile_namespace_path().replace(aft.getName(),""));

        info.setSize(aft.getCached_file_size());
        info.setCreationTime(aft.getCreation_time());

        info.setServerName(base.getHost());
        info.setInstance(attachFileTableProperties.getInstance());
        info.setDatabase(attachFileTableProperties.getDatabase());

        return info;
    }

    /**
     * Note: cant remove dir include files by FK of [parent_path_locator] column
     * @param uri
     * @return
     * @throws Exception
     */
    @Override
    public boolean delete(URI uri) throws Exception {
        URI fileNamespacePathUri = UriComponentsBuilder.newInstance()
                .path(tableNameProvider.provide())
                .path(uri.getPath())
                .build()
                .toUri();
        String fileNamespacePath = "\\" +
                fileNamespacePathUri.getPath().replace("/","\\");
        return abstractFileTableRepository.deleteByPath(fileNamespacePath) == 1;
    }

    @Override
    public List<FileInfo> readChild(URI uri) throws Exception {
        //use root because maybe servername use ip
        //and SQLServer use hostname
        URI base = UriComponentsBuilder.fromUriString(
                "smb:" + attachFileTableProperties.getRootPath().replace("\\","/")
        )
                .build()
                .toUri();

        if(uri.getPath().length() == 0 ||
            "/".equals(uri.getPath())){
            return abstractFileTableRepository.getChildByRootLocator()
                    .stream()
                    .map(aft -> {
                        FileTableInfo info = new FileTableInfo();
                        info.setName(uri.toString() + "/" + aft.getName());
                        info.setPath(uri.toString());
                        info.setParentPath(aft.getFile_namespace_path().replace(aft.getName(),""));

                        info.setSize(aft.getCached_file_size());
                        info.setCreationTime(aft.getCreation_time());

                        info.setServerName(base.getHost());
                        info.setInstance(attachFileTableProperties.getInstance());
                        info.setDatabase(attachFileTableProperties.getDatabase());

                        return info;
                    })
                    .collect(Collectors.toList());
        }else{
            URI fullFileNamespacePathUri = UriComponentsBuilder.fromUri(base)
                    .pathSegment(tableNameProvider.provide())
                    .path(uri.toString())
                    .build()
                    .toUri();
            String fileNamespacePath = "\\\\" +
                    fullFileNamespacePathUri.getHost() +
                    fullFileNamespacePathUri.getPath().replace("/","\\");

            //PathLocator Not included last '/'
            if(fileNamespacePath.endsWith("/")){
                fileNamespacePath = fileNamespacePath.substring(0,fileNamespacePath.length() - 1);
            }

            return abstractFileTableRepository.getChildByPathLocator(fileNamespacePath)
                    .stream()
                    .map(aft -> {
                        FileTableInfo info = new FileTableInfo();
                        info.setName(uri.toString() + "/" + aft.getName());
                        info.setPath(uri.toString());
                        info.setParentPath(aft.getFile_namespace_path().replace(aft.getName(),""));

                        info.setSize(aft.getCached_file_size());
                        info.setCreationTime(aft.getCreation_time());

                        info.setServerName(base.getHost());
                        info.setInstance(attachFileTableProperties.getInstance());
                        info.setDatabase(attachFileTableProperties.getDatabase());

                        return info;
                    })
                    .collect(Collectors.toList());
        }
    }
}
