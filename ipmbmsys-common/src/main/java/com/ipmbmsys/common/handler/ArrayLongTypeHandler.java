package com.ipmbmsys.common.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ArrayLongTypeHandler extends BaseTypeHandler<Long[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Long[] parameter, JdbcType jdbcType) throws SQLException {
        String value = Arrays.stream(parameter)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        ps.setString(i, value);
    }

    @Override
    public Long[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return convertToLongArray(value);
    }

    @Override
    public Long[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return convertToLongArray(value);
    }

    @Override
    public Long[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return convertToLongArray(value);
    }

    private Long[] convertToLongArray(String value) {
        if (value == null || value.isEmpty()) {
            return new Long[0];
        }
        return Arrays.stream(value.split(","))
                .map(Long::valueOf)
                .toArray(Long[]::new);
    }
}
