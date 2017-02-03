package com.cvent.dropwizard.mybatis.sessionbuilder;

import com.cvent.dropwizard.mybatis.MyBatisFactory;
import com.cvent.dropwizard.mybatis.datasource.ConfigurableLazyDataSourceFactory;
import com.cvent.pangaea.MultiEnvAware;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.setup.Environment;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.SqlSessionFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the base SqlSessionFactoryProvider class to manage SQL session initialization for all environments specified
 * in the configuration on startup. It also manages new session creation during runtime for new environments not
 * available in the existing session factories collection.
 *
 * @author Brent Ryan, Nikhil Bhagwat, zhuang
 */
public final class SqlSessionFactoryProvider {

    private final MultiEnvAware<SqlSessionFactory> sessionFactories;
    private final MultiEnvAware<ConfigurableLazyDataSourceFactory> dataSourceFactories;
    private final Environment dropwizardEnvironment;
    private final String applicationName;
    private final List<Class<?>> typeHandlers;
    private final List<Class<?>> sqlMappers;
    private final Map<Class<?>, Class<?>> typeClassToTypeHandlerClassMap;
    private final Map<String, Class<?>> typeToAliasClassMap;
    private final ObjectFactory objectFactory;
//    //private final MybatisConfigurationSettings mybatisConfigurationSettings;
    private final Map<String, Object> mybatisConfigurationSettings;

    /**
     * Create a new provider instance
     * 
     * @param dropwizardEnvironment The dropwizard environment object
     * @param applicationName       The application name
     * @param dataSourceFactories   The MultiEnvAware datasource list used to create session factories from
     * @param typeHandlers          The list of type handlers to configure with each session factory
     * @param sqlMappers            The list of sql mappers to configure with each session factory
     */
    private SqlSessionFactoryProvider(Environment dropwizardEnvironment,
                                      String applicationName,
                                      MultiEnvAware<ConfigurableLazyDataSourceFactory> dataSourceFactories,
                                      List<Class<?>> typeHandlers,
                                      List<Class<?>> sqlMappers,
                                      Map<Class<?>, Class<?>> typeClassToTypeHandlerClassMap,
                                      Map<String, Class<?>> typeToAliasClassMap,
                                      ObjectFactory objectFactory,
                                      Map<String, Object> mybatisConfigurationSettingsMap) {
        this.dropwizardEnvironment = dropwizardEnvironment;
        this.applicationName = applicationName;
        this.dataSourceFactories = dataSourceFactories;
        this.typeHandlers = typeHandlers;
        this.sqlMappers = sqlMappers;
        this.typeClassToTypeHandlerClassMap = typeClassToTypeHandlerClassMap;
        this.typeToAliasClassMap = typeToAliasClassMap;
        this.objectFactory = objectFactory;
        this.mybatisConfigurationSettings = mybatisConfigurationSettingsMap;
        sessionFactories = dataSourceFactories.convert((env, dataSource) -> buildSessionFactory(dataSource, env));
        //this.mybatisConfigurationSettings = new MybatisConfigurationSettings();
    }

    /**
     * A simple builder allowing us to customize the underlying session factory provider
     */
    public static class Builder {

        private final List<Class<?>> typeHandlers = new ArrayList<>();
        private final List<Class<?>> sqlMappers = new ArrayList<>();
        private final Map<Class<?>, Class<?>> typeClassToTypeHandlerClassMap = new HashMap<>();
        private final Map<String, Class<?>> typeToAliasClassMap = new HashMap<>();
        private final MultiEnvAware<ConfigurableLazyDataSourceFactory> dataSourceFactories;
        private final Environment dropwizardEnvironment;
        private final String applicationName;
        private ObjectFactory objectFactory;
        private final Map<String, Object> mybatisConfigurationSettingsMap = new HashMap<>();

        /**
         * A new Builder
         * 
         * @param dropwizardEnvironment The dropwizard environment object
         * @param applicationName The application name
         * @param dataSourceFactories The MultiEnvAware datasource list used to create session factories from
         */
        public Builder(Environment dropwizardEnvironment, String applicationName,
                MultiEnvAware<ConfigurableLazyDataSourceFactory> dataSourceFactories) {
            this.dropwizardEnvironment = dropwizardEnvironment;
            this.applicationName = applicationName;
            this.dataSourceFactories = dataSourceFactories;
        }

        /**
         * Register a new type handler to be used with this sql session provider
         * 
         * @param typeHandler
         * @return 
         */
        public Builder register(Class<?> typeHandler) {
            typeHandlers.add(typeHandler);
            return this;
        }

        /**
         * Register a new type and type handler association to be used with this sql session provider
         *
         * @param javaTypeClass
         * @param typeHandlerClass
         * @return
         */
        public Builder register(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
            typeClassToTypeHandlerClassMap.put(javaTypeClass, typeHandlerClass);
            return this;
        }

