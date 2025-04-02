package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao.mapper.UserAddressMapper;
import com.example.bookshopwebapplication.entities.UserAddress;
import org.apache.poi.ss.formula.eval.ConcatEval;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserAddressDao extends AbstractDao<UserAddress> {
    public UserAddressDao() {
        super("user_addresses");
    }

    public Long save(UserAddress userAddress) {
        clearSQL();
        builderSQL.append("INSERT INTO bookshopdb.user_addresses " +
                "(user_id, address_type, recipient_name, phone_number, " +
                "address_line1, province_code, district_code, ward_code, " +
                "province_name, district_name, ward_name, is_default) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), userAddress.getUserId(), userAddress.getAddressType(),
                userAddress.getRecipientName(), userAddress.getPhoneNumber(),
                userAddress.getAddressLine1(), userAddress.getProvinceCode(),
                userAddress.getDistrictCode(), userAddress.getWardCode(),
                userAddress.getProvinceName(), userAddress.getDistrictName(),
                userAddress.getWardName(), userAddress.getIsDefault() ? 1 : 0);
    }

    public void update(UserAddress userAddress) {
        // Bước 1: Reset tất cả các địa chỉ khác về 0
        clearSQL();
        builderSQL.append("UPDATE bookshopdb.user_addresses SET is_default = 0 " +
                "WHERE user_id = ? AND id != ?");
        boolean resetSuccess = executeUpdateAddressDefault(builderSQL.toString(),
                userAddress.getUserId(),
                userAddress.getId());

        if (!resetSuccess) {
            return;
        }

        // Bước 2: Cập nhật địa chỉ được chọn
        clearSQL();
        builderSQL.append("UPDATE bookshopdb.user_addresses SET address_type = ?, recipient_name = ?, phone_number = ?, " +
                "address_line1 = ?, province_code = ?, district_code = ?, ward_code = ?, is_default = ?, " +
                "province_name = ?, district_name = ?, ward_name = ? WHERE id = ?");
        update(builderSQL.toString(), userAddress.getAddressType(), userAddress.getRecipientName(),
                userAddress.getPhoneNumber(), userAddress.getAddressLine1(), userAddress.getProvinceCode(),
                userAddress.getDistrictCode(), userAddress.getWardCode(), userAddress.getIsDefault() ? 1 : 0,
                userAddress.getProvinceName(), userAddress.getDistrictName(), userAddress.getWardName(),
                userAddress.getId());
    }

    public void delete(Long id) {
        clearSQL();
        builderSQL.append("DELETE FROM bookshopdb.user_addresses WHERE id = ?");
        delete(builderSQL.toString(), id);
    }

    public List<UserAddress> findByUser(Long userId) {
        clearSQL();
        builderSQL.append("SELECT * FROM bookshopdb.user_addresses WHERE user_id = ?");
        return query(builderSQL.toString(), new UserAddressMapper(), userId);
    }

    private boolean executeUpdateAddressDefault(String sql, Long userId, Long id) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql);
            statement.setLong(1, userId);
            statement.setLong(2, id);
            statement.executeUpdate();
            connection.commit();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            close(connection, statement, null);
        }
    }

    @Override
    public UserAddress mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        try {
            UserAddress userAddress = new UserAddress();
            userAddress.setId(resultSet.getLong("id"));
            userAddress.setUserId(resultSet.getLong("user_id"));
            userAddress.setAddressType(resultSet.getString("address_type"));
            userAddress.setRecipientName(resultSet.getString("recipient_name"));
            userAddress.setPhoneNumber(resultSet.getString("phone_number"));
            userAddress.setAddressLine1(resultSet.getString("address_line1"));
            userAddress.setAddressLine2(resultSet.getString("address_line2"));
            userAddress.setProvinceCode(resultSet.getInt("province_code"));
            userAddress.setDistrictCode(resultSet.getInt("district_code"));
            userAddress.setWardCode(resultSet.getInt("ward_code"));
            userAddress.setProvinceName(resultSet.getString("province_name"));
            userAddress.setDistrictName(resultSet.getString("district_name"));
            userAddress.setWardName(resultSet.getString("ward_name"));
            userAddress.setPostalCode(resultSet.getString("postal_code"));
            userAddress.setIsDefault(resultSet.getBoolean("is_default"));
            userAddress.setNotes(resultSet.getString("notes"));
            userAddress.setCreatedAt(resultSet.getTimestamp("created_at"));
            userAddress.setUpdatedAt(resultSet.getTimestamp("updated_at"));
            return userAddress;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
