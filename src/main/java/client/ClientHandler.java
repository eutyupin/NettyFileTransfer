package client;

import commands.Command;
import commands.TransferredFileCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.RandomAccessFile;

public class ClientHandler extends SimpleChannelInboundHandler<Command> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command) throws Exception {
        if (command instanceof TransferredFileCommand) {
            byte[] transferredFile = ((TransferredFileCommand) command).getContent();
        try (RandomAccessFile receivedFile = new RandomAccessFile("./clientFiles/testServer.txt", "rw")) {
                receivedFile.write(transferredFile);
            }
            channelHandlerContext.close();
            System.exit(0);
        }
    }
}
