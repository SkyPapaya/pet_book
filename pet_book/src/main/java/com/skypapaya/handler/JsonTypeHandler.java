package com.skypapaya.handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.*;
import java.util.List;

public class JsonTypeHandler extends BaseTypeHandler<List<String>> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, mapper.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("JSON writing error", e);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private List<String> parse(String json) {
        try {
            if (json == null || json.isEmpty()) return null;
            return mapper.readValue(json, List.class);
        } catch (Exception e) {
            return null;
        }
    }
}