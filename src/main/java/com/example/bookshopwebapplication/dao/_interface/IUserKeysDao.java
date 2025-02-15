package com.example.bookshopwebapplication.dao._interface;

import com.example.bookshopwebapplication.entities.UserKeys;

import java.util.Optional;

public interface IUserKeysDao extends IGenericDao<UserKeys> {
    Optional<UserKeys> getByUserId(long userId);

}
