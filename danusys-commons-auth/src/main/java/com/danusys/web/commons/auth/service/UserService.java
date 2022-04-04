package com.danusys.web.commons.auth.service;


import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.dto.request.UserRequest;
import com.danusys.web.commons.auth.dto.response.UserResponse;
import com.danusys.web.commons.auth.entity.UserSpecification;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserInGroup;
import com.danusys.web.commons.auth.model.UserStatus;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import com.danusys.web.commons.auth.repository.UserInGroupRepository;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.repository.UserStatusRepository;
import com.danusys.web.commons.auth.util.SHA256;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserInGroupRepository userInGroupRepository;
    private final UserStatusRepository userStatusRepository;

    public User get(String userName, String errorMessage) {
        return userRepository.findByUserId(userName);
    }

    public UserResponse get(String userName) {
        User user = userRepository.findByUserId(userName);
        return new UserResponse(user);
    }

    public UserResponse get(int userSeq) {
        User user = userRepository.findByUserSeq(userSeq);
        return new UserResponse(user);
    }

    /* 일반 리스트 조회 */
    public Map<String, Object> getList(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        /* 키워드 검색조건 */
        String keyword =  CommonUtil.validOneNull(paramMap, "keyword");
        List<String> statusParam = CommonUtil.valiArrNull(paramMap, "status");

        if (statusParam.isEmpty()){
            statusParam.add("''");
        }

        Specification<User> spec = Specification.where(UserSpecification.inStatus(statusParam));
        if(!keyword.equals("")) {
            spec.or(UserSpecification.likeTel(keyword))
                    .or(UserSpecification.likeId(keyword))
                    .or(UserSpecification.likeName(keyword));
        }

        List<UserResponse> userResponseList = userRepository.findAll(spec).stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());

        resultMap.put("data", userResponseList);

        return resultMap;
    }

    /* 데이터 테이블 리스트 조회*/
    public Map<String, Object> getListPaging(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        /* 키워드 검색조건 */
        String keyword =  CommonUtil.validOneNull(paramMap, "keyword");
        List<String> statusParam = CommonUtil.valiArrNull(paramMap, "status");

        if (statusParam.isEmpty()){
            statusParam.add("''");
        }

        Specification<User> spec = Specification.where(UserSpecification.inStatus(statusParam));
        if(!keyword.equals("")) {
            spec.or(UserSpecification.likeTel(keyword))
                .or(UserSpecification.likeId(keyword))
                .or(UserSpecification.likeName(keyword));
        }

        try {
            /* 페이지 및 멀티소팅 */
            Pageable pageable = PagingUtil.getPageableWithSort((int) paramMap.get("start"), (int) paramMap.get("length"), new ArrayList<>());

            Page<User> userPageList = userRepository.findAll(spec, pageable);
            List<UserResponse> userResponseList = userPageList.getContent().stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());

            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", userResponseList); // 페이징 + 검색조건 결과
            pagingMap.put("count", userPageList.getTotalElements()); // 검색조건이 반영된 총 카운트
            resultMap = PagingUtil.createPagingMap(paramMap, pagingMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    /* 일반 리스트 조회 */
    public Map<String, Object> getListGroupInUser(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        /* 키워드 검색조건 */
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");

        Specification<User> spec = Specification.where(UserSpecification.likeName(keyword))
                .or(UserSpecification.likeTel(keyword));
        UserGroup userGroup = userGroupRepository.findByUserGroupSeq((int) paramMap.get("userGroupSeq"));
        List<UserInGroup> userInGroup = userInGroupRepository.findAllByUserGroup(userGroup);

        /* response에 추가된 데이터로 정렬할 때 */
        Comparator<UserResponse> compare = Comparator
                .comparing(UserResponse::getChecked)
                .thenComparing(UserResponse::getUserSeq);

        List<UserResponse> UserResponseList = userRepository.findAll(spec).stream()
                .map(r -> {
                    boolean inGroup = userInGroup.stream()
                            .filter(user -> r.getUserSeq() == user.getUser().getUserSeq())
                            .collect(Collectors.toList())
                            .isEmpty();
                    return new UserResponse(r, !inGroup);
                })
                .sorted(compare)
                .collect(Collectors.toList());
        resultMap.put("data", UserResponseList);

        return resultMap;
    }

    /* 데이터 테이블 리스트 조회*/
    public Map<String, Object> getListGroupInUserPaging(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            /* 키워드 검색조건 */
            String keyword = CommonUtil.validOneNull(paramMap, "keyword");

            Specification<User> spec = Specification.where(UserSpecification.likeName(keyword))
                    .or(UserSpecification.likeTel(keyword));
            UserGroup userGroup = userGroupRepository.findByUserGroupSeq((int) paramMap.get("userGroupSeq"));
            List<UserInGroup> userInGroup = userInGroupRepository.findAllByUserGroup(userGroup);

            /* 페이지 및 멀티소팅 */
            Pageable pageable = PagingUtil.getPageableWithSort((int) paramMap.get("start"), (int) paramMap.get("length"), new ArrayList<>());

            Page<User> userPageList = userRepository.findAll(spec, pageable);

            /* response에 추가된 데이터로 정렬할 때 */
            Comparator<UserResponse> compare = Comparator
                    .comparing(UserResponse::getChecked)
                    .thenComparing(UserResponse::getUserSeq);

            List<UserResponse> UserResponseList = userRepository.findAll(spec).stream()
                    .map(r -> {
                        boolean inGroup = userInGroup.stream()
                                .filter(user -> r.getUserSeq() == user.getUser().getUserSeq())
                                .collect(Collectors.toList())
                                .isEmpty();
                        return new UserResponse(r, !inGroup);
                    })
                    .sorted(compare)
                    .collect(Collectors.toList());
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", UserResponseList); // 페이징 + 검색조건 결과
            pagingMap.put("count", userPageList.getTotalElements()); // 검색조건이 반영된 총 카운트
            resultMap = PagingUtil.createPagingMap(paramMap, pagingMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    public int getUserSize() {
        return userRepository.findAll().size();
    }

    @Transactional
    public int add(User user) {

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

    @Transactional
    public int mod(User User) {
        User findUser = userRepository.findByUserSeq(User.getUserSeq());

        if (findUser != null) {
            if (User.getPassword() != null) {
                SHA256 sha256 = new SHA256();
                try {
                    String cryptoPassword = sha256.encrypt(User.getPassword());
                    findUser.setPassword("{SHA-256}" + cryptoPassword);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

            }
            if (User.getUserName() != null)
                findUser.setUserName(User.getUserName());
            if (User.getEmail() != null)
                findUser.setEmail(User.getEmail());
            if (User.getTel() != null)
                findUser.setTel(User.getTel());
            if (User.getAddress() != null)
                findUser.setAddress(User.getAddress());
            if (User.getStatus() != null)
                findUser.setStatus(User.getStatus());
            if (User.getDetailAddress() != null)
                findUser.setDetailAddress(User.getDetailAddress());

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            findUser.setUpdateDt(timestamp);
            userRepository.save(findUser);
        } else {
            return 0;
        }

        return findUser.getUserSeq();
    }

    @Transactional
    public User mod(String userName, String refreshToken) {
        User findUser = this.get(userName, "Error update user id");
        if(findUser!=null){
            findUser.setRefreshToken(refreshToken);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            findUser.setLastLoginDt(timestamp);
        }
        return findUser;
    }

    @Transactional
    public void del(User user) {
        User findUser = userRepository.findByUserSeq(user.getUserSeq());
        findUser.setStatus("2");
//        userRepository.save(findUser);
    }

    public int checkId(String userId) {
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
