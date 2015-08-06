package com.lifuz.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import com.lifuz.netty.handler.EchoClentHandler;

/**
 * 
 * @author 李富
 * @date 2015年8月5日
 *
 */
public class EchoClient {
	
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
					
//					//接收到的数据用LineBasedFrameDecoder解码器进行读取和解析，
//					//这个解码器，读取数据直到\n,\r\n，读取结束或者，读满1024字节。
//					pipe.addLast(new LineBasedFrameDecoder(1024));

					ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
					
					//使用自己定义的分割符，接收数据,如果读取到的数据长度大于1024，则抛出异常
					pipe.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
					//将上一个解码器读取的数据，进行二次解码，使用StringDecoder类，解码成字符串
					pipe.addLast(new StringDecoder());
					//将接收到且解码好的数据，数据处理类中
					pipe.addLast(new EchoClentHandler());
					
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
		
		new EchoClient().connect("127.0.0.1", 40005);
	}

}
