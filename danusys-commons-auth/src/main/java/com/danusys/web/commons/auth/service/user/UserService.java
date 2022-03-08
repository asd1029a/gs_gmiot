package com.danusys.web.commons.auth.service.user;


import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserDto;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.repository.UserStatusRepository;
import com.danusys.web.commons.auth.util.SHA256;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class UserService {

    private final UserRepository userRepository;
    private final UserGroupInUserRepository userGroupInUserRepository;
    private final UserStatusRepository userStatusRepository;

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
        UserDto userDto = new UserDto(user.getUserSeq(), user.getUserId(), user.getUserName(), user.getEmail(), user.getTel(), user.getAddress(),
                user.getStatus(), user.getDetailAddress(),
                user.getLastLoginDt(), user.getInsertUserSeq(), user.getUpdateUserSeq(), user.getInsertDt(), user.getUpdateDt());
//        user.setUserGroupInUser(userGroupInUserRepository.findByUser(user));

        return userDto;
    }

    @Transactional
    public int updateUser(User user) {
        User findUser = userRepository.findByUserSeq(user.getUserSeq());

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        CommonsUserDetails userDetails = (CommonsUserDetails) principal;
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

//            findUser.setUpdateUserSeq(userDetails.getUserSeq());
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            findUser.setUpdateDt(timestamp);


        } else {
            return 0;
        }

        //    return userRepository.save(findUser);
        return findUser.getUserSeq();
    }

    @Transactional
    public User updateUser(String userName, String refreshToken) {
        User findUser = this.findUser(userName, "Error update user id");
        if(findUser!=null)
            findUser.setRefreshToken(refreshToken);
        return findUser;
    }

    @Transactional
    public void deleteUser(User user) {
        User findUser = userRepository.findByUserSeq(user.getUserSeq());
        findUser.setStatus("2");
    }


    @Transactional
    public int saveUser(User user) {

        if (user.getUserId() == null || user.getPassword() == null)
            return -1;

        User findUser = userRepository.findByUserId(user.getUserId());
        if (findUser != null)
            return 0;
        SHA256 sha256 = new SHA256();


        try {
            String cryptoPassword = sha256.encrypt(user.getPassword());
            user.setPassword("{SHA-256}" + cryptoPassword);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //  user.setInsertUserSeq(LoginInfoUtil.getUserDetails().getUserSeq());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setInsertDt(timestamp);

        userRepository.save(user);
        return user.getUserSeq();
    }

    public Map<String, Object> findListUser(Map<String, Object> paramMap) {

        int start = 0;
        int length = 1;
        int count = 0;
        int draw = 0;

        if (paramMap.get("start") != null)
            start = Integer.parseInt(paramMap.get("start").toString());
        if (paramMap.get("length") != null)
            length = Integer.parseInt(paramMap.get("length").toString());

        if (paramMap.get("draw") != null) {
            draw = Integer.parseInt(paramMap.get("draw").toString());
        }
        PageRequest pageRequest = PageRequest.of(start / length, length);

        Page<User> userPageList = null;
        //   log.info("totalPage={}",userList2.getTotalPages());

        List<User> userList = null;
        if (paramMap.get("userName") != null && length != 1) {
//            String userName = paramMap.get("userName").toString();
//            userPageList = userRepository.findAllByUserNameLike("%" + userName + "%", pageRequest);
//            userList = userPageList.toList();
//            count = (int) userPageList.getTotalElements();
            Map<String, Object> filter = new HashMap<>();
            filter.put("userName", paramMap.get("userName"));
            if (paramMap.get("tel") != null)
                filter.put("tel", paramMap.get("tel"));
//            userPageList = userRepository.findAll(UserSpecs.withTitle(filter), pageRequest);
            userList = userPageList.toList();
            count = (int) userPageList.getTotalElements();
            log.info("count={}", count);
        } else if (paramMap.get("userName") == null) {
            userPageList = userRepository.findAll(pageRequest);
            userList = userPageList.toList();
            count = (int) userPageList.getTotalElements();
        }
        userList.forEach(r -> {
            //log.info(r.getStatus());
            r.setStatus(userStatusRepository.findByCodeValue(r.getStatus()).getCodeName());
        });
        List<UserDto> userDtoList = userList.stream().map(UserDto::new).collect(Collectors.toList());


        //List<MissionResponse> changeMissionList = missionList.stream().map(MissionResponse::new).collect(Collectors.toList());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            if (paramMap.get("draw") != null) {
                Map<String, Object> pagingMap = new HashMap<>();
                pagingMap.put("data", userDtoList); // 페이징 + 검색조건 결과
                pagingMap.put("count", count); // 검색조건이 반영된 총 카운트
                resultMap = PagingUtil.createPagingMap(paramMap, pagingMap);
            } else {
                resultMap.put("data", userDtoList);
                resultMap.put("count", count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
    //userList.stream().map(UserDto::new).collect(Collectors.toList());
    // return userList.stream().collect(Collectors.toMap(User::getUserId, UserDto::new));

    public int getUserSize() {
        return userRepository.findAll().size();
    }

    public int idCheck(String userId) {

        User user = userRepository.findByUserId(userId);
        return (user == null) ? 1 : 0;
    }


//
//    public com.danusys.web.commons.app.model.paging.Page<List<Map<String, Object>>> getLists(PagingRequest pagingRequest) {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//
////            List<Map<String, Object>> lists = objectMapper
////                    .readValue(getClass().getClassLoader().getResourceAsStream("notice.json"),
////                            new TypeReference<List<Map<String, Object>>>() {});
//
////            Map<String, Object> lists=  userRepository.findByUserId("asd").stream().collect(Collectors.toMap(
////                    User::getUserId,User::getUserSeq,(oldValue,newValue) ->{
////                            log.info("oldValue:{} new VAlue: {}", oldValue,newValue);
////                            return oldValue;
////                    })
////            );
//            List<Map<String,Object>> lists =userRepository.findAllByAddressLike("%"+"독"+"%");
//
//            return Paging.getPage(lists, pagingRequest);
//
//
//        //return new com.danusys.web.commons.app.model.paging.Page<>();
//    }

}
