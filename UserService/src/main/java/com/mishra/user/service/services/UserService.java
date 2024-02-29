package com.mishra.user.service.services;

import com.mishra.user.service.entities.User;

import java.util.List;

public interface UserService {
    // user operations

    //create a new user
    User saveUser(User user);

    // findAllUsers
    List<User> getAllUsers();
    //find user by id
    User getUserById(String id);

    //delete user
    void deleteUser(String id);

    //update user
    User updateUser(User user);


}
