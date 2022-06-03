package com.danusys.web.commons.app.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo
 * Date : 2022/06/03
 * Time : 14:37
 */
@Getter
@Setter
@Builder
public class CookieData {
    private LocalDateTime createTime;
    private LocalDateTime expiredTime;
    private Cookie cookie;
}
