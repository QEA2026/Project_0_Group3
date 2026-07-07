package com.group3.service;

import com.group3.DAOs.UserDAOInterface;
import com.group3.models.User;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserService {
    private final UserDAOInterface userDAO;

    public UserService(UserDAOInterface userDAO) { this.userDAO = userDAO; }

    public User login(String username, String password) {
        User user = userDAO.getUser(username);

        // same message for "no such user" and "wrong password" — don't leak which
        if (user == null || user.getUsername() == null) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
        if (!result.verified) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // role-based access: this app is managers only
        if (!"Manager".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("Access denied: manager role required");
        }
        return user;
    }
}
