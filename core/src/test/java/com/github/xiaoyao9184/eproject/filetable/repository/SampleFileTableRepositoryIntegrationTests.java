package com.github.xiaoyao9184.eproject.filetable.repository;

import com.github.xiaoyao9184.eproject.filetable.CustomConversionConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.io.IOException;

/**
 * Created by xy on 2019/6/18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomConversionConfiguration.class)
public class SampleFileTableRepositoryIntegrationTests {

	@Autowired SampleFileTableRepository repository;
	@Autowired DatabaseClient database;

	@Value("${eproject.filetable.share.path}")
	String path;

	@Test
	public void getChildByPath() throws IOException {
		repository.getChildByPath(path + "\\sample_filetable\\new_dir") //
				.as(StepVerifier::create)
				.assertNext(fs -> {
					fs.getId();
				})
				.verifyComplete();
	}

	@Test
	public void getChildByRoot() throws IOException {
		repository.getChildByRoot() //
				.as(StepVerifier::create) //
				.assertNext(fs -> {
					fs.getId();
				})
				.assertNext(fs -> {
					fs.getId();
				})
				.assertNext(fs -> {
					fs.getId();
				})
				.verifyComplete();
	}

//	@Test
//	public void stream() throws IOException {
//		repository.getFile(path + "\\sample_filetable\\new_text.txt") //
//				.as(StepVerifier::create) //
//				.assertNext(bb -> {
//					bb.arrayOffset();
//				})
//				.verifyComplete();
//	}

}
