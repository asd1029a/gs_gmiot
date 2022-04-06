package com.danusys.web.commons.auth.session.dto.request;

import com.danusys.web.commons.auth.session.model.UserInGroup;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserRequest {
    private int userSeq;
    private String userId;
    private String password;
    private String userName;
    private String email;
    private String tel;
    private String address;
    private String detailAddress;
    private Timestamp lastLoginDt;
    private int insertUserSeq;
    private int updateUserSeq;
    private Timestamp insertDt;
    private Timestamp updateDt;
    private String refreshToken;
    private String status;
    private List<UserInGroup> userInGroup = new ArrayList<>();
    private List<Integer> userGroupSeqList = new ArrayList<>();
    private List<Integer> userSeqList = new ArrayList<>();
}
