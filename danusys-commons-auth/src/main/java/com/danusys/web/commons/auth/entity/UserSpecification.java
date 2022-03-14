package com.danusys.web.commons.auth.entity;

import com.danusys.web.commons.auth.model.User;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * 클래스이름 : UserSpecification
 *
 * 작성자 : 이유나 연구원
 * 작성일 : 2022-03-10
 * 설명 : user Specification 클래스, 쿼리 조건문 기술
 *
 **/
public class UserSpecification {

    public static Specification<User> likeName(String keyword) {
        return (root, query, cb) -> cb.like(root.get("userName"), "%" + keyword + "%");
    }
    public static Specification<User> likeTel(String keyword) {
        return (root, query, cb) -> cb.like(root.get("tel"), "%" + keyword + "%");
    }
}
