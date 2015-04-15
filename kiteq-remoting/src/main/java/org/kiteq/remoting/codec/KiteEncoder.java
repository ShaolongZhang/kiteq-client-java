package org.kiteq.remoting.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.kiteq.commons.util.ByteArrayUtils;
import org.kiteq.protocol.packet.KitePacket;
import org.kiteq.remoting.utils.ByteBufUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author gaofeihang
 * @since Feb 5, 2015
 */
public class KiteEncoder extends MessageToMessageEncoder<Object> {
    
    private static final Logger logger = LoggerFactory.getLogger(KiteEncoder.class);
    
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        
        KitePacket packet = (KitePacket) msg;
        ByteBufAllocator allocator = ctx.alloc();
        ByteBuf buf = packet.toByteBuf(allocator);
        out.add(buf);
        
        if (logger.isDebugEnabled()) {
            logger.debug("encoded hex: {}", ByteArrayUtils.hexDump(ByteBufUtils.toByteArray(buf)));
        }
    }

}
