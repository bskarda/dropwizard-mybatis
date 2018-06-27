package com.cvent.dropwizard.mybatis.datasource;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.ManagedPooledDataSource;

import com.codahale.metrics.MetricRegistry;

/**
 * Builds lazy (connect on first usage) instance of managed pooled datasource
 */
public class ConfigurableLazyDataSourceFactory extends DataSourceFactory {

    @JsonProperty
    private boolean isLazy;

    /** {@inheritDoc} */
    @Override
    public ManagedDataSource build(MetricRegistry metricRegistry, String name) {
        ManagedDataSource managedDataSource = super.build(metricRegistry, name);

        if (isLazy) {
            if (managedDataSource instanceof ManagedPooledDataSource) {
            /* of course we can't depend on it by method protocol, but right now
             * (currently used version of dropwizard 0.7.1 as well as last currently available 0.8.1)
             * exactly this implementation will be returned by super.build(MetricRegistry mr, String name) method
             */
                ManagedPooledDataSource managedPooledDataSource = (ManagedPooledDataSource) managedDataSource;
                return new LazyManagedPooledDataSource(managedPooledDataSource.getPoolProperties(), metricRegistry);
            }

            throw new AssertionError(
                    "You can't use this DataSourceFactory implementation with your dropwizard version");
        } else {
            return managedDataSource;
        }
    }
}
