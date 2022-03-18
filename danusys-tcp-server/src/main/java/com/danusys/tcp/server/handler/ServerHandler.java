package com.danusys.tcp.server.handler;

import com.danusys.tcp.server.domain.MessageCallback;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Charset CHARSET = Charset.forName("UTF-8");
    private final MessageCallback writer;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf inBuffer = (ByteBuf) msg;
        String received = inBuffer.toString(CHARSET);
        if(writer == null){
            System.out.println("Server received : " + received);
            ctx.write(Unpooled.copiedBuffer("echo : "+received, CHARSET));
        } else {
            writer.read(received);
            String arg = writer.write(received);
            ctx.write(Unpooled.copiedBuffer(arg.toCharArray(), CHARSET));
        }
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx){
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }

    //종료 이벤트
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if(writer != null){
            writer.afterClose(ctx);
        }
        super.channelUnregistered(ctx);
    }
}
