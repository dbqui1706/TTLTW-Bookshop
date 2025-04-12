package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IRoleDao;
import com.example.bookshopwebapplication.dao.mapper.RoleMapper;
import com.example.bookshopwebapplication.entities.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class RoleDao extends AbstractDao<Role> implements IRoleDao {
    public RoleDao() {
        super("roles");
    }

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM roles";
        return query(sql, new RoleMapper());
    }

    @Override
    public Optional<Role> findById(Long id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        return getById(sql, new RoleMapper(), id);
    }

    @Override
    public Optional<Role> findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        List<Role> roles = query(sql, new RoleMapper(), name);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }

    @Override
    public Long save(Role role) {
        String sql = "INSERT INTO roles (name, description, is_system) VALUES (?, ?, ?)";
        return insert(sql, role.getName(), role.getDescription(), role.isSystem() ? 1 : 0);
    }

    @Override
    public boolean update(Role role) {
        String sql = "UPDATE roles SET name = ?, description = ?, is_system = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        update(sql, role.getName(), role.getDescription(), role.isSystem() ? 1 : 0, role.getId());
        return true;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM roles WHERE id = ?";
        delete(sql, id);
        return true;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM roles WHERE id = ?";
        return count(sql, id) > 0;
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM roles WHERE name = ?";
        return count(sql, name) > 0;
    }

    @Override
    public boolean existsByNameExcludingId(String name, Long id) {
        String sql = "SELECT COUNT(*) FROM roles WHERE name = ? AND id <> ?";
        return count(sql, name, id) > 0;
    }

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM roles";
        return count(sql);
    }

    @Override
    public List<Role> findWithPaginationAndSearch(int start, int length, String orderColumn, String orderDirection, String searchValue) {
        List<Role> roles = new LinkedList<>();
        // Map tên cột từ DataTable sang tên cột trong DB
        Map<String, String> columnMap = new HashMap<>();
        columnMap.put("id", "id");
        columnMap.put("name", "name");
        columnMap.put("description", "description");
        columnMap.put("system", "is_system");
        columnMap.put("created_at", "created_at");

        // Đảm bảo tên cột hợp lệ
        String dbColumn = columnMap.getOrDefault(orderColumn, "id");

        // Đảm bảo hướng sắp xếp hợp lệ
        String direction = "ASC".equalsIgnoreCase(orderDirection) ? "ASC" : "DESC";

        StringBuilder sql = new StringBuilder("SELECT * FROM roles");

        // Thêm điều kiện tìm kiếm
        List<Object> params = new ArrayList<>();
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql.append(" WHERE (name LIKE ? OR description LIKE ?)");
            String searchPattern = "%" + searchValue + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Thêm sắp xếp
        sql.append(" ORDER BY ").append(dbColumn).append(" ").append(direction);

        // Thêm phân trang
        if (length > 0) {
            sql.append(" LIMIT ").append(start).append(", ").append(length);
        }
        // Thực hiện truy vấn
        roles = query(sql.toString(), new RoleMapper(), params.toArray());
        return roles.isEmpty() ? new LinkedList<>() : roles;
    }

    @Override
    public int countWithSearch(String searchValue) {
        return 0;
    }

    @Override
    public Role mapResultSetToEntity(ResultSet resultSet) throws SQLException {
        return Role.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .isSystem(resultSet.getBoolean("is_system"))
                .createdAt(resultSet.getTimestamp("created_at"))
                .updatedAt(resultSet.getTimestamp("updated_at"))
                .build();
    }
}
