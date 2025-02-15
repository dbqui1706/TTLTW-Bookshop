package com.example.bookshopwebapplication.service;

import com.example.bookshopwebapplication.dao.OauthUserDao;
import com.example.bookshopwebapplication.dto.UserDto;
import com.example.bookshopwebapplication.entities.OauthUser;
import com.example.bookshopwebapplication.service._interface.IOauthUserService;

import java.util.List;
import java.util.Optional;

public class OauthUserService implements IOauthUserService {
    private final OauthUserDao oauthUserDao = new OauthUserDao();
    private final UserService userService = new UserService();

    @Override
    public Optional<OauthUser> insert(OauthUser oauthUser) {
        if (oauthUser.getProvider().equals("GOOGLE")) {
            String email = oauthUser.getEmail();
            Optional<UserDto> user = userService.getByEmail(email);
            if (user.isPresent()) {
                return findByUserID(user.get().getId());
            }
        }
        if (oauthUser.getProvider().equals("FACEBOOK")) {
            Optional<OauthUser> fbOauth = oauthUserDao.getByProviderFBID(oauthUser.getProviderID());
            return fbOauth.isPresent() ? Optional.of(fbOauth.get()) : Optional.empty();
        }
        Long id = oauthUserDao.save(oauthUser);
        Optional<OauthUser> user = getById(id);
        return user;
    }

    @Override
    public Optional<OauthUser> update(OauthUser oauthUser) {
        return Optional.empty();
    }

    @Override
    public void delete(Long[] ids) {

    }

    @Override
    public Optional<OauthUser> getById(Long id) {
        return oauthUserDao.findByID(id);
    }

    @Override
    public List<OauthUser> getPart(Integer limit, Integer offset) {
        return List.of();
    }

    @Override
    public List<OauthUser> getOrderedPart(Integer limit, Integer offset, String orderBy, String sort) {
        return List.of();
    }

    @Override
    public int count() {
        return 0;
    }

    public Optional<OauthUser> findByUserID(Long userID) {
        return oauthUserDao.findByUserID(userID);
    }
}
