package com.danusys.web.commons.auth.dto.response;


import com.danusys.web.commons.auth.model.UserStatus;
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
