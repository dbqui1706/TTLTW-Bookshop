package com.example.bookshopwebapplication.http.response_admin.invetory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Builder
public class InventoryMovementData {
    private Timestamp periodDate;
    private String periodLabel;
    private int importQuantity;
    private double importValue;
    private int exportQuantity;
    private double exportValue;
    private int netQuantityChange;
    private double netValueChange;

    public InventoryMovementData(ResultSet resultSet) throws SQLException {
        this.setPeriodDate(resultSet.getTimestamp("date_point"));
        this.setPeriodLabel(resultSet.getString("date_label"));
        this.setImportQuantity(resultSet.getInt("import_quantity"));
        this.setImportValue(resultSet.getDouble("import_value"));
        this.setExportQuantity(resultSet.getInt("export_quantity"));
        this.setExportValue(resultSet.getDouble("export_value"));
        this.setNetQuantityChange(resultSet.getInt("net_quantity"));
        this.setNetValueChange(resultSet.getDouble("net_value"));
    }
}
