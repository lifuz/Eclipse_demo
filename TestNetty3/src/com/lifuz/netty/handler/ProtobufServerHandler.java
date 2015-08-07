package com.lifuz.netty.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import com.lifuz.netty.protobuf.SubscribeReqProto;
import com.lifuz.netty.protobuf.SubscribeRespProto;

public class ProtobufServerHandler extends ChannelHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		SubscribeReqProto.SubscribeReq ss = (SubscribeReqProto.SubscribeReq) msg;

		if ("lifuz".equals(ss.getUserName())) {
			System.out.println(ss.toString());

			ctx.writeAndFlush(resp(ss.getId()));
		}

	}

	private SubscribeRespProto.SubscribeResp resp(int id) {

		SubscribeRespProto.SubscribeResp.Builder sr = SubscribeRespProto.SubscribeResp
				.newBuilder();
		sr.setId(id);
		sr.setCode(0);
		sr.setDesc("Netty book order succeed, 3 day latter,sent to designated address. ");

		return sr.build();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ctx.close();
	}
}
