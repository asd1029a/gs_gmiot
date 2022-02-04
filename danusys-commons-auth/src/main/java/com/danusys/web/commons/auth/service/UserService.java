package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserDto;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserRepository;

import com.danusys.web.commons.auth.util.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserGroupInUserRepository userGroupInUserRepository;



    public User findUser(String userName, String errorMessage) {

        return userRepository.findByUserId(userName);

    }

    public User findUser(String userName) {
        User user=userRepository.findByUserId(userName);
        //user.setUserGroupInUser(userGroupInUserRepository.findByUser(user));
        return user;

    }

    @Transactional
    public User updateUser(String userName, String refreshToken) {
        User findUser = this.findUser(userName, "Error update user id");
        if(findUser!=null)
        findUser.setRefreshToken(refreshToken);

        //    return userRepository.save(findUser);
        return findUser;
    }




    @Transactional
    public int saveUser(User user) {
        SHA256 sha256 = new SHA256();
        try {
            String cryptoPassword = sha256.encrypt(user.getPassword());
            user.setPassword("{SHA-256}" +cryptoPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        userRepository.save(user);
        return user.getUserSeq();
    }


}
