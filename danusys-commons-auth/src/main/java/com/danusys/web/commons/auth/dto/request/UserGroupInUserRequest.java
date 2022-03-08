package com.danusys.web.commons.auth.dto.request;


import lombok.Data;

@Data
public class UserGroupInUserRequest {
    private int insertUserSeq ;
    private int userSeq;
    private int userGroupSeq;

}
