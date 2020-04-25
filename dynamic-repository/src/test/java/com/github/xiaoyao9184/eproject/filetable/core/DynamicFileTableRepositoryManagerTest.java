package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import net.bytebuddy.description.type.TypeDefinition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by xy on 2020/4/19.
 */
public class DynamicFileTableRepositoryManagerTest {

    DynamicFileTableRepositoryManager manager;

    @MockBean
    JpaRepositoryFactory jpaRepositoryFactory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        manager = new DynamicFileTableRepositoryManager(jpaRepositoryFactory) {
            @Override
            public List<Annotation> expandEntity(String entityName) {
                return Collections.emptyList();
            }

            @Override
            public List<TypeDefinition> expandRepository(Class<?> entityClass) {
                return Collections.emptyList();
            }

            @Override
            public void initEntity(Class<? extends AbstractFileTable> entity) throws Exception {

            }

            @Override
            public void interceptRepositoryInit(AbstractFileTableRepository<AbstractFileTable, String> repository) {

            }
        };
    }

    @Test
    public void test_createEntityClass(){
        Class<?> clazz = manager.createEntityClass("UnitTest");
        assertEquals(clazz.getName(), "com.github.xiaoyao9184.eproject.filetable.entity.UnitTest");

        Entity entity = clazz.getAnnotation(Entity.class);
        assertEquals(entity.name(), "unit_test");
        Table table = clazz.getAnnotation(Table.class);
        assertEquals(table.name(), "unit_test");
    }

    @Test
    public void test_createRepositoryClass(){
        Class<?> entityClass = manager.createEntityClass("UnitTest");
        Class<?> clazz = manager.createRepositoryClass(entityClass);
        assertEquals(clazz.getName(), "com.github.xiaoyao9184.eproject.filetable.repository.UnitTestRepository");
    }

}
