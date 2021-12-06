package client;

import commands.DownloadRequestCommand;
import commands.UploadFileCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import util.CustomFileDecoder;
import util.CustomFileEncoder;

import java.io.IOException;
import java.io.RandomAccessFile;

public class ClientMain {

        private static int port;
        private static String host;
        private static NioEventLoopGroup workGroup;
        private static Bootstrap client;
        private static Channel clientChannel;
        private static final int DEFAULT_PORT_VALUE = 9000;
        private static final String DEFAULT_HOST_VALUE = "localhost";
        public static final String DEFAULT_SERVER_FILE_PATH = "./serverFiles/testServer.txt";
        public static final String DEFAULT_CLIENT_FILE_PATH = "./clientFiles/clientData.txt";


        public static void main(String[] args) {
            argumentSetParameters(args);
            new ClientMain().run();
        }

        public void run() {
            try {
                workGroup = new NioEventLoopGroup(1);
                client = new Bootstrap()
                        .group(workGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel nioSocketChannel) {
                                nioSocketChannel.pipeline().addLast(
                                        new LengthFieldBasedFrameDecoder(1024*1024, 0,
                                                2,0,2),
                                        new LengthFieldPrepender(2),
                                        new CustomFileEncoder(),
                                        new CustomFileDecoder(),
                                        new ClientHandler()
                                );
                            }
                        })
                        .option(ChannelOption.SO_KEEPALIVE, true);
                clientChannel = client.connect(host, port).sync().channel();

                fileDownloadFromServer(DEFAULT_SERVER_FILE_PATH);
                fileUploadToServer(DEFAULT_CLIENT_FILE_PATH);

                clientChannel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workGroup.shutdownGracefully();
            }
        }

    private void fileUploadToServer(String path) throws IOException {

        try(RandomAccessFile requestedFile = new RandomAccessFile(DEFAULT_CLIENT_FILE_PATH, "r")) {
            final UploadFileCommand uploadFileCommand = new UploadFileCommand();
            byte[] content = new byte[(int) requestedFile.length()];
            requestedFile.read(content);
            uploadFileCommand.setContent(content);
            clientChannel.writeAndFlush(uploadFileCommand);
            System.out.println("File " + content.length + " bytes was transferred to server");
        }
    }

    private void fileDownloadFromServer(String path) {
        DownloadRequestCommand downloadRequestCommand = new DownloadRequestCommand();
        downloadRequestCommand.setPath(path);
        clientChannel.writeAndFlush(downloadRequestCommand);
    }

    private static void argumentSetParameters(String[] args) {
            if (args.length > 0) {
                host = args[0];
                port = Integer.parseInt(args[1]);
            } else {
                host = DEFAULT_HOST_VALUE;
                port = DEFAULT_PORT_VALUE;
            }
        }
}