        /**
         * Register a new alias to be used with this sql session provider
         *
         * @param alias
         * @param value
         * @return
         */
        public Builder registerAlias(String alias, Class<?> value) {
            typeToAliasClassMap.put(alias, value);
            return this;
        }

        /**
         * Register a new sql mapper to be used with this sql session provider
         *
         * @param sqlMapper
         * @return 
         */
        public Builder addMapper(Class<?> sqlMapper) {
            sqlMappers.add(sqlMapper);
            return this;
        }

        /**
         * Add an object factory to the builder
         * 
         * @param factory
         * @return 
         */
        public Builder objectFactory(ObjectFactory factory) {
            this.objectFactory = factory;
            return this;
        }

        /**
         * Add a new MyBatis Configuration Setting.
         * @param configName
         * @param configSettingObject
         * @return
         */
        public Builder addConfigurationSettings(String configName, Object configSettingObject) {
            this.mybatisConfigurationSettingsMap.put(configName, configSettingObject);
            return this;
        }

        /**
         * Create a new SqlSessionFactoryProvider based on the attributes that have been added to this builder
         * 
         * @return a new instance of SqlSessionFactoryProvider
         */
        public SqlSessionFactoryProvider build() {
            return new SqlSessionFactoryProvider(dropwizardEnvironment,
                    applicationName,
                    dataSourceFactories,
                    typeHandlers,
                    sqlMappers,
                    typeClassToTypeHandlerClassMap,
                    typeToAliasClassMap,
                    objectFactory,
                    mybatisConfigurationSettingsMap);
        }

    }

    /**
     * Build a new sql session factory.  This method is NOT threadsafe so this internal class must take care to
     * synchronize on sessionFactories.
     * 
     * @param dataSource        The datasource to use for constructing the session factory from
     * @param environmentName   The environment name to use
     * @return  A new SqlSessionFactory
     */
    private SqlSessionFactory buildSessionFactory(ConfigurableLazyDataSourceFactory dataSource,
            String environmentName) {
        String dataSourceName = String.format("%s-%s-sql", applicationName, environmentName);
        SqlSessionFactory sessionFactory;

        try {
            ManagedDataSource ds = dataSource.build(dropwizardEnvironment.metrics(), dataSourceName);
            sessionFactory = new MyBatisFactory().build(dropwizardEnvironment, dataSource, ds, dataSourceName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<Class<?>, Class<?>> typeClassToTypeHandlerClassEntry :
                typeClassToTypeHandlerClassMap.entrySet()) {
            sessionFactory.getConfiguration().getTypeHandlerRegistry().register(
                    typeClassToTypeHandlerClassEntry.getKey(),
                    typeClassToTypeHandlerClassEntry.getValue());
        }

        for (Map.Entry<String, Class<?>> typeToAliasClassEntry :
                typeToAliasClassMap.entrySet()) {
            sessionFactory.getConfiguration().getTypeAliasRegistry().registerAlias(
                    typeToAliasClassEntry.getKey(),
                    typeToAliasClassEntry.getValue());
        }

        for (Class<?> typeHandler : typeHandlers) {
            sessionFactory.getConfiguration().getTypeHandlerRegistry().register(typeHandler);
        }

        for (Class<?> sqlMapper : sqlMappers) {
            sessionFactory.getConfiguration().addMapper(sqlMapper);
        }

        if (objectFactory != null) {
            sessionFactory.getConfiguration().setObjectFactory(objectFactory);
        }

        //Only add to it after it's be initialized.  This is used mainly for "templates"
        if (sessionFactories != null) {
            sessionFactories.put(environmentName, sessionFactory);
        }

        mybatisConfigurationSettings.forEach((settingName, value) -> {
            try {
                PropertyUtils.setSimpleProperty(sessionFactory.getConfiguration(), settingName, value);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        return sessionFactory;
    }

    /**
     * Get SQL session by environment name, create a new session if requested environment is not found
     *
     * @param environmentName
     * @return SqlSessionFactory
     */
    public SqlSessionFactory getSqlSessionFactory(String environmentName) {
        if (StringUtils.isBlank(environmentName) || sessionFactories.containsKey(environmentName)) {
            return sessionFactories.get(environmentName);
        }

        //implement double check locking so that we're protected against multiple threads trying to get a session
        //factory while avoiding the possibility of duplication session factories and not hurting performance for 99% of
        //the calls.
        synchronized (this) {
            if (StringUtils.isBlank(environmentName) || sessionFactories.containsKey(environmentName)) {
                return sessionFactories.get(environmentName);
            } else {
                return buildSessionFactory(dataSourceFactories.get(environmentName), environmentName);
            }
        }
    }

}
