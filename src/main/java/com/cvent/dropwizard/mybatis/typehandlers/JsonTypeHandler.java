package com.cvent.dropwizard.mybatis.typehandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 *
 * MyBatis TypeHandler to convert to/from json and jsonb types
 *
 * To use with Immutables you may need to map both the interface and the immutable class in the class that extends
 * this one, something like:
 * @MappedTypes({AppBuilderSettings.class, ImmutableAppBuilderSettings.class })
 * public class AppBuilderSettingsTypeHandler extends JsonTypeHandler<AppBuilderSettings> { ...
 *
 * @param <T> - type of object you are converting to/from
 */
public class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private final Class<T> clazz;

    private final ObjectMapper mapper;

    public JsonTypeHandler(Class<T> clazz, ObjectMapper objectMapper) {
        this.clazz = clazz;
        this.mapper = objectMapper;
    }

    /**
     * Convert this object to JSON
     * @param ps
     * @param i
     * @param parameter
     * @param jdbcType
     * @throws SQLException
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setObject(i, mapper.writeValueAsString(parameter), Types.OTHER);
        } catch (IOException e) {
            throw new RuntimeException("ObjectMapper could not write object", e);
        }
    }

    /**
     * Retrieve this custom Json object
     * @param rs
     * @param columnName
     * @return
     * @throws SQLException
     */
    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return convert(rs.getString(columnName));
    }

    /**
     * Retrieve this custom Json object
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return convert(rs.getString(columnIndex));
    }

    /**
     * Retrieve this custom Json object
     * @param cs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return convert(cs.getString(columnIndex));
    }


    private T convert(String json) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("ObjectMapper could not convert JSON", e);
        }
    }
}
