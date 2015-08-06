package com.lifuz.netty.handler;

import com.lifuz.netty.bean.SubscribeResp;
import com.lifuz.netty.bean.UserInfo;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqClientHandler extends ChannelHandlerAdapter {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		UserInfo ui = new UserInfo();

		ui.setId(1);
		ui.setAddress("上海奉贤沪杭公路1950号普若迪公司");
		ui.setName("lifuz");
		ui.setProductName("netty 权威指南");
		ui.setPhoneNumber("13013874964");

		//直接向服务器，写一个对象
		ctx.writeAndFlush(ui);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		SubscribeResp sr = (SubscribeResp) msg;
		System.out.println(sr);
		
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
