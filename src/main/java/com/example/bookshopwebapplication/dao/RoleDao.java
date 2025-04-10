package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IRoleDao;
import com.example.bookshopwebapplication.dao.mapper.RoleMapper;
import com.example.bookshopwebapplication.entities.Role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

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
