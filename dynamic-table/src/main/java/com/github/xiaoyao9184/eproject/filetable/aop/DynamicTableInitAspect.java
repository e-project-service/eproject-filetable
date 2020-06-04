package com.github.xiaoyao9184.eproject.filetable.aop;

import com.github.xiaoyao9184.eproject.filetable.core.DynamicFileTableRepositoryManager;
import com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by xy on 2020/6/1.
 */
@Aspect
public class DynamicTableInitAspect {

    private static final Logger logger = LoggerFactory.getLogger(DynamicTableInitAspect.class);

    @Autowired
    DynamicFileTableRepositoryManager manager;

    @Pointcut("execution(* com.github.xiaoyao9184.eproject.filetable.core.FileTableNameProvider.provide()) && args()")
    public void provide(){}

    @AfterReturning(
            returning = "name",
            pointcut = "provide() && target(provider)",
            argNames = "joinPoint,name,provider")
    public void provideAfterReturning(
            JoinPoint joinPoint,
            String name,
            FileTableNameProvider provider) throws Throwable {
        if(!manager.isInitialized(name)){
            logger.info("Will init dynamic table {}.", name);
            manager.getAndInitIfNeed(name);
        }
    }

}
