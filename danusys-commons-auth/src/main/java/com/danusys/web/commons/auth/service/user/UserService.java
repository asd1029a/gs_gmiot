package com.danusys.web.commons.auth.service.user;


import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.dto.response.UserResponse;
import com.danusys.web.commons.auth.entity.UserSpecification;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.repository.UserGroupInUserRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.repository.UserStatusRepository;
import com.danusys.web.commons.auth.util.SHA256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
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


    public UserResponse findUser(int userSeq) {
        User user = userRepository.findByUserSeq(userSeq);

        return new UserResponse(user);
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
        Map<String, Object> resultMap = new HashMap<String, Object>();

        String keyword =  CommonUtil.validOneNull(paramMap, "keyword");

        /* 키워드 검색조건 */
        Specification<User> spec = Specification.where(UserSpecification.likeName(keyword))
                .or(UserSpecification.likeTel(keyword));

        /* 데이터 테이블 리스트 조회*/
        try {
            if (paramMap.get("draw") != null) {
                /* 페이지 및 멀티소팅 */
                Pageable pageable = PagingUtil.getPageableWithSort((int) paramMap.get("start"), (int) paramMap.get("length"), new ArrayList<>());

                Page<User> userPageList = userRepository.findAll(spec, pageable);
                List<UserResponse> groupResponseList = userPageList.getContent().stream()
                        .map(user -> {
                            String status = userStatusRepository.findByCodeValue(user.getStatus()).getCodeName();

                            return new UserResponse(user, status);
                        })
                        .collect(Collectors.toList());
                Map<String, Object> pagingMap = new HashMap<>();
                pagingMap.put("data", groupResponseList); // 페이징 + 검색조건 결과
                pagingMap.put("count", userPageList.getTotalElements()); // 검색조건이 반영된 총 카운트
                resultMap = PagingUtil.createPagingMap(paramMap, pagingMap);

                /* 일반 리스트 조회 */
            } else {
                List<UserResponse> userResponseList = userRepository.findAll(spec).stream()
                        .map(user -> {
                            String status = userStatusRepository.findByCodeValue(user.getStatus()).getCodeName();

                            return new UserResponse(user, status);
                        })
                        .collect(Collectors.toList());
                Map<String, Object> pagingMap = new HashMap<>();
                resultMap.put("data", userResponseList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

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
