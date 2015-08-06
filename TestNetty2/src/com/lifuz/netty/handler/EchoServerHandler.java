package com.lifuz.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoServerHandler extends ChannelHandlerAdapter {
	
	/**
	 * 接收来自客户端消息
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		// 因为解码器，自动把字节流，转换成了字符串
		String body = (String) msg;
		System.out.println("the time server receive order:" + body);

		body = body + "$_";

		// 把发送的数据放到缓存中
		// Unpooled.copiedBuffer(curentTime.getBytes());相当于
		// firstMessage = Unpooled.buffer(req.length);
		// firstMessage.writeBytes(req);两句
		ByteBuf resp = Unpooled.copiedBuffer(body.getBytes());
		// 把消息发送给客户端
		ctx.write(resp);
	}

	/**
	 * 处理数据接收完成之后的操作
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		// 清空缓存区
		ctx.flush();
	}

	/**
	 * 异常处理方法 关闭通道
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}
}
