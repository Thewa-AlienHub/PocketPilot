package org.example.pocketpilot.repository;

import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.requestDTO.LoginRequestDTO;
import org.example.pocketpilot.model.UserModel;

import java.util.List;

public interface UserRepository {

    UserModel authenticateUser(LoginRequestDTO dto);

    boolean userExists (String email);

    boolean save(UserModel user);

    List<UserModel> getUsers();

    UserModel getUserByUserName(String username);

    String getCurrencyCodeById(ObjectId userId);

    String getUserEmailByUserId(ObjectId userId);
}
