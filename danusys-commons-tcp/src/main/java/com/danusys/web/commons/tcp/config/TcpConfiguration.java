/*
package com.danusys.web.tcp.server.config;

import com.danusys.web.tcp.server.domain.MessageCallback;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(NettyProperties.class)
public class TcpConfiguration {

    private final NettyProperties nettyProperties;

    @Bean
    public ServerBootstrap serverBootstrap(TcpChannelInitializer tcpChannelInitializer) {
        //ServerBootstrap : 서버 설정을 도와주는 클래스
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup(), workerGroup())
                // NioServerSocketChannel: incoming connections를 수락하기 위해 새로운 Channel을 객체화할 때 사용
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(tcpChannelInitializer);
        // ServerBootstarp에 다양한 Option 추가 가능
        // SO_BACKLOG: 동시에 수용 가능한 최대 incoming connections 개수
        // 이 외에도 SO_KEEPALIVE, TCP_NODELAY 등 옵션 제공
        b.option(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog());
        return b;
    }

    // boss: incoming connection을 수락하고, 수락한 connection을 worker에게 등록(register)
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(nettyProperties.getBossCount());
    }

    // worker: boss가 수락한 연결의 트래픽 관리
    @Bean(destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorkerCount());
    }

    @Bean
    public InetSocketAddress tcpSocketAddress() {
        return new InetSocketAddress(nettyProperties.getTcpPort());
    }

    @Bean
    public MessageCallback myCallBack() {
        return new MessageCallback();
    }
}
*/
