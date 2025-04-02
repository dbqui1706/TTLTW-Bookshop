package com.example.bookshopwebapplication.dao.mapper;

import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.entities.UserAddress;

import javax.swing.tree.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAddressMapper implements IRowMapper<UserAddress> {
    @Override
    public UserAddress mapRow(ResultSet resultSet) throws SQLException {
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
