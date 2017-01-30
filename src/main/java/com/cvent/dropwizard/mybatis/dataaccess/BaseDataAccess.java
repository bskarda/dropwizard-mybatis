package com.cvent.dropwizard.mybatis.dataaccess;

import org.apache.ibatis.session.SqlSessionFactory;

import java.util.function.Function;

/**
 * This is the base class needed for data access classes to operate
 * under the multi-environment set up.
 * @author Nikhil Bhagwat
 */
public class BaseDataAccess {
    private Function<String, SqlSessionFactory> sqlSessionFactoryFunction;

    /**
     * contructor
     * @param sqlSessionFactoryFunction
     */
    public BaseDataAccess(Function<String, SqlSessionFactory> sqlSessionFactoryFunction) {
        this.sqlSessionFactoryFunction = sqlSessionFactoryFunction;
    }

    /**
     * Gets the sql session factory by environment name
     * The behavior of .apply() will be defined by the SessionBuilderFactory class
     * @param environment
     * @return SqlSessionFactory
     */
    protected SqlSessionFactory getSessionFactory(String environment) {
        return sqlSessionFactoryFunction.apply(environment);
    }
}
