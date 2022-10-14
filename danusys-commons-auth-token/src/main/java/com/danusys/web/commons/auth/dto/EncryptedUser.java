package com.danusys.web.commons.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EncryptedUser {

    private String securedUsername;
    private String securedPassword;
}
