package com.danusys.web.commons.auth.service;

import com.danusys.web.commons.app.CommonUtil;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.dto.response.GroupResponse;
import com.danusys.web.commons.auth.entity.UserGroupSpecification;
import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.repository.UserGroupRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;
    String inUserId = null;

    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }


    public UserGroup findUserGroup(String groupName, String errorMessage) {
        return userGroupRepository.findByGroupName(groupName);
    }

    //    @Transactional(readOnly = true)
    public GroupResponse findUserGroupResponseByGroupSeq(int groupSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(groupSeq);

        findUserGroup.getUserGroupInUser().forEach(r -> {
            inUserId += r.getUser().getUserName() + ", ";
        });
        inUserId= StringUtils.substring(inUserId,0,-2);

        GroupResponse groupResponse = new GroupResponse(findUserGroup);

        return groupResponse;
    }

    //    @Transactional(readOnly = true)
    public GroupResponse findUserGroupByGroupSeq(int groupSeq) {
        UserGroup findUserGroup = userGroupRepository.findByUserGroupSeq(groupSeq);
        return new GroupResponse(findUserGroup);
    }

    @Transactional
    public int updateUserGroup(UserGroup userGroup) {

        GroupResponse findUserGroup = this.findUserGroupByGroupSeq(userGroup.getUserGroupSeq());
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
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        CommonsUserDetails userDetails = (CommonsUserDetails) principal;

        // log.info("{}",userDetails.getUserSeq());
        if (userGroup.getGroupName() == null || userGroup.getGroupDesc() == null) {
            return 0;
        }
//        userGroup.setInsertUserSeq(userDetails.getUserSeq());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        userGroup.setInsertDt(timestamp);

        userGroupRepository.save(userGroup);
        return userGroup.getUserGroupSeq();
    }

    public void deleteUserGroup(UserGroup userGroup) {
        userGroupRepository.deleteById(userGroup.getUserGroupSeq());
    }

    @Transactional
    public Map<String, Object> findListGroup(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        String keyword =  CommonUtil.validOneNull(paramMap, "keyword");

        /* 키워드 검색조건 */
        Specification<UserGroup> spec = Specification.where(UserGroupSpecification.likeGroupName(keyword))
                .or(UserGroupSpecification.likeGroupDesc(keyword));

        /* 데이터 테이블 리스트 조회*/
        try {
            if (paramMap.get("draw") != null) {
                /* 페이지 및 멀티소팅 */
                Pageable pageable = PagingUtil.getPageableWithSort((int) paramMap.get("start"), (int) paramMap.get("length"), new ArrayList<>());

                Page<UserGroup> userGroupPageList = userGroupRepository.findAll(spec, pageable);

                List<GroupResponse> groupResponseList = userGroupPageList.getContent().stream()
                        .map(GroupResponse::new)
                        .collect(Collectors.toList());
                Map<String, Object> pagingMap = new HashMap<>();
                pagingMap.put("data", groupResponseList); // 페이징 + 검색조건 결과
                pagingMap.put("count", userGroupPageList.getTotalElements()); // 검색조건이 반영된 총 카운트
                resultMap = PagingUtil.createPagingMap(paramMap, pagingMap);

            /* 일반 리스트 조회 */
            } else {
                List<GroupResponse> groupResponseList = userGroupRepository.findAll(spec).stream()
                        .map(GroupResponse::new)
                        .collect(Collectors.toList());
                Map<String, Object> pagingMap = new HashMap<>();
                resultMap.put("data", groupResponseList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultMap;
    }
}
