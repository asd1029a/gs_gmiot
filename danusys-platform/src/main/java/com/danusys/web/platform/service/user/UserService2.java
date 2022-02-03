package com.danusys.web.platform.service.user;


import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserDto;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserRepository;

import com.danusys.web.commons.auth.util.SHA256;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class UserService2 {

    private final UserRepository userRepository;
    private final UserGroupInUserRepository userGroupInUserRepository;


    public User findUser(String userName, String errorMessage) {

        return userRepository.findByUsername(userName);

    }

    public User findUser(String userName) {
        User user = userRepository.findByUsername(userName);
        //user.setUserGroupInUser(userGroupInUserRepository.findByUser(user));
        return user;

    }


    public User findUser(int userSeq) {
        User user = userRepository.findById(userSeq);
        //user.setUserGroupInUser(userGroupInUserRepository.findByUser(user));
        return user;

    }

    @Transactional
    public User updateUser(User user) {
        User findUser = userRepository.findById(user.getId());
        if (findUser != null) {
         //   if (user.getUserName() != null)
            //    findUser.setUserName(user.getUserName());
            if (user.getEmail() != null)
                findUser.setEmail(user.getEmail());
            if (user.getTel() != null)
                findUser.setTel(user.getTel());
            if (user.getAddress() != null)
                findUser.setAddress(user.getAddress());
            if (user.getStatus() != 0)
                findUser.setStatus(user.getStatus());
            if (user.getDetailAddress() != null)
                findUser.setDetailAddress(user.getDetailAddress());
            if (user.getUpdateUserSeq() != 0) {
                findUser.setUpdateUserSeq(user.getUpdateUserSeq());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                findUser.setUpdateDt(timestamp);
            }


        }

        //    return userRepository.save(findUser);
        return findUser;
    }

    @Transactional
    public void deleteUser(User user) {

        userRepository.deleteById(user.getId());


    }


    @Transactional
    public User saveUser(User user) {
        SHA256 sha256 = new SHA256();
        try {
            String cryptoPassword = sha256.encrypt(user.getPassword());
            user.setPassword("{SHA-256}" + cryptoPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return userRepository.save(user);
    }


}
