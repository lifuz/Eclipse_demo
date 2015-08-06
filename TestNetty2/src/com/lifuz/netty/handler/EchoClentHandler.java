package com.lifuz.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 这个类是客户端的消息收发的主类
 * @author 李富
 * @date 2015年8月5日
 *
 */

public class EchoClentHandler extends ChannelHandlerAdapter {
	
	//定义一个发送消息的缓存对象
	private final ByteBuf firstMessage;
	private static final String NAME = "hi,nice to meet you.$_";
	
	public EchoClentHandler() {
		//把要发送的消息加载到缓存中
		//首先把消息转成字节数组
		System.out.println(NAME + NAME + NAME);
		byte[] req = (NAME + NAME + NAME).getBytes();
//		//然后根据的字节数组的长度设置缓存的大小
//		firstMessage = Unpooled.buffer(req.length);
//		//把发送的消息写入缓存中
//		firstMessage.writeBytes(req);
		
		firstMessage = Unpooled.copiedBuffer(req);
		
	}
	
	/**
	 * 发送消息的方法
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		//把消息发送给服务器
		ctx.writeAndFlush(firstMessage);
		
	}
	
	/**
	 * 读取消息的方法
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		String body = (String) msg;
		System.out.println(body);
		
	}
	
	/*
	 * 异常处理
	 * 
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

}
