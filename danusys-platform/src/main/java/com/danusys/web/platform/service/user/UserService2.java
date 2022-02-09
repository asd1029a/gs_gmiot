package com.danusys.web.platform.service.user;


import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
@Slf4j
public class UserService2 {

    private final UserRepository userRepository;
    private final UserGroupInUserRepository userGroupInUserRepository;

    /*


     */

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

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommonsUserDetails userDetails = (CommonsUserDetails) principal;
        // log.info("{}",userDetails.getUserSeq());

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
            if (user.getStatus() != null)
                findUser.setStatus(user.getStatus());
            if (user.getDetailAddress() != null)
                findUser.setDetailAddress(user.getDetailAddress());

            findUser.setUpdateUserSeq(userDetails.getUserSeq());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            findUser.setUpdateDt(timestamp);


        } else {
            return 0;
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

        if (user.getUserId() == null || user.getPassword() == null)
            return -1;

        User findUser = userRepository.findByUserId(user.getUserId());
        if (findUser != null)
            return 0;
        SHA256 sha256 = new SHA256();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommonsUserDetails userDetails = (CommonsUserDetails) principal;
        // log.info("{}",userDetails.getUserSeq());
        try {
            String cryptoPassword = sha256.encrypt(user.getPassword());
            user.setPassword("{SHA-256}" + cryptoPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        user.setInsertUserSeq(userDetails.getUserSeq());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setInsertDt(timestamp);

        userRepository.save(user);
        return user.getUserSeq();
    }


    // public List<UserDto> findListUser() {
    public Map<String, Object> findListUser(Map<String, Object> paramMap) {
        List<User> userList = userRepository.findAll();
        List<UserDto> userDtoList = userList.stream().map(UserDto::new).collect(Collectors.toList());
        //List<MissionResponse> changeMissionList = missionList.stream().map(MissionResponse::new).collect(Collectors.toList());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            if (paramMap.get("draw") != null) resultMap = PagingUtil.createPagingMap(paramMap, userDtoList);
            else {
                resultMap.put("data", userDtoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
    //userList.stream().map(UserDto::new).collect(Collectors.toList());
    // return userList.stream().collect(Collectors.toMap(User::getUserId, UserDto::new));


}
