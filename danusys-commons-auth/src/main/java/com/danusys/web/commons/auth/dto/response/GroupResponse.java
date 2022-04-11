package com.danusys.web.commons.auth.dto.response;

import com.danusys.web.commons.auth.model.UserGroup;
import com.danusys.web.commons.auth.model.UserGroupPermit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

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
    private String inUserId = "";

    //private List<UserGroupPermit> userGroupPermit;
    private List<UserGroupPermitResponse> userGroupPermit;
    //    private List<UserGroupInUser> userGroupInUser;
    private String checked;
    private int userGroupStatus;

    public GroupResponse(UserGroup userGroup) {
        this.userGroupSeq = userGroup.getUserGroupSeq();
        this.groupName = userGroup.getGroupName();
        this.groupDesc = userGroup.getGroupDesc();
        this.insertDt = userGroup.getInsertDt();
        this.insertUserSeq = userGroup.getInsertUserSeq();
        this.updateDt = userGroup.getUpdateDt();
        this.updateUserSeq = userGroup.getUpdateUserSeq();
        userGroup.getUserInGroup().forEach(r -> {
            this.inUserId += r.getUser().getUserName() + ", ";
        });
        this.inUserId = StringUtils.substring(inUserId, 0, -2);
        this.userGroupPermit = userGroup.getUserGroupPermit().stream().map(UserGroupPermitResponse::new).collect(Collectors.toList());

        this.userGroupStatus = Integer.parseInt(userGroup.getUserGroupStatus());
    }

    public GroupResponse(UserGroup userGroup, boolean inUser) {
        this.userGroupSeq = userGroup.getUserGroupSeq();
        this.groupName = userGroup.getGroupName();
        this.groupDesc = userGroup.getGroupDesc();
        this.insertDt = userGroup.getInsertDt();
        this.insertUserSeq = userGroup.getInsertUserSeq();
        this.updateDt = userGroup.getUpdateDt();
        this.updateUserSeq = userGroup.getUpdateUserSeq();
        this.userGroupPermit = userGroup.getUserGroupPermit().stream().map(UserGroupPermitResponse::new).collect(Collectors.toList());
        this.userGroupStatus = Integer.parseInt(userGroup.getUserGroupStatus());
        this.checked = inUser ? "checked" : "unchecked";
    }
}
