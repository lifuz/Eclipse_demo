package com.lifuz.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import com.lifuz.netty.protobuf.SubscribeReqProto;

public class ProtobufClientHandler extends ChannelHandlerAdapter {
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		SubscribeReqProto.SubscribeReq.Builder ui = SubscribeReqProto.SubscribeReq.newBuilder();

		ui.setId(1);
		ui.setAddress("上海奉贤沪杭公路1950号普若迪公司");
		ui.setUserName("lifuz");
		ui.setProductName("netty 权威指南");
		ui.build();

		// 直接向服务器，写一个对象
		ctx.writeAndFlush(ui);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		
		System.out.println(msg);

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}
}
