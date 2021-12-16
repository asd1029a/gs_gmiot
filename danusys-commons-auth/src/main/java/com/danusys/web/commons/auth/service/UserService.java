package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    public User findUser(String userName, String errorMessage) {
        return userRepository.findByUsername(userName);

    }
    @Transactional
    public User updateUser(String userName,String refreshToken) {
        User findUser = this.findUser(userName, "Error update user id");

        findUser.setRefreshToken(refreshToken);

    //    return userRepository.save(findUser);
     return findUser;
    }

}
