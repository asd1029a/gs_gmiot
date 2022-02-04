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
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService2 {

    private final UserRepository userRepository;
    private final UserGroupInUserRepository userGroupInUserRepository;


    public User findUser(String userName, String errorMessage) {

        return userRepository.findByUserId(userName);

    }

    public User findUser(String userName) {
        User user = userRepository.findByUserId(userName);
        //user.setUserGroupInUser(userGroupInUserRepository.findByUser(user));
        return user;

    }


    public UserDto findUser(int userSeq) {
        User user = userRepository.findByUserSeq(userSeq);
        UserDto userDto = new UserDto(user.getUserSeq(), user.getUserId(), user.getUserName(), user.getEmail(), user.getTel(), user.getAddress(), user.getStatus(), user.getDetailAddress(),
                user.getLastLoginDt(), user.getInsertUserSeq(), user.getUpdateUserSeq(), user.getInsertDt(), user.getUpdateDt(), user.getUserGroupInUser());
        //user.setUserGroupInUser(userGroupInUserRepository.findByUser(user));
        return userDto;

    }

    @Transactional
    public int updateUser(User user) {
        User findUser = userRepository.findByUserSeq(user.getUserSeq());
        if (findUser != null) {
            if (user.getPassword() != null) {
                SHA256 sha256 = new SHA256();
                try {
                    String cryptoPassword = sha256.encrypt(user.getPassword());
                    findUser.setPassword("{SHA-256}" + cryptoPassword);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            if (user.getUserName() != null)
                findUser.setUserName(user.getUserName());
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
        return findUser.getUserSeq();
    }

    @Transactional
    public void deleteUser(User user) {

        userRepository.deleteByUserSeq(user.getUserSeq());


    }


    @Transactional
    public int saveUser(User user) {
        SHA256 sha256 = new SHA256();
        try {
            String cryptoPassword = sha256.encrypt(user.getPassword());
            user.setPassword("{SHA-256}" + cryptoPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        userRepository.save(user);
        return user.getUserSeq();
    }


    public List<User> findListUser() {
        return userRepository.findAll();
    }
}
