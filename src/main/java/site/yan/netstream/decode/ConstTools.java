package site.yan.netstream.decode;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

/**
 * @author zhao xubin
 * @date 2022/06/30
 */
public class ConstTools {
    public final static int BYTE_LENGTH_2 = 2;
    public final static int BYTE_LENGTH_4 = 4;

    public static boolean bufGt4Byte(ByteBuf buf) {
        return buf.readableBytes() >= BYTE_LENGTH_4;
    }

    public static boolean bufGt2Byte(ByteBuf buf) {
        return buf.readableBytes() >= BYTE_LENGTH_2;
    }

    public static boolean bufGtByte(ByteBuf buf, int len) {
        return buf.readableBytes() >= len;
    }

    public static byte[] readByte(ByteBuf buf, int readCount) throws Exception {
        if (buf.readableBytes() < readCount) {
            throw new Exception("bytes buffer abnormal length");
        }
        byte[] byteTemp = new byte[readCount];
        for (int i = 0; i < readCount; i++) {
            byteTemp[i] = buf.readByte();
        }
        return byteTemp;
    }

    public static int intByByte(byte[] bytes) {
        int temp = 0x00;
        for (int i = bytes.length - 1; i >= 0; i--) {
            temp = temp | ((bytes[bytes.length - 1 - i] & 0xFF) << i * 8);
        }
        return temp;
    }

    public static String stringByByte(byte[] bytes) throws UnsupportedEncodingException {
        return new String(bytes, "UTF-8");
    }

    public static int intByByteBuf(ByteBuf buf, int readCount) throws Exception {
        byte[] bytes = readByte(buf, readCount);
        return intByByte(bytes);
    }
}
