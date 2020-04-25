package com.github.xiaoyao9184.eproject.filetable.core;

import com.github.xiaoyao9184.eproject.filetable.core.bytebuddy.RuntimeEntityImpl;
import com.github.xiaoyao9184.eproject.filetable.core.bytebuddy.RuntimeTableImpl;
import com.github.xiaoyao9184.eproject.filetable.entity.AbstractFileTable;
import com.github.xiaoyao9184.eproject.filetable.repository.AbstractFileTableRepository;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.type.TypeDescription;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by xy on 2020/4/19.
 */
public class ByteBuddyCreateFileTableRepositoryTest {

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateEntityClass() {
        Class<?> clazz = new ByteBuddy()
                .subclass(AbstractFileTable.class)
                .name("com.github.xiaoyao9184.eproject.filetable.core.UnitTest")
                .annotateType(new RuntimeEntityImpl("unit_test"))
                .annotateType(new RuntimeTableImpl("unit_test"))
                .make()
                .load(AbstractFileTable.class.getClassLoader())
                .getLoaded();

        assertEquals(clazz.getName(), "com.github.xiaoyao9184.eproject.filetable.core.UnitTest");

        Entity entity = clazz.getAnnotation(Entity.class);
        assertEquals(entity.name(), "unit_test");
        Table table = clazz.getAnnotation(Table.class);
        assertEquals(table.name(), "unit_test");
    }

    @Test
    public void testCreateRepositoryClass() {
        Class<? extends AbstractFileTable> entityClass = new ByteBuddy()
                .subclass(AbstractFileTable.class)
                .name("com.github.xiaoyao9184.eproject.filetable.core.UnitTest")
                .annotateType(new RuntimeEntityImpl("unit_test"))
                .annotateType(new RuntimeTableImpl("unit_test"))
                .make()
                .load(AbstractFileTable.class.getClassLoader())
                .getLoaded();

        TypeDescription.Generic t = TypeDescription.Generic.Builder.parameterizedType(
                AbstractFileTableRepository.class,
                entityClass,
                String.class)
                .build();

        Class<?> clazz = new ByteBuddy()
                .subclass(t)
                .name("com.github.xiaoyao9184.eproject.filetable.repository.UnitTestRepository")
                .make()
                .load(entityClass.getClassLoader())
                .getLoaded();

        assertEquals(clazz.getName(), "com.github.xiaoyao9184.eproject.filetable.repository.UnitTestRepository");
        Type gsc = clazz.getGenericInterfaces()[0];
        assertTrue(gsc instanceof ParameterizedType);
        ParameterizedType pt = (ParameterizedType) gsc;
        assertEquals(pt.getRawType(), AbstractFileTableRepository.class);
        assertEquals(pt.getActualTypeArguments()[0], entityClass);
        assertEquals(pt.getActualTypeArguments()[1], String.class);

        Table table = entityClass.getAnnotation(Table.class);
        assertEquals(table.name(), "unit_test");
    }

}
