package org.example.pocketpilot.repository.Impl;

import org.bson.types.ObjectId;
import org.example.pocketpilot.dto.requestDTO.LoginRequestDTO;
import org.example.pocketpilot.entities.UserEntity;
import org.example.pocketpilot.model.UserModel;
import org.example.pocketpilot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private  MongoTemplate mongoTemplate;

    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UserModel authenticateUser(LoginRequestDTO dto) {

        // Check if the username exists
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(dto.getUsername()));

        UserEntity userEntity = mongoTemplate.findOne(query, UserEntity.class);

        if (userEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found");
        }

        // Check if the password matches
        if (!userEntity.getPassword().equals(dto.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password");
        }

        return mapToUserModel(userEntity);
    }

    @Override
    public boolean userExists(String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        UserEntity existingUser = mongoTemplate.findOne(query, UserEntity.class);

        if (existingUser != null) {
            return true;
        }else {
            return false;
        }

    }

    @Override
    public boolean save(UserModel user) {
        try {
            UserEntity userEntity = mapToUserEntity(user);
            mongoTemplate.insert(userEntity);
            return true;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save user", e);
        }
    }

    @Override
    public List<UserModel> getUsers() {
        return mongoTemplate.findAll(UserModel.class);
    }

    @Override
    public UserModel getUserByUserName(String username) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(username));
        UserEntity user = mongoTemplate.findOne(query, UserEntity.class);
        return mapToUserModel(user);
    }

    @Override
    public String getCurrencyCodeById(ObjectId userId) {
        UserEntity user = mongoTemplate.findById(userId, UserEntity.class);

        if (user != null) {
            return user.getCurrencyCode();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        }

    }

    @Override
    public String getUserEmailByUserId(ObjectId userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(userId));
        query.fields().include("email"); // Fetch only the email field

        UserEntity user = mongoTemplate.findOne(query, UserEntity.class);
        return user != null ? user.getEmail() : null;
    }


    private UserModel mapToUserModel(UserEntity entity) {
        UserModel model = new UserModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setEmail(entity.getEmail());
        model.setUserName(entity.getUserName());
        model.setUserRole(entity.getUserRole());
        model.setCurrencyCode(entity.getCurrencyCode());
        return model;
    }

    private UserEntity mapToUserEntity(UserModel user) {
        UserEntity entity = new UserEntity();
        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setUserName(user.getUserName());
        entity.setUserRole(user.getUserRole());
        entity.setCurrencyCode(user.getCurrencyCode());
        return entity;
    }

}
