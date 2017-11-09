package com.cvent.dropwizard.mybatis.dataaccess;

import com.cvent.dropwizard.mybatis.sessionbuilder.SqlSessionFactoryProvider;
import com.cvent.pangaea.util.EnvironmentUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.function.Function;

/**
 * This is a thin generic wrapper around mybatis mappers that encapsulates SQL session management so you can just
 * invoke mapper methods (more or less) directly.
 *
 * Example:
 *
 * (initialization)
 * WrappedInvoker&lt;MyMapper&gt; dataAccess =
 *              new MybatisMapperInvoker&lt;&gt;(MyMapper.class, sqlSessionFactoryProvider);
 *
 * (business logic)
 * final IdType id = getIdFromRequest();
 * MyEntity entity = dataAccess.invoke(mapper -> mapper.getById(id));
 *
 * As you can see, this class can replace the bespoke data access classes that exist solely to wrap mapper methods. In
 * the example above, you can see that parameters to mapper methods may be passed by closure. This means you have to
 * follow the rules for closures in Java, namely that the variables being closed over must be final (or effectively
 * final).
 *
 * This class has its limits. It cannot execute multiple mapper methods within a transaction, and it can only
 * support a single mapper type. If you need transactions, or your use case is complex and requires multiple mappers,
 * you should continue to write data access wrapper classes yourself.
 *
 * @param <TMapper> The type of your mapper interface
 */
public class MyBatisMapperInvoker<TMapper> implements WrappedInvoker<TMapper> {
    private final Class<TMapper> mapperClass;
    private final SqlSessionFactoryProvider sqlSessionFactoryProvider;

    public MyBatisMapperInvoker(Class<TMapper> mapperClass,
                                SqlSessionFactoryProvider sqlSessionFactoryProvider) {
        this.mapperClass = mapperClass;
        this.sqlSessionFactoryProvider = sqlSessionFactoryProvider;
    }

    @Override
    public <TResult> TResult invoke(Function<TMapper, TResult> function) {
        String environment = EnvironmentUtil.getEnvironment();
        return invoke(function, environment);
    }

    /**
     * In case you want to eschew cvent-pangea's contextual environment and pass it yourself, this overload is
     * available.
     *
     * @param function The Function
     * @param environment The environment name as registered in the SqlSessionFactoryProvider
     * @param <TResult> The result of the Function, usually the result of a mapper method
     * @return The result of your function
     */
    public <TResult> TResult invoke(Function<TMapper, TResult> function, String environment) {
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryProvider.getSqlSessionFactory(environment);
        try (SqlSession session = sqlSessionFactory.openSession(true)) {
            TMapper mapper = session.getMapper(mapperClass);
            return function.apply(mapper);
        }
    }
}
