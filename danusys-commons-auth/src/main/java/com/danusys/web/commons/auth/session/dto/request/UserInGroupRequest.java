package com.danusys.web.commons.auth.session.dto.request;


import lombok.Data;

@Data
public class UserInGroupRequest {
    private int insertUserSeq ;
    private int userSeq;
    private int userGroupSeq;
}
