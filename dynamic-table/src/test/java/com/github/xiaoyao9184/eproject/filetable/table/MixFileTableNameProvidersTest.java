package com.github.xiaoyao9184.eproject.filetable.table;

import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import com.github.xiaoyao9184.eproject.filetable.model.Named;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.ExclusivelyTypeIfInWhiteListStrategy;
import com.github.xiaoyao9184.eproject.filetable.model.TableNameProviders;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.AllWithJoinStrategy;
import com.github.xiaoyao9184.eproject.filetable.table.strategy.MixFileTableNameStrategy;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;

public class MixFileTableNameProvidersTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @MockBean
    MixFileTableNameProvider mixFileTableNameProvider;

    public static class TestUsernameFileTableNameProvider implements FileTableNameProvider, Named {

        @Override
        public String provide() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }
    }

    public static class TestClientIdFileTableNameProvider implements FileTableNameProvider, Named {

        @Override
        public String provide() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }
    }

    public static class TestZoneFileTableNameProvider implements FileTableNameProvider, Named {

        @Override
        public String provide() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }
    }

    @Test
    public void provide_join() {
        List<MixFileTableNameStrategy> strategyList = Arrays.asList(
                new AllWithJoinStrategy()
        );
        List<TableNameProviders> tableNameProvidersList = Arrays.asList(
                TableNameProviders.client_id,
                TableNameProviders.username
        );
        TestUsernameFileTableNameProvider username = Mockito.mock(TestUsernameFileTableNameProvider.class);
        TestClientIdFileTableNameProvider client = Mockito.mock(TestClientIdFileTableNameProvider.class);

        doReturn(TableNameProviders.client_id.name()).when(client).name();
        doReturn(TableNameProviders.username.name()).when(username).name();

        doReturn("client1").when(client).provide();
        doReturn("username1").when(username).provide();

        List<FileTableNameProvider> fileTableNameProviderList = Arrays.asList(
                username,
                client
        );

        MixFileTableNameProvider mixFileTableNameProvider = new MixFileTableNameProvider(
                strategyList,
                tableNameProvidersList,
                fileTableNameProviderList);


        String name = mixFileTableNameProvider.provide();

        Assert.assertEquals(name,"client1_username1");
    }

    @Test
    public void provide_exclusively() {
        List<MixFileTableNameStrategy> strategyList = Arrays.asList(
                new ExclusivelyTypeIfInWhiteListStrategy(
                        TestZoneFileTableNameProvider.class,
                        -1,
                        Arrays.asList("zone1")
                ),
                new ExclusivelyTypeIfInWhiteListStrategy(
                        TestClientIdFileTableNameProvider.class,
                        0,
                        Arrays.asList("client1")
                ),
                new AllWithJoinStrategy()
        );
        List<TableNameProviders> tableNameProvidersList = Arrays.asList(
                TableNameProviders.zone,
                TableNameProviders.client_id,
                TableNameProviders.username
        );
        TestZoneFileTableNameProvider zone = Mockito.mock(TestZoneFileTableNameProvider.class);
        TestClientIdFileTableNameProvider client = Mockito.mock(TestClientIdFileTableNameProvider.class);
        TestUsernameFileTableNameProvider username = Mockito.mock(TestUsernameFileTableNameProvider.class);

        doReturn(TableNameProviders.zone.name()).when(zone).name();
        doReturn(TableNameProviders.client_id.name()).when(client).name();
        doReturn(TableNameProviders.username.name()).when(username).name();

        doReturn("zone1").when(zone).provide();
        doReturn("client1").when(client).provide();
        doReturn("username1").when(username).provide();

        List<FileTableNameProvider> fileTableNameProviderList = Arrays.asList(
                zone,
                username,
                client
        );

        MixFileTableNameProvider mixFileTableNameProvider = new MixFileTableNameProvider(
                strategyList,
                tableNameProvidersList,
                fileTableNameProviderList);


        String name = mixFileTableNameProvider.provide();

        Assert.assertEquals(name,"zone1");
    }
}