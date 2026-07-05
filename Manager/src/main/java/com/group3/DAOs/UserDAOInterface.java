package com.group3.DAOs;

import java.util.ArrayList;

import com.group3.models.User;

public interface UserDAOInterface {

    // get users return all, no params
    ArrayList<User> getUsers();

    // get users
    User getUser(String username);

    // get user id
    User getUserById(int id);

    // insert users, takes user, returns user
    User insertUser(User user);

}
