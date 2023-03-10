package com.danusys.web.commons.netty.handler;

import com.danusys.web.commons.netty.domain.MessageCallback;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Component
@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final Charset CHARSET = Charset.forName("EUC-KR");
    private final MessageCallback writer;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf inBuffer = (ByteBuf) msg;
        String received = inBuffer.toString(CHARSET);
        writer.read(received);
        String arg = writer.write(received);
        ctx.write(Unpooled.copiedBuffer(arg.toCharArray(), CHARSET));
    }
    // 클라이언트와 연결되어 트래픽을 생성할 준비가 되었을 때 호출되는 메소드
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String remoteAddress = ctx.channel().remoteAddress().toString();
        log.info("Remote Address: " + remoteAddress);
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
