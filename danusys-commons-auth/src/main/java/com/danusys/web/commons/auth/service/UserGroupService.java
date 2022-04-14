package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.dto.response.GroupResponse;
import com.danusys.web.commons.auth.entity.UserGroupSpecification;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserInGroup;
import com.danusys.web.commons.auth.service.repository.UserGroupPermitRepository;
import com.danusys.web.commons.auth.service.repository.UserGroupRepository;
import com.danusys.web.commons.auth.service.repository.UserInGroupRepository;
import com.danusys.web.commons.auth.service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserGroupService {

    private final UserRepository userRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserInGroupRepository userInGroupRepository;
    private final UserGroupPermitRepository userGroupPermitRepository;
    private final UserGroupPermitService userGroupPermitService;
    private final UserInGroupService userInGroupService;

//    @Transactional(readOnly = true)
//    public GroupResponse getOneByGroupSeq(int groupSeq) {
//        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(groupSeq);
//
//        findUserGroup.getUserGroupInUser().forEach(r -> {
//            inUserId += r.getUser().getUserName() + ", ";
//        });
//        inUserId= StringUtils.substring(inUserId,0,-2);
//
//        GroupResponse groupResponse = new GroupResponse(findUserGroup);
//
//        return groupResponse;
//    }

    public int checkGroupName(String groupName) {
        UserGroup userGroup = userGroupRepository.findByGroupName(groupName);
        return (userGroup == null) ? 1 : 0;
    }

    @Transactional(readOnly = true)
    public GroupResponse getOneByGroupSeq(int groupSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(groupSeq);
        return new GroupResponse(findUserGroup);
    }

    /* 일반 리스트 조회 */
    @Transactional
    public Map<String, Object> getList(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        /* 키워드 검색조건 */
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");

        Specification<UserGroup> spec = Specification.where(UserGroupSpecification.likeGroupName(keyword))
                .or(UserGroupSpecification.likeGroupDesc(keyword));

        List<GroupResponse> groupResponseList = userGroupRepository.findAll(spec).stream()
                .map(GroupResponse::new)
                .collect(Collectors.toList());
        resultMap.put("data", groupResponseList);

        return resultMap;
    }

    /* 데이터 테이블 리스트 조회*/
    @Transactional
    public Map<String, Object> getListPaging(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            /* 키워드 검색조건 */
            String keyword = CommonUtil.validOneNull(paramMap, "keyword");

            Specification<UserGroup> spec = Specification.where(UserGroupSpecification.likeGroupName(keyword))
                    .or(UserGroupSpecification.likeGroupDesc(keyword));
            /* 페이지 및 멀티소팅 */
            List<Sort.Order> orders = new ArrayList<>();
            Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "insertDt");
            orders.add(order1);
            Pageable pageable = PagingUtil.getPageableWithSort((int) paramMap.get("start"), (int) paramMap.get("length"), orders);

            Page<UserGroup> userGroupPageList = userGroupRepository.findAll(spec, pageable);

            List<GroupResponse> groupResponseList = userGroupPageList.getContent().stream()
                    .map(GroupResponse::new)
                    .collect(Collectors.toList());
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", groupResponseList); // 페이징 + 검색조건 결과
            pagingMap.put("count", userGroupPageList.getTotalElements()); // 검색조건이 반영된 총 카운트
            resultMap = PagingUtil.createPagingMap(paramMap, pagingMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    /* 일반 리스트 조회 */
    public Map<String, Object> getListUserInGroup(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        /* 키워드 검색조건 */
        String keyword = CommonUtil.validOneNull(paramMap, "keyword");

        Specification<UserGroup> spec = Specification.where(UserGroupSpecification.likeGroupName(keyword))
                .or(UserGroupSpecification.likeGroupDesc(keyword));
        User user = userRepository.findByUserSeq((int) paramMap.get("userSeq"));
        List<UserInGroup> userInGroup = userInGroupRepository.findAllByUser(user);

        /* response에 추가된 데이터로 정렬할 때 */
        Comparator<GroupResponse> compare = Comparator
                .comparing(GroupResponse::getChecked)
                .thenComparing(GroupResponse::getUserGroupSeq);

        List<GroupResponse> groupResponseList = userGroupRepository.findAll(spec).stream()
                .map(r -> {
                    boolean inUser = userInGroup.stream()
                            .filter(ugiu -> r.getUserGroupSeq() == ugiu.getUserGroup().getUserGroupSeq())
                            .collect(Collectors.toList())
                            .isEmpty();
                    return new GroupResponse(r, !inUser);
                })
                .sorted(compare)
                .collect(Collectors.toList());
        resultMap.put("data", groupResponseList);

        return resultMap;
    }

    /* 데이터 테이블 리스트 조회*/
    public Map<String, Object> getListUserInGroupPaging(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            /* 키워드 검색조건 */
            String keyword = CommonUtil.validOneNull(paramMap, "keyword");

            Specification<UserGroup> spec = Specification.where(UserGroupSpecification.likeGroupName(keyword))
                    .or(UserGroupSpecification.likeGroupDesc(keyword));
            User user = userRepository.findByUserSeq((int) paramMap.get("userSeq"));
            List<UserInGroup> userInGroup = userInGroupRepository.findAllByUser(user);

            /* 페이지 및 멀티소팅 */
            Pageable pageable = PagingUtil.getPageableWithSort((int) paramMap.get("start"), (int) paramMap.get("length"), new ArrayList<>());

            Page<UserGroup> userGroupPageList = userGroupRepository.findAll(spec, pageable);

            /* response에 추가된 데이터로 정렬할 때 */
            Comparator<GroupResponse> compare = Comparator
                    .comparing(GroupResponse::getChecked)
                    .thenComparing(GroupResponse::getUserGroupSeq);

            List<GroupResponse> groupResponseList = userGroupPageList.getContent().stream()
                    .map(r -> {
                        boolean inUser = userInGroup.stream()
                                .filter(ugiu -> r.getUserGroupSeq() == ugiu.getUserGroup().getUserGroupSeq())
                                .collect(Collectors.toList())
                                .isEmpty();
                        return new GroupResponse(r, !inUser);
                    })
                    .sorted(compare)
                    .collect(Collectors.toList());
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", groupResponseList); // 페이징 + 검색조건 결과
            pagingMap.put("count", userGroupPageList.getTotalElements()); // 검색조건이 반영된 총 카운트
            resultMap = PagingUtil.createPagingMap(paramMap, pagingMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }

    @Transactional
    public int add(Map<String, Object> paramMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserGroup userGroup = objectMapper.convertValue(paramMap, UserGroup.class);

        if (userGroup.getGroupName() == null || userGroup.getGroupDesc() == null) {
            return 0;
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        userGroup.setInsertDt(timestamp);

        userGroupRepository.save(userGroup);

        paramMap.put("userGroupSeqList", Arrays.asList(userGroup.getUserGroupSeq()));
        paramMap.put("userGroupSeq", userGroup.getUserGroupSeq());
        userInGroupService.add(paramMap);
        userGroupPermitService.add(paramMap);

        return userGroup.getUserGroupSeq();
    }

    @Transactional
    public int mod(Map<String, Object> paramMap) {
        ObjectMapper objectMapper = new ObjectMapper();
        UserGroup userGroup = objectMapper.convertValue(paramMap, UserGroup.class);
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(userGroup.getUserGroupSeq());

        userInGroupService.delUserGroupSeq(userGroup.getUserGroupSeq());
        userInGroupService.add(paramMap);

        userGroupPermitService.delByUserGroupSeq(userGroup.getUserGroupSeq());
        userGroupPermitService.add(paramMap);

        if (findUserGroup == null) {
            return 0;
        } else {
            if (userGroup.getGroupDesc() != null)
                findUserGroup.setGroupDesc(userGroup.getGroupDesc());
            if (userGroup.getGroupName() != null)
                findUserGroup.setGroupName(userGroup.getGroupName());
            if (userGroup.getUserGroupStatus() != null)
                findUserGroup.setUserGroupStatus(userGroup.getUserGroupStatus());

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            CommonsUserDetails userDetails = (CommonsUserDetails) principal;

            findUserGroup.setUpdateUserSeq(userDetails.getUserSeq());
            findUserGroup.setUpdateDt(timestamp);

            return findUserGroup.getUserGroupSeq();
        }
    }

    @Transactional
    public void del(int userGroupSeq) {
        userInGroupRepository.deleteAllByUserGroupSeq(userGroupSeq);
        userGroupPermitRepository.deleteByUserGroupSeq(userGroupSeq);
        userGroupRepository.deleteById(userGroupSeq);
    }
}
