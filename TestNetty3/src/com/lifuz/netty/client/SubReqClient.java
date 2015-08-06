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
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import com.lifuz.netty.handler.SubReqClientHandler;

/**
 * 
 * @author 李富
 * @date 2015年8月5日
 *
 */
public class SubReqClient {

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
						// 接收数据时，先对数据进行解码
						pipe.addLast(new ObjectDecoder(1024,
								ClassResolvers.cacheDisabled(this.getClass()
										.getClassLoader())));
						// 发送数据时，先对数据进行编码
						pipe.addLast(new ObjectEncoder());
						// 将接收到且解码好的数据，数据处理类中
						pipe.addLast(new SubReqClientHandler());

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

		new SubReqClient().connect("127.0.0.1", 40005);
	}

}
