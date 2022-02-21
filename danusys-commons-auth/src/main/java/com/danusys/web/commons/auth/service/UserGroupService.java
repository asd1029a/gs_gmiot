package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.dto.response.GroupResponse;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserDto;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.commons.auth.util.PagingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;


    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }


    public UserGroup findUserGroup(String groupName, String errorMessage) {
        return userGroupRepository.findByGroupName(groupName);

    }

    //    @Transactional(readOnly = true)
    public GroupResponse findUserGroupResponseByGroupSeq(int groupSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(groupSeq);
        GroupResponse groupResponse = new GroupResponse(findUserGroup.getUserGroupSeq(), findUserGroup.getGroupName(), findUserGroup.getGroupDesc(), findUserGroup.getInsertDt(),
                findUserGroup.getInsertUserSeq(), findUserGroup.getUpdateDt(), findUserGroup.getUpdateUserSeq());

        return groupResponse;
    }

    //    @Transactional(readOnly = true)
    public UserGroup findUserGroupByGroupSeq(int groupSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(groupSeq);


        return findUserGroup;
    }

    @Transactional
    public int updateUserGroup(UserGroup userGroup) {

        UserGroup findUserGroup = this.findUserGroupByGroupSeq(userGroup.getUserGroupSeq());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommonsUserDetails userDetails = (CommonsUserDetails) principal;
        // log.info("{}",userDetails.getUserSeq());
        if (findUserGroup == null) {
            return 0;
        }
        if (userGroup.getGroupDesc() != null)
            findUserGroup.setGroupDesc(userGroup.getGroupDesc());
        if (userGroup.getGroupName() != null)
            findUserGroup.setGroupName(userGroup.getGroupName());

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        findUserGroup.setUpdateUserSeq(userDetails.getUserSeq());
        findUserGroup.setUpdateDt(timestamp);


        //    return userRepository.save(findUser);
        return findUserGroup.getUserGroupSeq();
    }

    @Transactional
    public int saveUserGroup(UserGroup userGroup) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CommonsUserDetails userDetails = (CommonsUserDetails) principal;

        // log.info("{}",userDetails.getUserSeq());
        if (userGroup.getGroupName() == null || userGroup.getGroupDesc() == null) {
            return 0;
        }
        userGroup.setInsertUserSeq(userDetails.getUserSeq());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        userGroup.setInsertDt(timestamp);

        userGroupRepository.save(userGroup);
        return userGroup.getUserGroupSeq();
    }

    public void deleteUserGroup(UserGroup userGroup) {

        userGroupRepository.deleteById(userGroup.getUserGroupSeq());
    }


    public Map<String, Object> findListGroup(Map<String, Object> paramMap) {
        int start = 0;
        int length = 1;
        int count = 0;
        int draw = 0;
        String groupName = null;
        String groupDesc = null;

        if (paramMap.get("start") != null)
            start = Integer.parseInt(paramMap.get("start").toString());
        if (paramMap.get("length") != null)
            length = Integer.parseInt(paramMap.get("length").toString());

        if (paramMap.get("draw") != null) {
            draw = Integer.parseInt(paramMap.get("draw").toString());
        }
        PageRequest pageRequest = PageRequest.of(start / length, length);
        Page<UserGroup> userGroupPageList = null;
        List<UserGroup> userGroupList = null;
        if (paramMap.get("groupName") != null) {
            groupName = paramMap.get("groupName").toString();
        }

        if (paramMap.get("groupDesc") != null) {
            groupDesc = paramMap.get("groupDesc").toString();
        }

        if (groupName == null)
            groupName = "";
        if (groupDesc == null)
            groupDesc = "";
        userGroupPageList = userGroupRepository
                .findByGroupNameLikeAndGroupDescLike("%" + groupName + "%", "%" + groupDesc + "%", pageRequest);

        count = (int) userGroupPageList.getTotalElements();
        userGroupList = userGroupPageList.toList();
        userGroupList.forEach(r -> {
            if (r.getUserGroupInUser()!= null)
                log.info("user={}", r.getUserGroupInUser().getUser().getUserName());
        });
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            if (paramMap.get("draw") != null) {
                Map<String, Object> pagingMap = new HashMap<>();
                pagingMap.put("data", userGroupList); // 페이징 + 검색조건 결과
                pagingMap.put("count", count); // 검색조건이 반영된 총 카운트
                resultMap = PagingUtil.createPagingMap(paramMap, pagingMap);
            } else {
                resultMap.put("data", userGroupList);
                resultMap.put("count", count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
