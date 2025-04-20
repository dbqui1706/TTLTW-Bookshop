package com.example.bookshopwebapplication.dao;

import com.example.bookshopwebapplication.dao._interface.IGenericDao;
import com.example.bookshopwebapplication.dao.mapper._interface.IRowMapper;
import com.example.bookshopwebapplication.utils.RequestContext;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * AbstractDao cung cấp các phương thức truy vấn cơ bản để thao tác với database.
 * Mỗi phương thức mở kết nối mới và sử dụng try-with-resources để tự động đóng tài nguyên.
 *
 * @param <T> Kiểu entity mà DAO sẽ thao tác
 */
public abstract class AbstractDao<T> implements IGenericDao<T> {
    // Logger dùng cho việc ghi log
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDao.class);

    // ResourceBundle để lấy thông tin kết nối từ file database.properties
    private final ResourceBundle bundle = ResourceBundle.getBundle("database");

    // Tên bảng của entity, được truyền từ lớp con (ví dụ: "user", "audit_log", …)
    protected String tableName;

    // Gson dùng để chuyển đối tượng thành JSON khi ghi log
    protected final Gson gson = new Gson();

    // String builder dùng để xây dựng câu truy vấn SQL
    protected final StringBuilder builderSQL = new StringBuilder();

    /**
     * Constructor nhận vào tên bảng của entity
     *
     * @param tableName Tên bảng
     */
    public AbstractDao(String tableName) {
        this.tableName = tableName;
    }

    public void clearSQL() {
        builderSQL.setLength(0);
    }

    /**
     * Mở kết nối mới đến database
     *
     * @return Connection mới
     * @throws RuntimeException nếu không kết nối được
     */
    public Connection getConnection() {
        try {
            // Tải driver từ file properties (ví dụ: "com.mysql.cj.jdbc.Driver")
            Class.forName(bundle.getString("driverName"));
            // Lấy connection từ DriverManager
            return DriverManager.getConnection(
                    bundle.getString("url"),
                    bundle.getString("user"),
                    bundle.getString("password")
            );
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error("Error getting connection", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Thực hiện truy vấn SELECT và ánh xạ kết quả thành danh sách entity
     *
     * @param sql        Câu truy vấn SQL
     * @param rowMapper  Đối tượng ánh xạ từng dòng của ResultSet thành entity
     * @param parameters Các tham số cho PreparedStatement
     * @return Danh sách entity
     */
    @Override
    public List<T> query(String sql, IRowMapper<T> rowMapper, Object... parameters) {
        List<T> result = new LinkedList<>();
        // Sử dụng try-with-resources để tự động đóng connection, stmt, resultSet
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, parameters);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.error("Error executing query", e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Thực hiện một truy vấn INSERT và trả về khóa tự động sinh (generated key)
     * Sau khi insert thành công, nếu bảng không phải là bảng audit_log thì ghi log thao tác.
     *
     * @param sql        Câu truy vấn INSERT
     * @param parameters Các tham số cho PreparedStatement
     * @return Khóa (ID) của bản ghi vừa insert, hoặc null nếu insert thất bại
     */
    @Override
    public Long insert(String sql, Object... parameters) {
        try (Connection conn = getConnection();
             // Sử dụng Statement.RETURN_GENERATED_KEYS để lấy ID tự động sinh ra
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Đặt các tham số cho câu truy vấn
            setParameters(stmt, parameters);
            stmt.executeUpdate();

            // Lấy generated keys
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    Long id = rs.getLong(1);
                    // Nếu không thao tác trên bảng audit_log, ghi log thao tác INSERT
                    if (!this.tableName.equalsIgnoreCase("audit_log")) {
                        // Lấy trạng thái hiện tại của bản ghi vừa insert
                        T insertedEntity = getCurrentState(id);
                        logOperation("INSERT", "INFO", null, insertedEntity,
                                RequestContext.getUserId() != null ? RequestContext.getUserId() : 0L);
                    }
                    return id;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error executing insert", e);
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Thực hiện một truy vấn INSERT không lấy generated key.
     * Sau khi insert thành công, nếu bảng không phải là bảng audit_log thì ghi log thao tác.
     *
     * @param sql        Câu truy vấn INSERT
     * @param parameters Các tham số cho PreparedStatement
     */
    public void insertNoGenerateKey(String sql, Object... parameters) {
        try (Connection conn = getConnection();
             // Không cần lấy generated keys
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Đặt các tham số cho câu truy vấn
            setParameters(stmt, parameters);
            stmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("Error executing insert", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Thực hiện một truy vấn UPDATE.
     * Lấy dữ liệu trước và sau khi update để ghi log.
     *
     * @param sql        Câu truy vấn UPDATE
     * @param parameters Các tham số cho PreparedStatement; giả sử ID là tham số cuối cùng.
     */
    @Override
    public void update(String sql, Object... parameters) {
        // Lấy ID từ các tham số (ví dụ: ID là tham số cuối cùng)
        Long id = extractIdFromParameters(parameters);
        // Lấy trạng thái trước khi update bằng một kết nối riêng
        T beforeState = getCurrentState(id);
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            // Thực hiện truy vấn UPDATE
            try (PreparedStatement stmt = prepareStatement(conn, sql, parameters)) {
                stmt.executeUpdate();
            }
            conn.commit();

            // Sau khi update, lấy trạng thái mới của bản ghi
            T afterState = getCurrentState(id);
            // Nếu không thao tác trên bảng audit_log, ghi log thao tác UPDATE
            if (!this.tableName.equalsIgnoreCase("audit_log")) {
                logOperation("UPDATE", "WARNING", beforeState, afterState,
                        RequestContext.getUserId() != null ? RequestContext.getUserId() : 0L);
            }
        } catch (SQLException e) {
            LOGGER.error("Error executing update", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String sql, Object... parameters) {
        // Lấy ID từ các tham số (ví dụ: ID là tham số cuối cùng)
        Long id = extractIdFromParameters(parameters);
        // Lấy trạng thái trước khi update bằng một kết nối riêng
        T beforeState = getCurrentState(id);
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            // Thực hiện truy vấn UPDATE
            try (PreparedStatement stmt = prepareStatement(conn, sql, parameters)) {
                stmt.executeUpdate();
            }
            conn.commit();

            // Sau khi update, lấy trạng thái mới của bản ghi
            T afterState = getCurrentState(id);
            // Nếu không thao tác trên bảng audit_log, ghi log thao tác UPDATE
            if (!this.tableName.equalsIgnoreCase("audit_log")) {
                logOperation("DELETE", "DANGER", beforeState, afterState,
                        RequestContext.getUserId() != null ? RequestContext.getUserId() : 0L);
            }
        } catch (SQLException e) {
            LOGGER.error("Error executing update", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Lấy trạng thái hiện tại của bản ghi từ database theo ID.
     * Mỗi lần gọi phương thức này mở một kết nối mới và tự đóng sau khi xong.
     *
     * @param id ID của bản ghi cần lấy trạng thái
     * @return Entity được ánh xạ từ kết quả truy vấn, hoặc null nếu không tìm thấy
     */
    protected T getCurrentState(Long id) {
        if (id == null) {
            return null;
        }
        T entity = null;
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity = mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting current state", e);
            throw new RuntimeException(e);
        }
        return entity;
    }

    /**
     * Ghi log thao tác vào bảng audit_log.
     * Nếu bảng hiện tại không phải là "audit_log" thì sử dụng AuditLogDao để lưu log.
     *
     * @param action     Loại thao tác (INSERT, UPDATE, DELETE, …)
     * @param level      Mức độ log (ví dụ: INFO, WARNING)
     * @param beforeData Dữ liệu trước khi thao tác (có thể null)
     * @param afterData  Dữ liệu sau khi thao tác
     * @param modifiedBy ID người thực hiện thao tác
     */
    protected void logOperation(String action, String level, Object beforeData,
                                Object afterData, Long modifiedBy) {
        // Tránh vòng lặp log khi đang thao tác trên bảng audit_log
        if (this.tableName.equalsIgnoreCase("audit_log")) {
            return;
        }
        AuditLogDao auditLogDao = new AuditLogDao();
        LOGGER.info("Logging operation: action={}, level={}, beforeData={}, afterData={}, modifiedBy={}",
                action, level, beforeData, afterData, modifiedBy);
        String ipAddress = RequestContext.getIpAddress();
        String beforeJson = beforeData != null ? gson.toJson(beforeData) : null;
        String afterJson = afterData != null ? gson.toJson(afterData) : null;

        // Ghi log bằng cách gọi phương thức insert của AuditLogDao
        long id = auditLogDao.saveLog(ipAddress, level, tableName, action, beforeJson, afterJson, modifiedBy);
    }

    /**
     * Tạo PreparedStatement và đặt các tham số vào câu truy vấn.
     *
     * @param conn       Connection hiện tại
     * @param sql        Câu truy vấn SQL
     * @param parameters Các tham số cho câu truy vấn
     * @return PreparedStatement đã được thiết lập tham số
     * @throws SQLException Nếu có lỗi xảy ra khi thiết lập tham số
     */
    protected PreparedStatement prepareStatement(Connection conn, String sql, Object... parameters)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        setParameters(stmt, parameters);
        return stmt;
    }

    /**
     * Đặt các tham số cho PreparedStatement.
     *
     * @param stmt       PreparedStatement cần thiết lập
     * @param parameters Các tham số cần đặt vào
     * @throws SQLException Nếu có lỗi khi đặt tham số
     */
    protected void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            Object param = parameters[i];
            int index = i + 1;
            if (param instanceof Long) {
                stmt.setLong(index, (Long) param);
            } else if (param instanceof String) {
                stmt.setString(index, (String) param);
            } else if (param instanceof Integer) {
                stmt.setInt(index, (Integer) param);
            } else if (param instanceof Timestamp) {
                stmt.setTimestamp(index, (Timestamp) param);
            } else if (param instanceof Double) {
                stmt.setDouble(index, (Double) param);
            } else if (param == null) {
                stmt.setNull(index, Types.NULL);
            }
        }
    }

    /**
     * Trích xuất ID từ các tham số của câu truy vấn.
     * Giả định rằng ID là tham số cuối cùng.
     *
     * @param parameters Các tham số truyền vào
     * @return ID dưới dạng Long, hoặc null nếu không tìm thấy
     */
    protected Long extractIdFromParameters(Object... parameters) {
        if (parameters.length > 0) {
            Object lastParam = parameters[parameters.length - 1];
            if (lastParam instanceof Long) {
                return (Long) lastParam;
            }
        }
        return null;
    }

    // Các phương thức sau được triển khai dựa trên logic của IGenericDao

    @Override
    public Optional<T> getById(String sql, IRowMapper<T> mapper, Object... parameters) {
        List<T> list = query(sql, mapper, parameters);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<T> getAll(String sql, IRowMapper<T> mapper, Object... parameters) {
        return query(sql, mapper, parameters);
    }

    @Override
    public List<T> getPart(String sql, IRowMapper<T> mapper, Object... parameters) {
        return query(sql, mapper, parameters);
    }

    @Override
    public List<T> getOrderedPart(String sql, IRowMapper<T> mapper, Object... parameters) {
        return query(sql, mapper, parameters);
    }

    @Override
    public int count(String sql, Object... parameters) {
        int count = 0;
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, parameters);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error counting records", e);
            throw new RuntimeException(e);
        }
        return count;
    }

    @Override
    public Long getIdElement(String sql, Object... parameters) {
        Long id = null;
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, parameters);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                id = rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.error("Error getting id element", e);
            throw new RuntimeException(e);
        }
        return id;
    }

    protected void close(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close(); // Chỉ đóng kết nối nếu không phải kết nối bên ngoài
            }
        } catch (SQLException e) {
            LOGGER.error("Error closing resources", e);
            throw new RuntimeException(e);
        }
    }

    public void executeTransaction(TransactionCallback callback) {
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            callback.execute(conn);

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    throw new RuntimeException("Không thể rollback transaction", ex);
                }
            }
            throw new RuntimeException("Lỗi khi thực thi transaction: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Log lỗi
                }
            }
        }
    }

    // Interface để chuyển các câu lệnh SQL vào transaction
    public interface TransactionCallback {
        void execute(Connection conn) throws SQLException;
    }

    /**
     * Thực hiện một truy vấn INSERT với connection được cung cấp từ bên ngoài.
     *
     * @param conn Connection được cung cấp từ bên ngoài
     * @param sql Câu truy vấn INSERT
     * @param parameters Các tham số
     * @return ID của bản ghi vừa được thêm vào
     */
    public Long insertWithConnection(Connection conn, String sql, Object... parameters) {
        try {
            // Sử dụng connection đã được cung cấp
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Đặt các tham số
            setParameters(stmt, parameters);
            stmt.executeUpdate();

            // Lấy ID được sinh ra
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                Long id = rs.getLong(1);

                // Không ghi log tại đây vì transaction chưa commit
                // Log sẽ được ghi sau khi transaction commit thành công

                return id;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thực hiện insert: " + e.getMessage(), e);
        }
        return null;
    }
    /**
     * Thực hiện một truy vấn UPDATE với connection được cung cấp từ bên ngoài.
     *
     * @param conn Connection được cung cấp từ bên ngoài
     * @param sql Câu truy vấn UPDATE
     * @param parameters Các tham số
     */
    public void updateWithConnection(Connection conn, String sql, Object... parameters) {
        try {
            // Sử dụng connection đã được cung cấp
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Đặt các tham số
            setParameters(stmt, parameters);
            stmt.executeUpdate();

            stmt.close();
            // Không ghi log tại đây vì transaction chưa commit
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thực hiện update: " + e.getMessage(), e);
        }
    }

    /**
     * Thực hiện một truy vấn DELETE với connection được cung cấp từ bên ngoài.
     *
     * @param conn Connection được cung cấp từ bên ngoài
     * @param sql Câu truy vấn DELETE
     * @param parameters Các tham số
     */
    public void deleteWithConnection(Connection conn, String sql, Object... parameters) {
        try {
            // Sử dụng connection đã được cung cấp
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Đặt các tham số
            setParameters(stmt, parameters);
            stmt.executeUpdate();

            stmt.close();
            // Không ghi log tại đây vì transaction chưa commit
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thực hiện delete: " + e.getMessage(), e);
        }
    }
    /**
     * Thực hiện truy vấn SELECT với connection được cung cấp từ bên ngoài.
     *
     * @param conn Connection được cung cấp từ bên ngoài
     * @param sql Câu truy vấn SQL
     * @param rowMapper Mapper để ánh xạ ResultSet sang entity
     * @param parameters Các tham số
     * @return Danh sách entity
     */
    public List<T> queryWithConnection(Connection conn, String sql, IRowMapper<T> rowMapper, Object... parameters) {
        List<T> result = new LinkedList<>();
        try {
            // Sử dụng connection đã được cung cấp
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Đặt các tham số
            setParameters(stmt, parameters);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thực hiện query: " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * Lấy một entity theo ID với connection được cung cấp từ bên ngoài.
     *
     * @param conn Connection được cung cấp từ bên ngoài
     * @param sql Câu truy vấn SQL
     * @param mapper Mapper để ánh xạ ResultSet sang entity
     * @param parameters Các tham số
     * @return Optional chứa entity nếu tìm thấy
     */
    public Optional<T> getByIdWithConnection(Connection conn, String sql, IRowMapper<T> mapper, Object... parameters) {
        List<T> list = queryWithConnection(conn, sql, mapper, parameters);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * Phương thức trừu tượng để ánh xạ 1 dòng ResultSet thành một entity.
     * Các lớp con phải triển khai phương thức này.
     *
     * @param resultSet ResultSet từ truy vấn SQL
     * @return Entity ánh xạ từ dòng dữ liệu trong ResultSet
     * @throws SQLException Nếu có lỗi khi truy xuất dữ liệu
     */
    public abstract T mapResultSetToEntity(ResultSet resultSet) throws SQLException;
}
