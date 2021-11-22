package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import commands.Command;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class CustomFileDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buffer, List<Object> outList) throws Exception {
        Command command = OBJECT_MAPPER.readValue(ByteBufUtil.getBytes(buffer), Command.class);
        outList.add(command);
    }
}
