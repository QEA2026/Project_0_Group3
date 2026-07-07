package com.group3.DAOs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.group3.models.User;

public interface UserDAOInterface {

    // create user
    User createUser(User user);

    // get users return all, no params, returns list of users
    Optional<List<User>> getUsers();

    // get user by username return user
    User getUser(String username);

    // get user by id returns
    User getUserById(int id);

}
