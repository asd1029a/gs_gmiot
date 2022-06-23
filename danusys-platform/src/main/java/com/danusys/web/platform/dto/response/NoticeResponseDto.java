package com.danusys.web.platform.dto.response;

import com.danusys.web.platform.entity.Notice;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.sql.Timestamp;

/**
 *
 * 클래스이름 : NoticeResponseDto
 *
 * 작성자 : 강명훈 주임연구원
 * 작성일 : 2022-03-07
 * 설명 : 공지사항 반환 Data Transfer Object, 데이터 반환시 해당 클래스로 반환할 것
 *
**/

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticeResponseDto {

    private Long noticeSeq;
    private String noticeTitle;
    private String noticeContent;
    private String noticeFile;
    private String insertUserId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertDt;
    private String updateUserId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp updateDt;

    public NoticeResponseDto(Notice notice, @Nullable String insertUserId, @Nullable String updateUserId) {
        this.noticeSeq = notice.getNoticeSeq();
        this.noticeTitle = notice.getNoticeTitle();
        this.noticeContent = notice.getNoticeContent();
        this.noticeFile = notice.getNoticeFile();
        this.insertDt = notice.getInsertDt();
        this.insertUserId = insertUserId;
        this.updateDt = notice.getUpdateDt();
        this.updateUserId = updateUserId;
    }
}
