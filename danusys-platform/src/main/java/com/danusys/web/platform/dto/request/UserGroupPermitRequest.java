package com.danusys.web.platform.dto.request;


import lombok.Data;

@Data
public class UserGroupPermitRequest {
    private int insertUserSeq ;
    private int permitSeq;
    private int userGroupSeq;
}
