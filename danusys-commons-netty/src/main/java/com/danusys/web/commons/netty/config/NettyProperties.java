package com.danusys.web.commons.netty.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

    @NotNull
    private int tcpPort;

    @NotNull
    private int bossCount;

    @NotNull
    private int workerCount;

    @NotNull
    private boolean keepAlive;

    @NotNull
    private int backlog;
}
