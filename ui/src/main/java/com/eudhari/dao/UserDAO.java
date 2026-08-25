package com.eudhari.dao;

import com.eudhari.model.UserModel;

import java.util.List;

public interface UserDAO {
    void saveUser(UserModel user);
    UserModel getUserById(String uid);
    void updateUser(UserModel user);
    int getUserCountByRole(String role);
    List<UserModel> getUsersByRole(String role);
    void updateUserStatus(String uid, String status);
}
