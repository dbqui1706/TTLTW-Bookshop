package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.entities.AuditLog;
import com.google.gson.Gson;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuditLogDao extends AbstractDao<AuditLog> {
    private static final Gson gson = new Gson();

    public AuditLogDao() {
        super("audit_log");
    }

    public Long saveLog(String ipAddress, String level, String tableName,
                        String action, String beforeData, String afterData,
                        Long modifiedBy) {
        clearSQL();
        builderSQL.append("INSERT INTO audit_log (ip_address, level, table_name, ");
        builderSQL.append("action, before_data, after_data, modified_by, modified_at) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)");

        return insert(builderSQL.toString(),
                ipAddress, level, tableName, action, beforeData, afterData, modifiedBy);
    }

    @Override
    public AuditLog mapResultSetToEntity(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getLong("id"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setLevel(rs.getString("level"));
        log.setTableName(rs.getString("table_name"));
        log.setAction(rs.getString("action"));
        log.setBeforeData(rs.getString("before_data"));
        log.setAfterData(rs.getString("after_data"));
        log.setModifiedBy(rs.getLong("modified_by"));
        log.setModifiedAt(rs.getTimestamp("modified_at"));
        return log;
    }
}
