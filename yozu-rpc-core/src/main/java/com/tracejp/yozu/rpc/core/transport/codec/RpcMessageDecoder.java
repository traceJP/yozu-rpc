package com.tracejp.yozu.rpc.core.transport.codec;

import com.tracejp.yozu.rpc.core.compress.ICompress;
import com.tracejp.yozu.rpc.core.constant.RpcConstants;
import com.tracejp.yozu.rpc.core.enums.CompressTypeEnum;
import com.tracejp.yozu.rpc.core.enums.RpcMessageTypeEnum;
import com.tracejp.yozu.rpc.core.enums.SerializationTypeEnum;
import com.tracejp.yozu.rpc.core.extension.ExtensionLoader;
import com.tracejp.yozu.rpc.core.serialize.ISerializer;
import com.tracejp.yozu.rpc.core.transport.domain.RpcMessage;
import com.tracejp.yozu.rpc.core.transport.domain.RpcRequest;
import com.tracejp.yozu.rpc.core.transport.domain.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * <p> Rpc Message 解码 <p/>
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
 * @since 2023/10/31 11:36
 */
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    private static final Logger logger = LoggerFactory.getLogger(RpcMessageDecoder.class);

    /**
     * 参数：
     * 1、最大帧长度：8M
     * 2、长度域偏移量：跳过魔数4byte+版本号1byte，共5byte
     * 3、长度域长度：4byte
     * 4、长度域调整：跳过魔数4byte，版本号1byte，长度域4byte，共9字节
     * 5、跳过字节数：0
     */
    public RpcMessageDecoder() {
        super(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decode;
            if (frame.readableBytes() >= RpcConstants.HEAD_LENGTH) {  // 确保收到整个完整的消息头
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    logger.error("Decode frame error: ", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decode;
    }

    private Object decodeFrame(ByteBuf frame) {
        checkMagicNumber(frame);
        checkVersion(frame);
        int fullLength = frame.readInt();
        byte messageType = frame.readByte();
        byte codecType = frame.readByte();
        byte compressType = frame.readByte();
        int requestId = frame.readInt();

        RpcMessage message = new RpcMessage();
        message.setMessageType(messageType);
        message.setCodec(codecType);
        message.setCompress(compressType);
        message.setRequestId(requestId);

        // 心跳消息固定返回
        if (message.getMessageType() == RpcMessageTypeEnum.HEARTBEAT_REQUEST_TYPE.getType()) {
            message.setData(RpcConstants.PING);
            return message;
        }
        if (message.getMessageType() == RpcMessageTypeEnum.HEARTBEAT_RESPONSE_TYPE.getType()) {
            message.setData(RpcConstants.PONG);
            return message;
        }

        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength > 0) {
            byte[] body = new byte[bodyLength];
            frame.readBytes(body);

            // 解压
            String compressName = CompressTypeEnum.getName(message.getCompress());
            ICompress compress = ExtensionLoader.getExtensionLoader(ICompress.class)
                    .getExtension(compressName);
            body = compress.decompress(body);

            // 反序列化
            String serializerName = SerializationTypeEnum.getName(message.getCodec());
            ISerializer serializer = ExtensionLoader.getExtensionLoader(ISerializer.class)
                    .getExtension(serializerName);

            // 请求 & 响应 消息解析
            if (message.getMessageType() == RpcMessageTypeEnum.REQUEST_TYPE.getType()) {
                message.setData(serializer.deserialize(body, RpcRequest.class));
                return message;
            }
            if (message.getMessageType() == RpcMessageTypeEnum.RESPONSE_TYPE.getType()) {
                message.setData(serializer.deserialize(body, RpcResponse.class));
                return message;
            }
        }

        return message;
    }

    private void checkMagicNumber(ByteBuf in) {
        int length = RpcConstants.MAGIC_NUMBER.length;
        byte[] magicNumber = new byte[length];
        in.readBytes(magicNumber);
        for (int i = 0; i < length; i++) {
            if (magicNumber[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("unknown magic code: " + Arrays.toString(magicNumber));
            }
        }
    }

    private void checkVersion(ByteBuf in) {
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new IllegalArgumentException("version isn't compatible: " + version);
        }
    }

}
