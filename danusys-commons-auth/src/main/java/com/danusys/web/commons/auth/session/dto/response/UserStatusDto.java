package com.danusys.web.commons.auth.session.dto.response;


import com.danusys.web.commons.auth.session.model.UserStatus;
import lombok.*;

@Getter
@Setter
public class UserStatusDto {
    private String codeId;
    private String codeName;
    private int codeValue;

    public UserStatusDto(UserStatus userStatus) {
        this.codeName = userStatus.getCodeName();
        this.codeValue = Integer.parseInt(userStatus.getCodeValue());
    }
}
