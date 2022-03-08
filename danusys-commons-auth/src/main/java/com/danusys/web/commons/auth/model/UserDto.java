package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {

    private int userSeq;
    private String userId;
    //만약에 에러가 날경우 userId-> username auth 쪽에있는 get set userId username으로 바꿀껏
    //repository도수정햇음
    private String userName;
    //위에 문제로 바꿨을경우 이것도 수정할것
    private String email;
    private String tel;

    private String address;

    private String status;



    private String detailAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp lastLoginDt;

    private int insertUserSeq;

    private int updateUserSeq;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp updateDt;

    public UserDto(User user) {

        this.userSeq = user.getUserSeq();
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.tel = user.getTel();
        this.address = user.getAddress();
        this.status = user.getStatus();
        this.detailAddress = user.getDetailAddress();
        this.lastLoginDt = user.getLastLoginDt();
        this.insertUserSeq = user.getInsertUserSeq();
        this.updateUserSeq = user.getUpdateUserSeq();
        this.insertDt = user.getInsertDt();
        this.updateDt = user.getUpdateDt();
    }


}
