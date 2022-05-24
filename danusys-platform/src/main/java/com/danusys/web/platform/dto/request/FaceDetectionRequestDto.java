package com.danusys.web.platform.dto.request;

import com.danusys.web.platform.entity.Notice;
import lombok.*;

/**
 * 클래스이름 : FaceDetectionRequestDto
 * <p>
 * 작성자 : 강명훈 주임연구원
 * 작성일 : 2022-03-07
 * 설명 : 얼굴 검출  Data Transfer Object, 데이터 요청시 해당 클래스로 매핑할 것
 **/

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FaceDetectionRequestDto {
    private String faceName;
    private Integer faceAge;
    private Integer faceGender;
    private float faceSimilarity;
    private String faceStatus;
    private String faceKind;
    private String faceFile;

    @Builder
    public FaceDetectionRequestDto(
            String faceName, Integer faceAge, Integer faceGender,
            float faceSimilarity, String faceStatus, String faceKind, String faceFile
    ) {
        this.faceName = faceName;
        this.faceAge = faceAge;
        this.faceGender = faceGender;
        this.faceSimilarity = faceSimilarity;
        this.faceStatus = faceStatus;
        this.faceKind = faceKind;
        this.faceFile = faceFile;
    }
}
