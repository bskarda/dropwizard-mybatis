package com.cvent.dropwizard.mybatis.sessionbuilder;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * An object that can produce SqlSessionFactory instances
 */
public interface SqlSessionFactoryProvider {

    /**
     * Use an environment name to produce an instance of SqlSessionFactory
     * @param environmentName
     * @return
     */
    SqlSessionFactory getSqlSessionFactory(String environmentName);
}
