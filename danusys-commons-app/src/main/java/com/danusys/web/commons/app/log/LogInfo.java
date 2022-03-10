package com.danusys.web.commons.app.log;

import lombok.Getter;
import lombok.Setter;

public class LogInfo {

    @Getter
    @Setter
    String timestamp;
    @Getter
    @Setter
    String hostname;
    @Getter
    @Setter
    String hostIp;
    @Getter
    @Setter
    String clientIp;
    @Getter
    @Setter
    String clientUrl;
    @Getter
    @Setter
    String callFunction;
    @Getter
    @Setter
    String type;
    @Getter
    @Setter
    String parameter;

}
