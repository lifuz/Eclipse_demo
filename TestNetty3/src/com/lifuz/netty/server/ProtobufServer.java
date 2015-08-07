package com.lifuz.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import com.lifuz.netty.handler.ProtobufServerHandler;
import com.lifuz.netty.protobuf.SubscribeReqProto;

public class ProtobufServer {

	public void bind(int port) throws Exception {

		// 服务器线程组 用于网络事件的处理 一个用于服务器接收客户端的连接
		// 另一个线程组用于处理SocketChannel的网络读写
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {

			// NIO服务器端的辅助启动类 降低服务器开发难度
			ServerBootstrap boot = new ServerBootstrap();

			boot.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class) // 监听tcp连接
					.option(ChannelOption.SO_BACKLOG, 1024)// 配置tcp参数
					// .handler(new LoggingHandler(LogLevel.INFO))
					// 绑定IO处理类，处理网络IO事件
					.childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel arg0)
								throws Exception {

							// System.out.println("收到数据了吗");
							ChannelPipeline pipe = arg0.pipeline();
							//使用Googleprotobuf进行编解码，不过不支持中文
							pipe.addLast(new ProtobufVarint32FrameDecoder());
							pipe.addLast(new ProtobufDecoder(
									SubscribeReqProto.SubscribeReq
											.getDefaultInstance()));
							pipe.addLast(new ProtobufVarint32LengthFieldPrepender());
							pipe.addLast(new ProtobufEncoder());

							// 将接收到且解码好的数据，数据处理类中
							pipe.addLast(new ProtobufServerHandler());

						}

					});

			// 服务器启动后 绑定监听端口 同步等待成功 主要用于异步操作的通知回调 回调处理用的ChildChannelHandler
			ChannelFuture f = boot.bind(port).sync();
			// 等待服务端监听端口关闭
			f.channel().closeFuture().sync();

		} finally {
			// 优雅退出 释放线程池资源
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

	public static void main(String[] args) throws Exception {
		new ProtobufServer().bind(40005);
	}

}
