package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import commands.Command;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class CustomFileEncoder extends MessageToByteEncoder<Command> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Command command, ByteBuf outBuf) throws Exception {
        byte[] val = OBJECT_MAPPER.writeValueAsBytes(command);
        outBuf.writeBytes(val);
    }
}
