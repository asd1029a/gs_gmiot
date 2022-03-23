package com.danusys.web.commons.netty.domain;

import io.netty.channel.ChannelHandlerContext;


public class MessageCallback {

    public void read(String read) {    //요청에 따른 처리
        System.out.println("요청들어온 메세지 : "+ read);
    }

    public String write(String msg) {   //응답할 내용
        String result = "보낸 메세지 : "+msg;
        return result;
    }
    public void afterClose(ChannelHandlerContext ctx) {  //커넥션 끊기면 할 내용
        System.out.println("커넥션이 끊기면 동작하는 메소드 입니다.");
    }
}
