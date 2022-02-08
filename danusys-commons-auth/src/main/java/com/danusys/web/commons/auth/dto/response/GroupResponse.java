package com.danusys.web.commons.auth.dto.response;

import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    private int userGroupSeq;
    private String groupName;
    private String groupDesc;
    private Timestamp insertDt;
    private int insertUserSeq;
    private Timestamp updateDt;
    private int updateUserSeq;



    public GroupResponse(UserGroup userGroup) {
        this.userGroupSeq = userGroup.getUserGroupSeq();
        this.groupName = userGroup.getGroupName();
        this.groupDesc = userGroup.getGroupDesc();
        this.insertDt = userGroup.getInsertDt();
        this.insertUserSeq = userGroup.getInsertUserSeq();
        this.updateDt = userGroup.getUpdateDt();
        this.updateUserSeq = userGroup.getUpdateUserSeq();
    }


}
