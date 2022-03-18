package com.danusys.tcp.server.config;

import com.danusys.tcp.server.handler.ServerHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class ChannelInitializer extends io.netty.channel.ChannelInitializer<SocketChannel> {

    private final ServerHandler serverHandler;


    @Override
    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addLast("ReadTimeoutHandler", new ReadTimeoutHandler(60));
        socketChannel.pipeline().addLast("WriteTimeoutHandler", new WriteTimeoutHandler(30));
        socketChannel.pipeline().addLast("myHandler", serverHandler);
    }
}
