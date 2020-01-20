package com.github.xiaoyao9184.eproject.filetable;

import com.github.xiaoyao9184.eproject.filetable.entity.SampleFileTable;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xy on 2019/6/17.
 */
@SpringBootApplication()
public class CustomConversionConfiguration extends AbstractR2dbcConfiguration {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Override
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new SampleFileTableReadConverter());
        return new R2dbcCustomConversions(getStoreConversions(), converterList);
    }

    @ReadingConverter
    public static class SampleFileTableReadConverter implements Converter<Row, SampleFileTable> {

        @Override
        public SampleFileTable convert(Row source) {
            SampleFileTable ft = new SampleFileTable();
            ft.setStream_id(source.get("stream_id",String.class));
            ft.setName(source.get("name",String.class));
            ft.setFile_type(source.get("file_type",String.class));
            ft.setCached_file_size(source.get("cached_file_size",Long.class));
            ft.setCreation_time(source.get("creation_time", OffsetDateTime.class));
            ft.setLast_write_time(source.get("last_write_time",OffsetDateTime.class));
            ft.setLast_access_time(source.get("last_access_time",OffsetDateTime.class));
            ft.setIs_directory(source.get("is_directory",Boolean.class));
            ft.setIs_offline(source.get("is_offline",Boolean.class));
            ft.setIs_hidden(source.get("is_hidden",Boolean.class));
            ft.setIs_readonly(source.get("is_readonly",Boolean.class));
            ft.setIs_archive(source.get("is_archive",Boolean.class));
            ft.setIs_system(source.get("is_system",Boolean.class));
            ft.setIs_temporary(source.get("is_temporary",Boolean.class));
            ft.setRoot(source.get("root",String.class));
            ft.setLevel(source.get("level",Integer.class));
            ft.setFile_namespace_path(source.get("file_namespace_path",String.class));
            ft.setPath_name(source.get("path_name",String.class));
            return ft;
        }
    }
}
