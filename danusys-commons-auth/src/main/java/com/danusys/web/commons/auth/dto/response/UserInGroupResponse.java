package com.danusys.web.commons.auth.dto.response;

import com.danusys.web.commons.auth.model.UserInGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInGroupResponse {

    private int userSeq;
    private int userGroupSeq;
    private int insertUserSeq;
    private Timestamp insertDt;

    public UserInGroupResponse(UserInGroup userInGroup) {

        this.userSeq = userInGroup.getUser().getUserSeq();
        this.userGroupSeq = userInGroup.getUserGroup().getUserGroupSeq();
        this.insertUserSeq = userInGroup.getInsertUserSeq();
        this.insertDt = userInGroup.getInsertDt();
    }

}
