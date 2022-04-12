package com.danusys.web.commons.auth.dto.request;


import lombok.Data;

@Data
public class UserGroupPermitRequest {
    private int insertUserSeq ;
    private int permitSeq;
    private int userGroupSeq;
}
