package com.danusys.web.platform.entity;

import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @클래스이름 : Notice
 *
 * @작성자 : 강명훈 주임연구원
 * @작성일 : 2022-03-07
 * @설명 : 공지사항 Entity 클래스, DB와 직접 연결 되는 Object 이므로 요청 또는 반환시 사용해서는 안됨
 *
**/

@Getter
@NoArgsConstructor
@Entity
@Table(name = "t_notice")
public class Notice {

    @Id
    @Column(name = "notice_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeSeq;

    @Column(name = "notice_title")
    private String noticeTitle;

    @Column(name = "notice_content")
    private String noticeContent;

    @Column(name = "notice_file")
    private String noticeFile;

    @Column(name = "insert_user_seq")
    private Integer insertUserSeq;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "insert_dt")
    private Timestamp insertDt;

    @Column(name = "update_user_seq")
    private Integer updateUserSeq;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "update_dt")
    private Timestamp updateDt;

    @Builder
    public Notice(String noticeTitle, String noticeContent, String noticeFile, Integer insertUserSeq) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeFile = noticeFile;
        this.insertUserSeq = insertUserSeq;
    }

    @PrePersist
    public void insertDt() {
        this.insertDt = Timestamp.valueOf(LocalDateTime.now());
    }

    public void update(String noticeTitle, String noticeContent, String noticeFile) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeFile = noticeFile;
        this.updateUserSeq = LoginInfoUtil.getUserDetails().getUserSeq();
        this.updateDt = Timestamp.valueOf(LocalDateTime.now());
    }
}
