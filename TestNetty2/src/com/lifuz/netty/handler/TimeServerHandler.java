package com.lifuz.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class TimeServerHandler extends ChannelHandlerAdapter {

	/**
	 * 接收来自客户端消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		
		//因为解码器，自动把字节流，转换成了字符串
		String body = (String) msg;
		System.out.println("the time server receive order:" + body);
		
		//设置返回消息的字符串，如何客户端发出的是"QUERY TIME ORDER"命令则返回当前系统时间，否则返回"BAD ORDER"
		String curentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(
				System.currentTimeMillis()).toString() : "BAD ORDER";
				
		curentTime = curentTime + System.getProperty("line.separator");
				
		//把发送的数据放到缓存中
		//Unpooled.copiedBuffer(curentTime.getBytes());相当于
		//firstMessage = Unpooled.buffer(req.length);
		//firstMessage.writeBytes(req);两句
		ByteBuf resp = Unpooled.copiedBuffer(curentTime.getBytes());
		//把消息发送给客户端
		ctx.write(resp);
	}

	/**
	 * 处理数据接收完成之后的操作
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//清空缓存区
		ctx.flush();
	}

	/**
	 * 异常处理方法
	 * 关闭通道
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}

}
