package com.cvent.dropwizard.mybatis;

import com.cvent.dropwizard.mybatis.datasource.ConfigurableLazyDataSourceFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by jmorley on 9/22/14.
 */
public class MyBatisConfiguration {

    @JsonProperty
    private String configFile;

    @JsonProperty
    private final ConfigurableLazyDataSourceFactory config = new ConfigurableLazyDataSourceFactory();

    @NotEmpty
    @JsonProperty
    private String environment;

    public String getConfigFile() {
        return configFile;
    }

    public ConfigurableLazyDataSourceFactory getConfig() {
        return config;
    }

    public String getEnvironment() {
        return environment;
    }
}
