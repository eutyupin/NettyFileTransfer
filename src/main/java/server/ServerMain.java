package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import util.CustomFileDecoder;
import util.CustomFileEncoder;

public class ServerMain {

        private static int port;
        private static final int DEFAULT_PORT_VALUE = 9000;

        public static void main(String[] args) throws InterruptedException {
            argumentSetPort(args);
            new ServerMain().run();
        }

        public void run() throws InterruptedException {
            NioEventLoopGroup connectorGroup = new NioEventLoopGroup(1);
            NioEventLoopGroup workGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap server = new ServerBootstrap()
                        .group(connectorGroup, workGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                                nioSocketChannel.pipeline().addLast(
                                        new LengthFieldBasedFrameDecoder(1024*1024, 0,
                                                2,0,2),
                                        new LengthFieldPrepender(2),
                                        new CustomFileEncoder(),
                                        new CustomFileDecoder(),
                                        new ServerHandler()
                                );
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);
                Channel channel = server.bind(port).sync().channel();
                channel.closeFuture().sync();
            } finally {
                {
                    connectorGroup.shutdownGracefully();
                    workGroup.shutdownGracefully();
                }
            }
        }

        private static void argumentSetPort(String[] args) {
            if (args.length > 0) port = Integer.parseInt(args[0]);
            else port = DEFAULT_PORT_VALUE;
        }
}
