package com.danusys.web.commons.auth.dto.response;

import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.PermitMenu;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserGroupPermitResponse {

    private int id;
    private Timestamp insertDt;
    private int insertUserSeq;
    private int userGroupSeq;
    private int permitSeq;
    private int permitMenuSeq;
    private Permit permit;
    private PermitMenu permitMenu;

    public UserGroupPermitResponse(UserGroupPermit userGroupPermit){

        this.id=userGroupPermit.getId();
        this.insertDt=userGroupPermit.getInsertDt();
        this.insertUserSeq=userGroupPermit.getInsertUserSeq();
        this.userGroupSeq=userGroupPermit.getUserGroupSeq();
        this.permitSeq=userGroupPermit.getPermitSeq();
        this.permitMenuSeq=userGroupPermit.getPermitMenuSeq();
        this.permit=userGroupPermit.getPermit();
        this.permitMenu=userGroupPermit.getPermitMenu();
    }
}
