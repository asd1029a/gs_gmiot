package com.danusys.web.platform.dto.request;

import lombok.*;

/**
 * packageName : com.danusys.web.platform.dto.request
 * fileName : SignageDto
 * author : brighthoon94
 * date : 2022-05-11
 * description :
 * ===========================================================
 * DATE     AUTHOR      NOTE
 * -----------------------------------------------------------
 * 2022-05-11  brighthoon94      최초 생성
 */

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignageRequestDto {

    private Integer templateSeq;
    private String templateContent;

    @Builder
    public SignageRequestDto(Integer templateSeq, String templateContent) {
        this.templateSeq = templateSeq;
        this.templateContent = templateContent;
    }
}
