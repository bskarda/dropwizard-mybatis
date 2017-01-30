package com.cvent.dropwizard.mybatis.typehandlers;

import java.util.UUID;
import java.util.function.Function;

import org.apache.ibatis.type.MappedTypes;

/**
 * Add this type handler to your MyBatis SqlSessionFactory's configuration like so:
 *
 * sessionFactory.getConfiguration()
 *               .getTypeHandlerRegistry()
 *               .register(UUID.class, UUIDTypeHandler);
 *
 * Or to use with default null-value
 * sessionFactory.getConfiguration()
 *               .getTypeHandlerRegistry()
 *               .register(UUID.class, new UUIDTypeHandler(new UUID(0L, 0L));
 *
 * When MyBatis encounters a UUID property during the mapping process, it will use this
 * class to convert the result from the DB to a UUID. Because JDBC has no knowledge
 * of proprietary DB types like uniqueidentifier in MSSQL, the type is set to NVARCHAR,
 * so that when the preparedStatement's params are set they can be converted back to a
 * jdbcType from UUID.
 */

@MappedTypes(UUID.class)
public class UUIDTypeHandler extends AbstractTypeHandler<UUID> {

    public UUIDTypeHandler() {
        this(null);
    }

    public UUIDTypeHandler(UUID nullValue) {
        this(UUID::fromString, nullValue);
    }

    protected UUIDTypeHandler(Function<String, UUID> format, UUID nullValue) {
        super(format, nullValue);
    }

}
