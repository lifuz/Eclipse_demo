package com.lifuz.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import com.lifuz.netty.handler.ProtobufClientHandler;
import com.lifuz.netty.protobuf.SubscribeRespProto;

/**
 * 
 * @author 李富
 * @date 2015年8月5日
 *
 */
public class ProtobufClient {

	public void connect(String host, int port) {

		// 配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();

		// 配置客户端NIO线程组
		Bootstrap boot = new Bootstrap();

		boot.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch)
							throws Exception {

						ChannelPipeline pipe = ch.pipeline();
						//使用Googleprotobuf进行编解码，不过不支持中文
						pipe.addLast(new ProtobufVarint32FrameDecoder());
						pipe.addLast(new ProtobufDecoder(
								SubscribeRespProto.SubscribeResp
										.getDefaultInstance()));
						pipe.addLast(new ProtobufVarint32LengthFieldPrepender());
						pipe.addLast(new ProtobufEncoder());
						// 将接收到且解码好的数据，数据处理类中
						pipe.addLast(new ProtobufClientHandler());

					}
				});

		try {

			// 异步链接服务器 同步等待链接成功
			ChannelFuture f = boot.connect(host, port).sync();

			// 等待链接关闭
			f.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 优雅退出 释放线程池资源
			group.shutdownGracefully();
		}

	}

	public static void main(String[] args) {

		new ProtobufClient().connect("127.0.0.1", 40005);
	}

}
