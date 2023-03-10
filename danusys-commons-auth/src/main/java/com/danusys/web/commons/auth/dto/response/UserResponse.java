package com.danusys.web.commons.auth.dto.response;

import com.danusys.web.commons.auth.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private int userSeq;
    private String userId; //에러가 날경우 userId-> userName auth 쪽에있는 get set userId userName으로 바꿀 것 (repository도 수정)
    private String userName; //위에 문제로 바꿨을 경우 이것도 수정
    private String email;
    private String tel;
    private String address;
    private int status;
    private String statusName;
    private String detailAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertDt;
    private int insertUserSeq;
    private String insertUserId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp updateDt;
    private int updateUserSeq;
    private String updateUserId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp lastLoginDt;
    private String checked;
    private UserStatusDto userStatus;

    public UserResponse(User user) {
        this.userSeq = user.getUserSeq();
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.tel = user.getTel();
        this.address = user.getAddress();
        this.status = Integer.parseInt(user.getStatus());
        this.detailAddress = user.getDetailAddress();
        this.lastLoginDt = user.getLastLoginDt();
        this.insertUserSeq = user.getInsertUserSeq();
        this.updateUserSeq = user.getUpdateUserSeq();
        this.insertDt = user.getInsertDt();
        this.updateDt = user.getUpdateDt();
        this.userStatus = new UserStatusDto(user.getUserStatus());
    }
    public UserResponse(User user, boolean inGroup) {
        this.userSeq = user.getUserSeq();
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.email = user.getEmail();
        this.tel = user.getTel();
        this.address = user.getAddress();
        this.status = Integer.parseInt(user.getStatus());
        this.detailAddress = user.getDetailAddress();
        this.lastLoginDt = user.getLastLoginDt();
        this.insertUserSeq = user.getInsertUserSeq();
        this.updateUserSeq = user.getUpdateUserSeq();
        this.insertDt = user.getInsertDt();
        this.updateDt = user.getUpdateDt();
        this.checked = inGroup ? "checked" : "unchecked";
        this.userStatus = new UserStatusDto(user.getUserStatus());
    }
}
