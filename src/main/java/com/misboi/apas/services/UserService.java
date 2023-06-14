package com.misboi.apas.services;

import com.misboi.apas.entities.User;
import com.misboi.apas.entities.UserRole;

import java.util.Set;

public interface UserService {

    // creating user
    public User createUser(User user, Set<UserRole> userRoles) throws Exception;

    // get user by username
    public User getUser(String username);

    // delete user by userid
    public void deleteUser(Long userId);

//    // update user by userName
//    public User updateUser(String userName);
}
