package com.cvent.dropwizard.mybatis.typehandlers;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

/**
 * SQL database saves all dates in EST time, (at least for Events + Surveys), however, on the java boxes that mybatis
 * runs on, the dates are in UTC time. When mybatis sends values to the database, it sends all the dates in just plain
 * string format. This handler will convert all the dates stored to EST to remain consistent with the existing data.
 *
 * @author atran
 */
@MappedJdbcTypes(JdbcType.DATE)
@MappedTypes(Date.class)
public class ESTDateTypeHandler extends BaseTypeHandler {

    private static final String DATE_FORMAT_WITH_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_FORMAT_WITHOUT_Z = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_FORMAT_WITHOUT_TIME = "yyyy-MM-dd";
    private static final String TIMEZONE = "US/Eastern";
    private static final String TIMEZONE_UTC = "UTC";

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i,
            Object o, JdbcType jdbcType) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_WITH_Z);
        sdf.setTimeZone(TimeZone.getTimeZone(TIMEZONE));
        if (o instanceof Date) {
            preparedStatement.setObject(i, sdf.format((Date) o), Types.TIMESTAMP);
        } else {
            throw new IllegalArgumentException("Object value is not a valid Date: " + o.toString());
        }
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName)
            throws SQLException {
        try {
            return parseDate(rs.getString(columnName));
        } catch (ParseException ex) {
            throw new RuntimeException("Illegal date retrieved from DB: " + ex);
        }
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex)
            throws SQLException {
        try {
            return parseDate(rs.getString(columnIndex));
        } catch (ParseException ex) {
            throw new RuntimeException("Illegal date retrieved from DB: " + ex);
        }
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex)
            throws SQLException {
        try {
            return parseDate(cs.getString(columnIndex));
        } catch (ParseException ex) {
            throw new RuntimeException("Illegal date retrieved from DB: " + ex);
        }
    }

    private Date parseDate(String dateString) throws ParseException {
        String dateFormatToUse;
        String timezoneToUse = TIMEZONE;
        
        if (StringUtils.isEmpty(dateString)) {
            return null;
        } else if (dateString.indexOf('Z') > -1) {
            dateFormatToUse = DATE_FORMAT_WITH_Z;
            timezoneToUse = TIMEZONE_UTC;
        } else if (dateString.indexOf('T') > -1) {
            dateFormatToUse = DATE_FORMAT_WITHOUT_Z;
        } else {
            dateFormatToUse = DATE_FORMAT_WITHOUT_TIME;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatToUse);
        sdf.setTimeZone(TimeZone.getTimeZone(timezoneToUse));
        return sdf.parse(dateString);
    }
}
