package com.lifuz.nio.server;

/**
 * 启动服务器的线程
 * @author 李富
 * @date 2015年8月5日
 *
 */
public class TimeServer {
	
	public static void main(String[] args) {
		MultiplexerTimeServer timeServer = new MultiplexerTimeServer(40001);
		
		new Thread(timeServer,"lifuz-server").start();
		
	}

}
