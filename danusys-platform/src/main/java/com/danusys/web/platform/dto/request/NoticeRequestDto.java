package com.danusys.web.platform.dto.request;

import com.danusys.web.platform.entity.Notice;
import lombok.*;

/**
 *
 * 클래스이름 : NoticeRequestDto
 *
 * 작성자 : 강명훈 주임연구원
 * 작성일 : 2022-03-07
 * 설명 : 공지사항 요청 Data Transfer Object, 데이터 요청시 해당 클래스로 매핑할 것
 *
**/

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoticeRequestDto {

    private String noticeTitle;
    private String noticeContent;
    private String noticeFile;
    private Integer insertUserSeq;

    @Builder
    public NoticeRequestDto(String noticeTitle, String noticeContent, String noticeFile, Integer insertUserSeq) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.noticeFile = noticeFile;
        this.insertUserSeq = insertUserSeq;
    }

    public Notice toEntity() {
        return Notice.builder()
                .noticeTitle(noticeTitle)
                .noticeContent(noticeContent)
                .noticeFile(noticeFile)
                .insertUserSeq(insertUserSeq)
                .build();
    }
}
