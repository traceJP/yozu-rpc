package com.tracejp.yozu.rpc.core.transport.codec;

import com.tracejp.yozu.rpc.core.compress.ICompress;
import com.tracejp.yozu.rpc.core.constant.RpcConstants;
import com.tracejp.yozu.rpc.core.enums.CompressTypeEnum;
import com.tracejp.yozu.rpc.core.enums.RpcMessageTypeEnum;
import com.tracejp.yozu.rpc.core.enums.SerializationTypeEnum;
import com.tracejp.yozu.rpc.core.extension.ExtensionLoader;
import com.tracejp.yozu.rpc.core.serialize.ISerializer;
import com.tracejp.yozu.rpc.core.transport.domain.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> Rpc Message 编码 <p/>
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 * </pre>
 *
 * @author traceJP
 * @since 2023/10/31 11:35
 */
@ChannelHandler.Sharable
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    private static final Logger logger = LoggerFactory.getLogger(RpcMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage rpcMessage, ByteBuf byteBuf) {
        try {
            // 魔数
            byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER);

            // 版本
            byteBuf.writeByte(RpcConstants.VERSION);

            // 消息长度 - 暂时跳过
            byteBuf.writerIndex(byteBuf.writerIndex() + 4);

            // 消息类型 & 序列化类型 & 压缩类型 & 请求ID
            byte messageType = rpcMessage.getMessageType();
            byteBuf.writeByte(messageType);
            byteBuf.writeByte(rpcMessage.getCodec());
            byteBuf.writeByte(rpcMessage.getCompress());
            byteBuf.writeInt(rpcMessage.getRequestId());

            // 消息体
            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;  // fullLength = headLength + bodyLength
            if (!RpcMessageTypeEnum.isHeartBeatType(messageType)) {  // 心跳类型无body

                // 序列化
                String serializerName = SerializationTypeEnum.getName(rpcMessage.getCodec());
                ISerializer serializer = ExtensionLoader.getExtensionLoader(ISerializer.class)
                        .getExtension(serializerName);
                bodyBytes = serializer.serialize(rpcMessage.getData());

                // 压缩
                String compressName = CompressTypeEnum.getName(rpcMessage.getCompress());
                ICompress compress = ExtensionLoader.getExtensionLoader(ICompress.class)
                        .getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);

                fullLength += bodyBytes.length;
            }
            if (bodyBytes != null) {
                byteBuf.writeBytes(bodyBytes);
            }

            // 消息长度
            int writeIndex = byteBuf.writerIndex();
            byteBuf.writerIndex(writeIndex - fullLength + RpcConstants.MAGIC_NUMBER.length + 1);
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(writeIndex);
        } catch (Exception e) {
            logger.error("Encode request error: ", e);
        }
    }

}
