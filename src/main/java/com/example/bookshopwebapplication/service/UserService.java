package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.UserDao;
import com.example.bookshopwebapplication.dao.UserKeysDao;
import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.User;
import com.example.bookshopwebapplication.entities.UserKeys;
import com.example.bookshopwebapplication.http.response.user.UserFullDetail;
import com.example.bookshopwebapplication.service._interface.IUserService;
import com.example.bookshopwebapplication.service.transferObject.TUser;
import com.example.bookshopwebapplication.utils.mail.EmailUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserService implements IUserService {
    private UserDao userDao = new UserDao();
    private UserKeysDao userKeysDao = new UserKeysDao();
    private TUser tUser = new TUser();
    private static final UserService instance = new UserService();
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
        List<UserDto> userDtoList = userDao.getOrderedPart(limit,offset,orderBy,sort)
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

    public UserKeys isExistKey(Long userID){
        Optional<UserKeys> userKeys = userKeysDao.getByUserId(userID);
        return userKeys.orElse(null);
    }

    public Long saveKey(UserKeys userKeys){
        return userKeysDao.save(userKeys);
    }

    public void updateKey(UserKeys userKeys){
        userKeysDao.update(userKeys);
    }

    public boolean sendPasswordResetEmail(UserDto userDto){
        // Gửi email khôi phục mật khẩu
        try {
            EmailUtils.sendPasswordResetEmail(userDto);
            return true;
        } catch (Exception e) {
            System.err.println("Function sendPasswordResetEmail: " + e);
            return false;
        }
    }
    public boolean resetPassword(String email, String password){
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
}
