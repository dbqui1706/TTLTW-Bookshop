package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.UserDao;
import com.example.bookshopwebapplication.dao.UserKeysDao;
import com.example.bookshopwebapplication.dao.UserSessionDao;
import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.User;
import com.example.bookshopwebapplication.entities.UserKeys;
import com.example.bookshopwebapplication.entities.UserSession;
import com.example.bookshopwebapplication.http.request.user.RegisterDTO;
import com.example.bookshopwebapplication.service._interface.IUserService;
import com.example.bookshopwebapplication.service.transferObject.TUser;
import com.example.bookshopwebapplication.utils.CookieUtil;
import com.example.bookshopwebapplication.utils.mail.EmailUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserService implements IUserService {
    private final UserDao userDao = new UserDao();
    private final UserSessionDao userSessionDao = new UserSessionDao();
    private final UserKeysDao userKeysDao = new UserKeysDao();
    private final TUser tUser = new TUser();
    private static final UserService instance = new UserService();
    private final ConcurrentHashMap<Long, String> userSessionCache = new ConcurrentHashMap<>();

    public static UserService getInstance() {
        return instance;
    }


    @Override
    public Optional<UserDto> insert(UserDto userDto) {
        Long id = userDao.save(tUser.toEntity(userDto));
        return getById(id);
    }

    @Override
    public Optional<UserDto> update(UserDto userDto) {
        userDao.update(tUser.toEntity(userDto));
        return getById(userDto.getId());
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) userDao.delete(id);
    }

    @Override
    public Optional<UserDto> getById(Long id) {
        Optional<User> user = userDao.getById(id);
        if (user.isPresent()) return Optional.of(tUser.toDto(user.get()));
        return Optional.empty();
    }

    @Override
    public List<UserDto> getPart(Integer limit, Integer offset) {
        List<UserDto> userDtoList = userDao.getPart(limit, offset)
                .stream()
                .map(user -> tUser.toDto(user)).collect(Collectors.toList());
        return userDtoList;
    }

    @Override
    public List<UserDto> getOrderedPart(Integer limit, Integer offset, String orderBy, String sort) {
        List<UserDto> userDtoList = userDao.getOrderedPart(limit, offset, orderBy, sort)
                .stream()
                .map(user -> tUser.toDto(user))
                .collect(Collectors.toList());
        return userDtoList;
    }

    @Override
    public int count() {
        return userDao.count();
    }

    @Override
    public Optional<UserDto> getByUsername(String username) {
        Optional<User> user = userDao.getByNameUser(username);
        if (user.isPresent()) {
            return Optional.of(tUser.toDto(user.get()));
        }
        return Optional.empty();
    }

    @Override
    public void changePassword(long userId, String newPassword) {
        userDao.changePassword(userId, newPassword);
    }

    // Phương thức để lấy đối tượng UserDto dựa trên địa chỉ email
    @Override
    public Optional<UserDto> getByEmail(String email) {
        Optional<User> user = userDao.getByEmail(email);
        if (user.isPresent()) return Optional.of(tUser.toDto(user.get()));
        return Optional.empty();
    }

    // Phương thức để lấy đối tượng UserDto dựa trên số điện thoại
    @Override
    public Optional<UserDto> getByPhoneNumber(String phoneNumber) {
        Optional<User> user = userDao.getByPhoneNumber(phoneNumber);
        if (user.isPresent()) return Optional.of(tUser.toDto(user.get()));
        return Optional.empty();
    }

    @Override
    public List<String> getAllUsername() {
        return userDao.getAll().stream()
                .map(user -> user.getUsername())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllEmails() {
        return userDao.getAll().stream()
                .map(user -> user.getEmail())
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllPhones() {
        return userDao.getAll().stream()
                .map(User::getPhoneNumber)
                .collect(Collectors.toList());
    }

    public UserKeys isExistKey(Long userID) {
        Optional<UserKeys> userKeys = userKeysDao.getByUserId(userID);
        return userKeys.orElse(null);
    }

    public Long saveKey(UserKeys userKeys) {
        return userKeysDao.save(userKeys);
    }

    public void updateKey(UserKeys userKeys) {
        userKeysDao.update(userKeys);
    }

    public boolean sendPasswordResetEmail(UserDto userDto) {
        // Gửi email khôi phục mật khẩu
        try {
            EmailUtils.sendPasswordResetEmail(userDto);
            return true;
        } catch (Exception e) {
            System.err.println("Function sendPasswordResetEmail: " + e);
            return false;
        }
    }

    public boolean resetPassword(String email, String password) {
        try {
            userDao.changePassword(email, password);
            return true;
        } catch (Exception e) {
            System.err.println("Function resetPassword: " + e);
            return false;
        }
    }

    public Map<String, Object> getStatistic() {
        return userDao.getUserStatistics();
    }

    public Map<String, Object> getAllUserDetails(int page, int limit, String search,
                                                 String role, String status, String sort) {
        // Lấy danh sách người dùng có đầy đủ thông tin chi tiết
        return userDao.getAllUserDetails(page, limit, search, role, status, sort);
    }

    public void saveUserSession(HttpServletRequest request, long userId, String token) {
        // Lấy thông tin thiết bị, ip, session id
        String deviceInfo = request.getHeader("User-Agent");
        String ip = request.getRemoteAddr();
        String sessionId = request.getSession().getId();

        UserSession userSession = new UserSession();
        userSession.setSessionToken(token);
        userSession.setIpAddress(ip);
        userSession.setDeviceInfo(deviceInfo);
        userSession.setExpireTime(new Timestamp(System.currentTimeMillis() + CookieUtil.EXPIRATION_TIME));
        userSession.setUserId(userId);

        // Kiểm xem session đã tồn tại chưa
        if (userSessionCache.containsKey(userId) && userSessionCache.get(userId).equals(sessionId)) {
            // Nếu đã tồn tại thì cập nhật lại
//            userSessionDao.update(userSession);
        } else {
            // Nếu chưa thì thêm mới
            userSessionCache.put(userId, sessionId); // put vào cache
            userSessionDao.save(userSession); // save vào database
        }
    }

    // Login with email and password
    public Optional<UserDto> login(String email, String password) {
        Optional<User> user = userDao.getByEmail(email);
        if (
                user.isPresent()
                        && user.get().getPassword().equals(password)
                        && user.get().getIsActiveEmail()
        ) {
            return Optional.of(tUser.toDto(user.get()));
        }
        return Optional.empty();
    }

    // Register with RegisterDTO
    public Optional<UserDto> register(RegisterDTO registerDTO) {
        User user = new User();
        user.setFullName(registerDTO.getFullname());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setRole(registerDTO.getRole());
        user.setPhoneNumber(registerDTO.getPhone());
        user.setGender(registerDTO.getGender());
        Long id = userDao.register(user);
        return getById(id);
    }

    public void setActiveEmail(String email) {
        User user = userDao.getByEmail(email).get();
        user.setIsActiveEmail(true);
        userDao.update(user);
    }
}
