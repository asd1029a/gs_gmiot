package com.danusys.web.tcp.server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
