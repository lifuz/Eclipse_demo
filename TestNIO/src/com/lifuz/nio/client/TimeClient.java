package com.lifuz.nio.client;

public class TimeClient {

	public static void main(String[] args) {
		new Thread(new TimeClientHandle("127.0.0.1", 40001)).start();
	}
	
}
