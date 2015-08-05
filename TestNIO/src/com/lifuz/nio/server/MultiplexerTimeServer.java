package com.lifuz.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;


/**
 * 处理服务器的全部操作，包括：
 * 启动服务器
 * 接收客户机的连接
 * 接收客户机的消息
 * 向客户机发送数据
 * @author 李富
 * @date 2015年8月5日
 *
 */
public class MultiplexerTimeServer implements Runnable {

	private Selector selector;
	
	private ServerSocketChannel serverChannel;
	private volatile boolean stop;
	
	public MultiplexerTimeServer(int port) {
		
		try {
			//打开多路复用器 selector
			selector = Selector.open();
			//打开ServerSocketChannel，用于监听客户端的连接
			serverChannel = ServerSocketChannel.open();
			//设置连接为非阻塞模式
			serverChannel.configureBlocking(false);
			//绑定端口号和设置最大连接数
			serverChannel.socket().bind(new InetSocketAddress(port),1024);
			//将通道与多路复用器进行注册
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void stop() {
		this.stop = true;
	}
	
	@Override
	public void run() {

		while(!stop) {
			
			try {
				//设置selector的休眠时间，循环间隔
				selector.select(1000);
				//把所有准备就绪的Channel的SelectionKey放到集合中
				Set<SelectionKey> selectKeys = selector.selectedKeys();
				//遍历处理所有的可以
				Iterator<SelectionKey> it = selectKeys.iterator();
				
				SelectionKey key = null;
				while(it.hasNext()) {
					key = it.next();
					//每处理一个key，则把这个key删除
					it.remove();
					try {
						//处理key的操作
						handleInput(key);
					} catch(Exception e) {
						if(key != null) {
							key.cancel();
							if(key.channel() !=null) {
								key.channel().close();
							}
						}
					}
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 根据的key事件的类型，来分别处理
	 * @param key
	 * @throws IOException
	 */
	private void handleInput(SelectionKey key) throws IOException {
		//判断key是否有效，如果有效，则进行下面处理
		if(key.isValid()) {
			//判断key的类型是否为一个新连接的socket
			if(key.isAcceptable()) {
				//获取服务器的连接通道
				ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
				//获取客户端的连接通道
				SocketChannel sc = ssc.accept();
				//设置客户端的连接通道为异步非阻塞模式
				sc.configureBlocking(false);
				//在多路复用器中注册这个连接，注册的事件是读取事件
				sc.register(selector, SelectionKey.OP_READ);
				
			}
			//如果key的类型为读取数据类型
			if(key.isReadable()) {
				//根据key获取与客户机的连接通道
				SocketChannel sc = (SocketChannel) key.channel();
				
				//分配一个新的缓冲区，大小为1024个字节
				ByteBuffer readBuffer = ByteBuffer.allocate(1024);
				//从通道中读取数据，返回值为读取的字节数，返回值为0的话，则没有数据，如果为-1则链路关闭
				int readBytes = sc.read(readBuffer);
				
				if(readBytes >0) {
					//翻转缓冲区，准备把数据从缓冲区中读取出来
					readBuffer.flip();
					//定义一个字节数组，当作读取的数据的容器，数组长度为缓冲区区数据的长度
					byte[] req = new byte[readBuffer.remaining()];
					//把缓冲区的数据读取到数组中
					readBuffer.get(req);
					//把字节数组转换成字符串
					String body = new String(req,"utf-8");
					
					System.out.println("The Time Server receive order:" + body);
					
					//准备返回的数据
					String curentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(
							System.currentTimeMillis()).toString() : "BAD ORDER";
					//发送数据到客户端	
					doWrite(sc,curentTime);
					
				} else if(readBytes <0) {
					key.cancel();
					sc.close();
				}
				
			}
			
		}
		
	}

	/**
	 * 给客户端发送数据
	 * @param sc
	 * @param curentTime
	 * @throws IOException
	 */
	private void doWrite(SocketChannel sc, String curentTime) throws IOException {
		//判断字符串是否为空
		if(curentTime != null && curentTime.trim().length() > 0) {
			//把发送的数据转换成字节数组
			byte[] buf = curentTime.getBytes();
			//分配一个新的缓冲区，大小是字节数组的长度
			ByteBuffer writeBuffer = ByteBuffer.allocate(buf.length);
			//把字节数组的数据放入缓冲区
			writeBuffer.put(buf);
			//翻转数据，准备发送
			writeBuffer.flip();
			//把数据发送到客户端
			sc.write(writeBuffer);
		}
		
	}

}
