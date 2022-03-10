package com.danusys.web.platform.entity;

import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;

/**
 *
 * 클래스이름 : NoticeSpecification
 *
 * 작성자 : 강명훈 주임연구원
 * 작성일 : 2022-03-07
 * 설명 : 공지사항 Specification 클래스, 쿼리 조건문 기술
 *
**/

public class NoticeSpecification {

    public static Specification<Notice> defaultWhere() {
        return (root, query, cb) -> cb.isTrue(cb.literal(true));
    }

    public static Specification<Notice> likeTitle(String noticeTitle) {
        return (root, query, cb) -> cb.like(root.get("noticeTitle"), "%" + noticeTitle + "%");
    }

    public static Specification<Notice> likeContent(String noticeContent) {
        return (root, query, cb) -> cb.like(root.get("noticeContent"), "%" + noticeContent + "%");
    }

    public static Specification<Notice> betweenDateTime(Timestamp startDatetime, Timestamp endDatetime) {
        return (root, query, cb) -> {
            if(startDatetime == null) {
                return cb.lessThanOrEqualTo(root.get("insertDt"), endDatetime);
            } else if(endDatetime == null) {
                return cb.greaterThanOrEqualTo(root.get("insertDt"), startDatetime);
            } else {
                return cb.between(root.get("insertDt"), startDatetime, endDatetime);
            }
        };
    }
}
