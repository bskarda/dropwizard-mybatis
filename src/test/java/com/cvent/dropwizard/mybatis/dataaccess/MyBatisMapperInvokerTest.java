package com.cvent.dropwizard.mybatis.dataaccess;

import com.cvent.dropwizard.mybatis.sessionbuilder.SqlSessionFactoryProvider;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MyBatisMapperInvokerTest {
    // I love Java so much
    private final SqlSessionFactoryProvider sqlSessionFactoryProvider = mock(SqlSessionFactoryProvider.class);
    private final SqlSessionFactory sqlSessionFactory = mock(SqlSessionFactory.class);
    private final SqlSession sqlSession = mock(SqlSession.class);

    @BeforeEach
    public void setup() {
        when(sqlSessionFactoryProvider.getSqlSessionFactory(anyString()))
                .thenReturn(sqlSessionFactory);
        when(sqlSessionFactory.openSession(eq(true)))
                .thenReturn(sqlSession);
    }

    @Test
    public void invoke_FunctionReceivesObject_ObjectPassedToAndReturnedFromFunctionIsNotTamperedWith() {
        // setup
        Object subject = new Object();
        when(sqlSession.getMapper(eq(Object.class)))
                .thenReturn(subject);

        // execute
        WrappedInvoker<Object> invoker = new MyBatisMapperInvoker<>(Object.class, sqlSessionFactoryProvider);
        Object result = invoker.invoke(object -> {
            assertEquals(subject, object);
            return object;
        });

        // verify
        assertEquals(subject, result);
    }

    @Test
    public void invoke_FunctionThrowsException_SameExceptionInstanceAvailableInCaller() {
        // setup
        Object subject = new Object();
        when(sqlSession.getMapper(eq(Object.class)))
                .thenReturn(subject);

        RuntimeException exception = new RuntimeException("Hahahah");
        boolean exceptionCaught = false;

        // execute
        WrappedInvoker<Object> invoker = new MyBatisMapperInvoker<>(Object.class, sqlSessionFactoryProvider);
        try {
            invoker.invoke(object -> {
                throw exception;
            });
        } catch (RuntimeException thrown) {
            exceptionCaught = true;

            // verify
            assertEquals(exception, thrown, "The exception changed!");
        } finally {
            assertTrue(exceptionCaught, "Expected exception, didn't catch one");
        }
    }
}
