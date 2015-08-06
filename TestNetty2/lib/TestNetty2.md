这个项目的主要目的：

测试解码器：LineBasedFrameDecoder 和 StringDecoder

测试分隔符解码：

ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());					
//使用自己定义的分割符，接收数据,如果读取到的数据长度大于1024，则抛出异常
pipe.addLast(new DelimiterBasedFrameDecoder(1024, delimiter));