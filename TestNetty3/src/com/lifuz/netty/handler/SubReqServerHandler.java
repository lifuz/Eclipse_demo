package com.lifuz.netty.handler;

import com.lifuz.netty.bean.SubscribeResp;
import com.lifuz.netty.bean.UserInfo;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class SubReqServerHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		
		UserInfo ui = (UserInfo) msg;
		
		if("lifuz".equals(ui.getName())) {
			System.out.println(ui);
			
			ctx.writeAndFlush(resp(ui.getId()));
		}
		
	}

	private SubscribeResp resp(int id) {
		
		SubscribeResp sr = new SubscribeResp();
		sr.setId(id);
		sr.setCode(0);
		sr.setDesc("Netty book order succeed, 3 day latter,sent to designated address. ");
		
		return sr;
	}
	
	

}
