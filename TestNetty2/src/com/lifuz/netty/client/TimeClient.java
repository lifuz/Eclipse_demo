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
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import com.lifuz.netty.handler.TimeClentHandler;

/**
 * 
 * @author 李富
 * @date 2015年8月5日
 *
 */
public class TimeClient {
	
	public void connect(String host , int port) {
		
		//配置客户端NIO线程组
		EventLoopGroup group = new NioEventLoopGroup();
		
		//配置客户端NIO线程组
		Bootstrap boot = new Bootstrap();
		
		boot.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {

					ChannelPipeline pipe = ch.pipeline();

					//接收到的数据用LineBasedFrameDecoder解码器进行读取和解析，
					//这个解码器，读取数据直到\n,\r\n，读取结束或者，读满1024字节。
					pipe.addLast(new LineBasedFrameDecoder(1024));
					//将上一个解码器读取的数据，进行二次解码，使用StringDecoder类，解码成字符串
					pipe.addLast(new StringDecoder());
					//将接收到且解码好的数据，数据处理类中
					pipe.addLast(new TimeClentHandler());
					
				}
			});
		
		try {
			
			//异步链接服务器 同步等待链接成功
			ChannelFuture f = boot.connect(host, port).sync();
			
			//等待链接关闭
			f.channel().closeFuture().sync();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			// 优雅退出 释放线程池资源
			group.shutdownGracefully();
		}
		
	}
	
	public static void main(String[] args) {
		
		new TimeClient().connect("127.0.0.1", 40000);
	}

}
