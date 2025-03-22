package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IUserDao;
import com.example.bookshopwebapplication.dao.mapper.UserMapper;
import com.example.bookshopwebapplication.entities.User;
import com.example.bookshopwebapplication.http.response.user.LoginHistory;
import com.example.bookshopwebapplication.http.response.user.UserFullDetail;

import java.sql.*;
import java.util.*;

public class UserDao extends AbstractDao<User> implements IUserDao {

    public UserDao() {
        super("user");
    }

    // Phương thức để lưu thông tin người dùng mới vào cơ sở dữ liệu
    public Long save(User user) {
        // Thiết lập câu truy vấn mới
        clearSQL();
        builderSQL.append("INSERT INTO user (username, password, fullname, email, phoneNumber, ");
        builderSQL.append("gender, address, role) ");
        builderSQL.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        return insert(builderSQL.toString(), user.getUsername(), user.getPassword(), user.getFullName(),
                user.getEmail(), user.getPhoneNumber(), user.getGender(), user.getAddress(), user.getRole());
    }

    // Phương thức để cập nhật thông tin người dùng trong cơ sở dữ liệu
    public void update(User user) {
        clearSQL();
        builderSQL.append("UPDATE user SET username = ?, password = ?, fullname = ?, email = ?, ");
        builderSQL.append("phoneNumber = ?, gender = ?, address = ?, role = ? ");
        builderSQL.append("WHERE id = ?");
        update(builderSQL.toString(), user.getUsername(), user.getPassword(), user.getFullName(),
                user.getEmail(), user.getPhoneNumber(), user.getGender(), user.getAddress(), user.getRole(),
                user.getId()
        );
    }

    // Phương thức để xóa người dùng từ cơ sở dữ liệu dựa trên ID
    public void delete(Long id) {
        clearSQL();
        builderSQL.append("DELETE FROM user WHERE id = ?");
        update(builderSQL.toString(), id);
    }

    // Phương thức để lấy thông tin người dùng dựa trên ID
    public Optional<User> getById(Long id) {
        clearSQL();
        builderSQL.append("SELECT * FROM user WHERE id = ?");
        List<User> users = query(builderSQL.toString(), new UserMapper(), id);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
    }

    // Phương thức để lấy một phần danh sách người dùng từ cơ sở dữ liệu
    public List<User> getPart(Integer limit, Integer offset) {
        clearSQL();
        builderSQL.append("SELECT * FROM user LIMIT " + offset + ", " + limit);
        return getPart(builderSQL.toString(), new UserMapper());
    }

    // Phương thức để lấy một phần danh sách người dùng từ cơ sở dữ liệu với sắp xếp
    public List<User> getOrderedPart(Integer limit, Integer offset, String orderBy, String sort) {
        clearSQL();
        builderSQL.append("SELECT * FROM user ORDER BY " + orderBy + " " + sort);
        builderSQL.append(" LIMIT " + offset + ", " + limit + "");
        return super.getOrderedPart(builderSQL.toString(), new UserMapper());
    }

    // Phương thức để lấy thông tin người dùng dựa trên tên đăng nhập
    @Override
    public Optional<User> getByNameUser(String username) {
        clearSQL();
        builderSQL.append("SELECT * FROM user WHERE username = ?");
        List<User> users = query(builderSQL.toString(), new UserMapper(), username);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
    }

    // Phương thức để thay đổi mật khẩu của người dùng dựa trên ID người dùng
    @Override
    public void changePassword(long userId, String newPassword) {
        clearSQL();
        builderSQL.append("UPDATE user SET password = ? WHERE id = ?");
        update(builderSQL.toString(), newPassword, userId);
    }

