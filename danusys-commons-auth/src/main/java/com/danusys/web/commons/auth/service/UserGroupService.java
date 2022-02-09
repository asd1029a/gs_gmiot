package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.dto.response.GroupResponse;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.commons.auth.util.PagingUtil;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
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
        List<UserGroup> userGroupList = userGroupRepository.findAll();
        List<GroupResponse> groupResponseList = userGroupList.stream().map(GroupResponse::new).collect(Collectors.toList());
        //List<MissionResponse> changeMissionList = missionList.stream().map(MissionResponse::new).collect(Collectors.toList());
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            if (paramMap.get("draw") != null) resultMap = PagingUtil.createPagingMap(paramMap, groupResponseList);
            else {
                resultMap.put("data", groupResponseList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return resultMap;
    }
}
