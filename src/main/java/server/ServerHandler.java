package server;

import commands.Command;
import commands.DownloadRequestCommand;
import commands.TransferredFileCommand;
import commands.UploadFileCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ServerHandler extends SimpleChannelInboundHandler<Command> {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client connected");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client disconnected");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Command command){
        checkCommands(command, channelHandlerContext);
    }

    private void checkCommands(Command command, ChannelHandlerContext channelHandlerContext) {
        if (command instanceof DownloadRequestCommand) {
            DownloadRequestCommand downloadRequestCommand = (DownloadRequestCommand) command;
            try (RandomAccessFile requestedFile = new RandomAccessFile(downloadRequestCommand.getPath(), "r")) {
                final TransferredFileCommand transferredFileCommand = new TransferredFileCommand();
                byte[] content = new byte[(int) requestedFile.length()];
                requestedFile.read(content);
                transferredFileCommand.setContent(content);
                channelHandlerContext.writeAndFlush(transferredFileCommand);
                System.out.println("File " + content.length + " bytes was transferred to client");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

            if (command instanceof UploadFileCommand) {
                try(RandomAccessFile uploadedFile = new RandomAccessFile("./serverFiles/clientData.txt", "rw")) {
                    byte[] uploadFileData = ((UploadFileCommand) command).getContent();
                    uploadedFile.write(uploadFileData);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Exception: " + cause.getMessage());
        ctx.close();
    }
}
