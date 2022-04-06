package com.danusys.web.commons.auth.session.entity;

import com.danusys.web.commons.auth.session.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

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
    public static Specification<User> likeId(String keyword) {
        return (root, query, cb) -> cb.like(root.get("userId"), "%" + keyword + "%");
    }
    public static Specification<User> inStatus(List<String> status) {
        return (root, query, cb) -> root.get("status").in(status);
    }
}
