package com.danusys.web.platform.service.user;


import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserDto;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserRepository;

import com.danusys.web.commons.auth.util.SHA256;
import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.service.event.EventServiceImpl;
import com.danusys.web.platform.service.notice.NoticeService;
import com.danusys.web.platform.service.notice.NoticeServiceImpl;
import com.danusys.web.platform.util.PagingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                user.getLastLoginDt(), user.getInsertUserSeq(), user.getUpdateUserSeq(), user.getInsertDt(), user.getUpdateDt());
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


    // public List<UserDto> findListUser() {
    public Map<String, Object> findListUser(Map<String, Object> paramMap) {
        List<User> userList = userRepository.findAll();
        //List<MissionResponse> changeMissionList = missionList.stream().map(MissionResponse::new).collect(Collectors.toList());

        if (paramMap.get("draw") != null) {
            EgovMap egovMap = null;
            try {
                egovMap = PagingUtil.createPagingMap(paramMap, userList.stream().map(UserDto::new).collect(Collectors.toList()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return egovMap;
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", userList.stream().map(UserDto::new).collect(Collectors.toList()));
            return resultMap;
        }

    }
    //userList.stream().map(UserDto::new).collect(Collectors.toList());
    // return userList.stream().collect(Collectors.toMap(User::getUserId, UserDto::new));


}
