package com.danusys.web.commons.auth.entity;

import com.danusys.web.commons.auth.model.UserGroup;
import org.springframework.data.jpa.domain.Specification;
/**
 *
 * 클래스이름 : UserGroupSpecification
 *
 * 작성자 : 이유나 연구원
 * 작성일 : 2022-03-09
 * 설명 : user Group Specification 클래스, 쿼리 조건문 기술
 *
 **/

public class UserGroupSpecification {
    public static Specification<UserGroup> defaultWhere() {
        return (root, query, cb) -> cb.isTrue(cb.literal(true));
    }

    public static Specification<UserGroup> likeGroupName(String groupName) {
        return (root, query, cb) -> cb.like(root.get("groupName"), "%" + groupName + "%");
    }

    public static Specification<UserGroup> likeGroupDesc(String groupDesc) {
        return (root, query, cb) -> cb.like(root.get("groupDesc"), "%" + groupDesc + "%");
    }
}
