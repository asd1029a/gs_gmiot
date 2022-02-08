package com.danusys.web.commons.auth.dto.response;

import com.danusys.web.commons.auth.model.UserGroupInUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInUserResponse {

    private int userSeq;
    private int userGroupSeq;
    private int insertUserSeq;
    private Timestamp insertDt;

    public GroupInUserResponse(UserGroupInUser userGroupInUser) {

        this.userSeq = userGroupInUser.getUser().getUserSeq();
        this.userGroupSeq = userGroupInUser.getUserGroup().getUserGroupSeq();
        this.insertUserSeq = userGroupInUser.getInsertUserSeq();
        this.insertDt = userGroupInUser.getInsertDt();
    }

}
