package com.danusys.web.commons.auth.service;


import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.dto.response.UserResponse;
import com.danusys.web.commons.auth.entity.UserSpecification;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserInGroup;
import com.danusys.web.commons.auth.service.repository.UserGroupRepository;
import com.danusys.web.commons.auth.service.repository.UserInGroupRepository;
import com.danusys.web.commons.auth.service.repository.UserRepository;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.commons.auth.util.SHA256;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserInGroupService userInGroupService;

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

        /* 계정 상태 검색조건 */
        List<String> statusParam = CommonUtil.valiArrNull(paramMap, "status");

        if (statusParam.isEmpty()){
            statusParam.add("''");
        }

        Specification<User> spec = Specification.where(UserSpecification.inStatus(statusParam));
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
            /* 계정 상태 검색조건 */
            List<String> statusParam = CommonUtil.valiArrNull(paramMap, "status");

            if (statusParam.isEmpty()){
                statusParam.add("''");
            }

            Specification<User> spec = Specification.where(UserSpecification.inStatus(statusParam));
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
    public int add(Map<String, Object> paramMap) {
        /* TODO : 트랜젝션 처리 요망 */
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(paramMap, User.class);

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

        user.setInsertUserSeq(LoginInfoUtil.getUserDetails().getUserSeq());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setInsertDt(timestamp);

        userRepository.save(user);

        paramMap.put("userSeqList", Arrays.asList(user.getUserSeq()));
        userInGroupService.add(paramMap);

        return user.getUserSeq();
    }

    @Transactional
    public int mod(Map<String, Object> paramMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(paramMap, User.class);

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
            if (user.getStatus() != null)
                findUser.setStatus(user.getStatus());
            if (user.getDetailAddress() != null)
                findUser.setDetailAddress(user.getDetailAddress());

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CommonsUserDetails userDetails = (CommonsUserDetails) principal;
            findUser.setUpdateUserSeq(userDetails.getUserSeq());
            findUser.setUpdateDt(timestamp);
            userRepository.save(findUser);

            userInGroupService.delUserSeq(user.getUserSeq());
            userInGroupService.add(paramMap);
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
        userInGroupRepository.deleteAllByUserSeq(findUser.getUserSeq());
    }

    public int checkId(String userId) {
        User user = userRepository.findByUserId(userId);
        return (user == null) ? 1 : 0;
    }

    public String checkAuthority(String authName, String permit) {
        GrantedAuthority flag = LoginInfoUtil.getUserDetails().getAuthorities()
                .stream()
                .filter(auth -> auth.getAuthority().toString().equals("ROLE_" + authName + "_" + permit))
                .findFirst()
                .orElse(null);

        return flag != null ? flag.getAuthority().toString() : "none";
    }

}
