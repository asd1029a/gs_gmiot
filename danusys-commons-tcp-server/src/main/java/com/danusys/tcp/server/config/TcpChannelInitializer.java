package com.danusys.tcp.server.config;

import com.danusys.tcp.server.handler.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final ServerHandler serverHandler;

    // 클라이언트 소켓 채널이 생성될 때 호출
    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast("ReadTimeoutHandler", new ReadTimeoutHandler(60));//읽기 타임아웃 설정(클라 -> 서버)
        socketChannel.pipeline().addLast("WriteTimeoutHandler", new WriteTimeoutHandler(30));//쓰기 타임아웃 설정(서버 -> 클라)
        socketChannel.pipeline().addLast(serverHandler);
    }
}
