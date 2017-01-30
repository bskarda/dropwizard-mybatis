package com.cvent.dropwizard.mybatis.mappers;

import org.apache.ibatis.annotations.Select;

/**
 * A mybatis mapper for performing healthchecks
 */
public interface HealthCheckMapper {

    /**
     * Perform a healthcheck using provided select
     *
     * @return
     */
    @Select("/* MyBatis Health Check */ SELECT 1")
    int healthCheck();
}
