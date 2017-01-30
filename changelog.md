# Change Log

## [3.1.3]

- Add ability to set ObjectFactory on SqlSessionFactoryProvider

## [3.1.2]

 - Fix NullPointerException in SqlSessionFactoryProvider when using `null` environment name with default environment 
 configuration

## [3.1.1]

 - Bump to use latest pangaea lib to fix issues with serialization of configurations

## [3.1.0]

 - Add ImmutablesObjectFactory to use when using MyBatis mappers with types created using Immutables library.  When using Immutables, the constructor tags should be used in the resultMaps:
 ```xml
 <resultMap id="immutableEntityResultMap" type="ImmutableEntity">
   <constructor>
     <idArg column="id" javaType="int" />
     <arg column="name" javaType="String" />
   </constructor>
 </resultMap>
 ```

## [3.0.5]

 - Added ConfigurableLazyDataSourceFactory.  ConfigurableLazyDataSourceFactory is an extension of io.dropwizard.db.DataSourceFactory, which is what Cvent services are using to make JDBC connections to our SQL Server instances.  The base DataSourceFactory builds a ManagedDataSource object, which immediately tries to connect to its configured database when it is registered during a service's startup.  This behavior was good enough until we started adding multi-environment support to our services.  Each instance of a multi-environment service needs to be able to connect to many individual databases.  With the original DataSourceFactory, a multi-environment service makes all of these connections at the same time (during service start), which causes a decent delay before the service can accept requests.  If one of these connections fails, the service will fail to run.

   ConfigurableLazyDataSourceFactory aims to solve this by deferring database connection until a request requiring the particular connection occurs.  This means the service starts up much more quickly, which is nice.  It also means any connection-related exceptions are thrown within the context of an individual request.  Because of this, a single unreachable database won't crash the whole service.  Requests requiring other databases won't be affected by one broken or misconfigured database.  Lazy database connections are kept open after they are first established.

   ConfigurableLazyDataSourceFactory introduces a configuration parameter named "isLazy".  By default (if not provided), isLazy is false.  When isLazy is false, ConfigurableLazyDataSourceFactory behaves exactly like DataSourceFactory.  The lazy behavior described in the paragraph above occurs only when isLazy is set to true.  This "opt-in" behavior is by design; in production we want to know immediately (at service startup) that a database connection is failing so we wouldn't want to accidentally enable these lazy connections.

   In order to use the deferred DB connections with the new ConfigurableLazyDataSourceFactory type, YAML database configurations should be updated to look similar to this (borrowed from rfp-service):

   ```YAML
   default_database_configuration: &default
       driverClass: com.microsoft.sqlserver.jdbc.SQLServerDriver
       validationQuery: SELECT 1
       minSize: 1
       maxSize: 50
       maxWaitForConnection: 30s
       checkConnectionWhileIdle: true
       defaultTransactionIsolation: READ_UNCOMMITTED
       isLazy: true #      <--- The important part
       properties:
           applicationName: "rfp-service"
   ```