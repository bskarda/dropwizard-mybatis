package com.cvent.dropwizard.mybatis.mappers;

import org.apache.ibatis.annotations.Select;

/**
 * Created with IntelliJ IDEA.
 * User: jwoo
 * Date: 5/9/14
 * Time: 1:18 PM
 * The author of this file does not guarantee functional code. Use at your own risk.
 */
public interface HealthCheckMapper {
    @Select ("/* MyBatis Health Check */ SELECT 1")
    int healthCheck();
}
