package com.lifuz.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientHandle implements Runnable {

	private String host;
	private int port;

	private Selector selector;
	private SocketChannel socketChannel;

	private volatile boolean stop;

	public TimeClientHandle(String host, int port) {
		// 设置服务器的地址和端口
		this.host = host == null ? "127.0.0.1" : host;
		this.port = port;

		try {
			// 打开多路复用器 selector
			selector = Selector.open();
			// 打开SocketChannel用于连接服务器
			socketChannel = SocketChannel.open();
			// 设置连接为非阻塞模式
			socketChannel.configureBlocking(false);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		try {
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!stop) {

			try {
				// 设置selector的休眠时间，循环间隔
				selector.select(1000);
				// 把所有准备就绪的Channel的SelectionKey放到集合中
				Set<SelectionKey> selectKeys = selector.selectedKeys();
				// 遍历处理所有的可以
				Iterator<SelectionKey> it = selectKeys.iterator();

				SelectionKey key = null;
				while (it.hasNext()) {
					key = it.next();
					// 每处理一个key，则把这个key删除
					it.remove();
					try {
						// 处理key的操作
						handleInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}

				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void handleInput(SelectionKey key) throws IOException {

		SocketChannel sc = (SocketChannel) key.channel();

		if (key.isValid()) {
			// 判断是否是连接三次握手的返回值
			if (key.isConnectable()) {
				if (sc.finishConnect()) {
					//连接成功，则注册到多路复用器上，并发送请求消息
					sc.register(selector, SelectionKey.OP_READ);
					
					doWrite(socketChannel);
				} else {
					System.exit(1);
				}
			}
		}

		// 如果key的类型为读取数据类型
		if (key.isReadable()) {

			// 分配一个新的缓冲区，大小为1024个字节
			ByteBuffer readBuffer = ByteBuffer.allocate(1024);
			// 从通道中读取数据，返回值为读取的字节数，返回值为0的话，则没有数据，如果为-1则链路关闭
			int readBytes = sc.read(readBuffer);

			if (readBytes > 0) {
				// 翻转缓冲区，准备把数据从缓冲区中读取出来
				readBuffer.flip();
				// 定义一个字节数组，当作读取的数据的容器，数组长度为缓冲区区数据的长度
				byte[] req = new byte[readBuffer.remaining()];
				// 把缓冲区的数据读取到数组中
				readBuffer.get(req);
				// 把字节数组转换成字符串
				String body = new String(req, "utf-8");

				System.out.println("now is : " + body);
				
				this.stop = true;

			} else if (readBytes < 0) {
				key.cancel();
				sc.close();
			}

		}

	}

	/**
	 * 连接服务器的方法
	 * 
	 * @throws IOException
	 */
	private void doConnect() throws IOException {
		// 如果连接成功，则注册到多路复用器上，并发送请求消息
		if (socketChannel.connect(new InetSocketAddress(host, port))) {
			System.out.println("li");
			socketChannel.register(selector, SelectionKey.OP_READ);

			doWrite(socketChannel);

		} else {
			System.out.println("li");
			// 如果连接失败则注册成等待连接的状态，等待服务器的连接
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}

	}

	/**
	 * 给服务器发送数据
	 * 
	 * @param sc
	 * @param curentTime
	 * @throws IOException
	 */
	private void doWrite(SocketChannel sc) throws IOException {
		// 判断字符串是否为空

		// 把发送的数据转换成字节数组
		byte[] buf = "QUERY TIME ORDER".getBytes();
		// 分配一个新的缓冲区，大小是字节数组的长度
		ByteBuffer writeBuffer = ByteBuffer.allocate(buf.length);
		// 把字节数组的数据放入缓冲区
		writeBuffer.put(buf);
		// 翻转数据，准备发送
		writeBuffer.flip();
		// 把数据发送到客户端
		sc.write(writeBuffer);

	}

}