    public boolean changePassword(String email, String newPassword) {
        clearSQL();
        builderSQL.append("UPDATE user SET password = ? WHERE email = ?");
        try {
            update(builderSQL.toString(), newPassword, email);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Phương thức để lấy thông tin người dùng dựa trên địa chỉ email
    @Override
    public Optional<User> getByEmail(String email) {
        clearSQL();
        builderSQL.append("SELECT * FROM user WHERE email = ?");
        List<User> users = query(builderSQL.toString(), new UserMapper(), email);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
    }

    // Phương thức để lấy thông tin người dùng dựa trên số điện thoại
    @Override
    public Optional<User> getByPhoneNumber(String phoneNumber) {
        clearSQL();
        builderSQL.append("SELECT * FROM user WHERE phoneNumber = ?");
        List<User> users = query(builderSQL.toString(), new UserMapper(), phoneNumber);
        return users.isEmpty() ? Optional.empty() : Optional.ofNullable(users.get(0));
    }

    @Override
    public List<User> getAll() {
        clearSQL();
        builderSQL.append(
                "SELECT * FROM user"
        );
        List<User> users = query(builderSQL.toString(), new UserMapper());
        return users.isEmpty() ? new ArrayList<>() : users;
    }

    // Phương thức để đếm tổng số lượng người dùng trong cơ sở dữ liệu
    public int count() {
        clearSQL();
        builderSQL.append("SELECT COUNT(*) FROM user");
        return count(builderSQL.toString());
    }

    // Phương thức lấy ra tổng số người dùng, đang hoạt động, số người dùng trong tháng, số bị block
    public Map<String, Object> getUserStatistics() {
        clearSQL();
        builderSQL.append(
                "{CALL bookshopdb.GetUserStatistics()}"
        );
        Map<String, Object> statistics = new HashMap<>();
        Connection conn = null;
        ResultSet rs = null;
        CallableStatement cstmt = null;
        try {
            conn = getConnection();
            cstmt = conn.prepareCall(builderSQL.toString());
            rs = cstmt.executeQuery();
            if (rs.next()) {
                statistics.put("totalUsers", rs.getInt("total_users"));
                statistics.put("activeUsers", rs.getInt("active_users"));
                statistics.put("activePercentage", rs.getDouble("active_percentage"));
                statistics.put("newUsersThisMonth", rs.getInt("new_users_this_month"));
                statistics.put("newUsersLastMonth", rs.getInt("new_users_last_month"));
                statistics.put("lockedAccounts", rs.getInt("locked_accounts"));
                statistics.put("lockedPercentage", rs.getDouble("locked_percentage"));

                // Tính tỷ lệ tăng trưởng người dùng
                int newUsersThisMonth = rs.getInt("new_users_this_month");
                int newUsersLastMonth = rs.getInt("new_users_last_month");

                if (newUsersLastMonth > 0) {
                    double growthPercentage = ((double) newUsersThisMonth - newUsersLastMonth) / newUsersLastMonth * 100;
                    statistics.put("growthPercentage", Math.round(growthPercentage * 100) / 100.0);
                } else if (newUsersThisMonth > 0) {
                    // Nếu tháng trước không có người dùng mới nhưng tháng này có
                    statistics.put("growthPercentage", 100.0);
                } else {
                    // Cả hai tháng đều không có người dùng mới
                    statistics.put("growthPercentage", 0.0);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, cstmt, rs);
        }
        return statistics;
    }

    @Override
    public User mapResultSetToEntity(ResultSet rs) throws SQLException {
        try {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFullName(rs.getString("fullname"));
            user.setEmail(rs.getString("email"));
            user.setPhoneNumber(rs.getString("phoneNumber"));
            user.setGender(rs.getInt("gender"));
            user.setAddress(rs.getString("address"));
            user.setRole(rs.getString("role"));
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy số người dùng với đầy đủ thông tin chi tiết theo page, limit
     *
     * @return {
     * users: [UserFullDetail],
     * currentPage: int,
     * totalPages: int,
     * totalUsers: int
     * }
     */
    public Map<String, Object> getAllUserDetails(int page, int limit, String search,
                                                 String role, String status, String sort) {
        List<UserFullDetail> users = new ArrayList<>();
        Map<Long, UserFullDetail> userMap = new HashMap<>();
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();

            // Xây dựng điều kiện WHERE và tham số một lần duy nhất
            StringBuilder whereClause = new StringBuilder();
            List<Object> whereParams = new ArrayList<>();
            buildWhereClause(whereClause, whereParams, search, role, status);

            // 1. Đếm tổng số người dùng trước (để tính totalPages)
            StringBuilder countSql = new StringBuilder();
            countSql.append("SELECT COUNT(*) as total FROM user u ")
                    .append("LEFT JOIN user_status us ON u.id = us.user_id ");

            // Thêm điều kiện WHERE nếu có
            if (whereClause.length() > 0) {
                countSql.append(whereClause);
            }

            PreparedStatement countStmt = conn.prepareStatement(countSql.toString());
            for (int i = 0; i < whereParams.size(); i++) {
                countStmt.setObject(i + 1, whereParams.get(i));
            }

            ResultSet countRs = countStmt.executeQuery();
            int totalUsers = 0;
            if (countRs.next()) {
                totalUsers = countRs.getInt("total");
            }
            countRs.close();
            countStmt.close();

            // Tính tổng số trang
            int totalPages = (int) Math.ceil((double) totalUsers / limit);

            // Đảm bảo page luôn hợp lệ
            if (page < 1) page = 1;
            if (totalPages > 0 && page > totalPages) page = totalPages;

            // 2. Truy vấn lấy thông tin cơ bản và trạng thái cho trang hiện tại
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT u.*, us.is_active, us.is_locked, us.lock_reason, us.lock_time, us.unlock_time, ")
                    .append("us.last_login_time, us.last_active_time, us.failed_login_count, us.updated_at ")
                    .append("FROM user u ")
                    .append("LEFT JOIN user_status us ON u.id = us.user_id ");

            // Thêm điều kiện WHERE nếu có
            if (whereClause.length() > 0) {
                sql.append(whereClause);
            }

            // Sắp xếp
            if (sort != null && !sort.trim().isEmpty()) {
                sql.append("ORDER BY ");
                switch (sort) {
                    case "name-asc":
                        sql.append("u.fullname ASC ");
                        break;
                    case "name-desc":
                        sql.append("u.fullname DESC ");
                        break;
                    case "date-asc":
                        sql.append("u.created_at ASC ");
                        break;
                    case "date-desc":
                        sql.append("u.created_at DESC ");
                        break;
                    case "last-login":
                        sql.append("us.last_login_time DESC NULLS LAST ");
                        break;
                    case "role-asc":
                        sql.append("u.role ASC ");
                        break;
                    default:
                        // Mặc định sắp xếp theo ID giảm dần
                        sql.append("u.id DESC ");
                        break;
                }
            } else {
                // Mặc định sắp xếp theo ID giảm dần nếu không có tham số sort
                sql.append("ORDER BY u.id DESC ");
            }

            // Thêm phân trang
            sql.append("LIMIT ? OFFSET ?");

            // Tính offset dựa trên page và limit
            int offset = (page - 1) * limit;

            // Sao chép các tham số từ whereParams
            List<Object> params = new ArrayList<>(whereParams);

            // Thêm limit và offset vào cuối danh sách tham số
            params.add(limit);
            params.add(offset);

            stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                long userId = rs.getLong("id");

                UserFullDetail user = new UserFullDetail();
                user.setId(userId);
                user.setUsername(rs.getString("username"));
                user.setFullname(rs.getString("fullname"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setGender(rs.getBoolean("gender"));
                user.setAddress(rs.getString("address"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at"));

                // Thông tin trạng thái
                user.setActive(rs.getBoolean("is_active"));
                user.setLocked(rs.getBoolean("is_locked"));
                user.setLockReason(rs.getString("lock_reason"));
                user.setLockTime(rs.getTimestamp("lock_time"));
                user.setUnlockTime(rs.getTimestamp("unlock_time"));
                user.setLastLoginTime(rs.getTimestamp("last_login_time"));
                user.setLastActiveTime(rs.getTimestamp("last_active_time"));
                user.setFailedLoginCount(rs.getInt("failed_login_count"));
                user.setStatusUpdatedAt(rs.getTimestamp("updated_at"));

                // Khởi tạo danh sách trống cho lịch sử đăng nhập
                user.setLoginHistory(new ArrayList<>());

                // Lưu vào map để dễ dàng truy cập sau này
                userMap.put(userId, user);
                users.add(user);
            }
            rs.close();
            stmt.close();

            // 3. Truy vấn lấy lịch sử đăng nhập gần đây cho tất cả người dùng
            if (!userMap.isEmpty()) {
                // Lấy tất cả ID người dùng
                List<Long> userIds = new ArrayList<>(userMap.keySet());

                // Kiểm tra danh sách có rỗng không trước khi thực hiện truy vấn
                if (!userIds.isEmpty()) {
                    // Tạo chuỗi dấu hỏi cho IN clause
                    String inClause = String.join(",", Collections.nCopies(userIds.size(), "?"));

                    // Truy vấn lịch sử đăng nhập
                    String loginHistorySql =
                            "SELECT h.* FROM (" +
                                    "    SELECT *, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY login_time DESC) as rn " +
                                    "    FROM user_login_history " +
                                    "    WHERE user_id IN (" + inClause + ")" +
                                    ") h WHERE h.rn <= 5"; // 5 lần đăng nhập gần nhất
                    PreparedStatement historyStmt = conn.prepareStatement(loginHistorySql);
                    for (int i = 0; i < userIds.size(); i++) {
                        historyStmt.setLong(i + 1, userIds.get(i));
                    }

                    ResultSet historyRs = historyStmt.executeQuery();

                    while (historyRs.next()) {
                        long userId = historyRs.getLong("user_id");
                        UserFullDetail user = userMap.get(userId);

                        if (user != null) {
                            LoginHistory historyEntry = new LoginHistory();
                            historyEntry.setId(historyRs.getLong("id"));
                            historyEntry.setLoginTime(historyRs.getTimestamp("login_time"));
                            historyEntry.setIpAddress(historyRs.getString("ip_address"));
                            historyEntry.setDeviceInfo(historyRs.getString("device_info"));
                            historyEntry.setBrowserInfo(historyRs.getString("browser_info"));
                            historyEntry.setLoginStatus(historyRs.getString("login_status"));

                            user.getLoginHistory().add(historyEntry);
                        }
                    }
                    historyRs.close();
                    historyStmt.close();
                }
            }

            // 4. Tạo kết quả trả về theo định dạng yêu cầu
            Map<String, Object> result = new HashMap<>();
            result.put("users", users);
            result.put("currentPage", page);
            result.put("totalPages", totalPages);
            result.put("totalUsers", totalUsers);

            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, stmt, rs);
        }
    }

    /**
     * Xây dựng điều kiện WHERE và tham số cho truy vấn
     *
     * @param whereClause StringBuilder để lưu điều kiện WHERE
     * @param params      Danh sách tham số
     * @param search      Từ khóa tìm kiếm
     * @param role        Vai trò
     * @param status      Trạng thái
     */
    private void buildWhereClause(StringBuilder whereClause, List<Object> params, String search, String role, String status) {
        boolean hasCondition = false;

        // Lọc theo tìm kiếm
        if (search != null && !search.trim().isEmpty()) {
            whereClause.append("WHERE (u.username LIKE ? " +
                    "OR u.fullname LIKE ? " +
                    "OR u.email LIKE ? " +
                    "OR u.phoneNumber LIKE ? " +
                    "OR u.address LIKE ? ");

            // Kiểm tra xem search có phải là số không
            try {
                long searchId = Long.parseLong(search.trim());
                whereClause.append("OR u.id = ?) ");
                params.add("%" + search + "%");
                params.add("%" + search + "%");
                params.add("%" + search + "%");
                params.add("%" + search + "%");
                params.add("%" + search + "%");
                params.add(searchId);
            } catch (NumberFormatException e) {
                whereClause.append(") ");
                params.add("%" + search + "%");
                params.add("%" + search + "%");
                params.add("%" + search + "%");
                params.add("%" + search + "%");
                params.add("%" + search + "%");
            }

            hasCondition = true;
        }

        // Lọc theo vai trò
        if (role != null && !role.trim().isEmpty()) {
            whereClause.append(hasCondition ? "AND " : "WHERE ");
            whereClause.append("u.role = ? ");
            params.add(role);
            hasCondition = true;
        }

        // Lọc theo trạng thái
        if (status != null && !status.trim().isEmpty()) {
            whereClause.append(hasCondition ? "AND " : "WHERE ");
            switch (status) {
                case "active":
                    whereClause.append("us.is_active = 1 ");
                    break;
                case "inactive":
                    whereClause.append("us.is_active = 0 ");
                    break;
                case "locked":
                    whereClause.append("us.is_locked = 1 ");
                    break;
                default:
                    // Mặc định không lọc theo trạng thái nếu giá trị không hợp lệ
                    whereClause.append("1=1 ");
                    break;
            }
        }
    }

    public void saveUserSession(String sessionId, String ip, String deviceInfo, long userId) {
        this.tableName = "user_session";
        clearSQL();
        builderSQL.append("INSERT INTO user_session (session_token, ip_address, device_info, user_id) ");
        builderSQL.append("VALUES (?, ?, ?, ?)");
        insert(builderSQL.toString(), sessionId, ip, deviceInfo, userId);
    }
}
