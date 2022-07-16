package site.yan.netstream.decode;

import io.netty.buffer.ByteBuf;
import lombok.Data;

import static site.yan.netstream.decode.ConstTools.*;

/**
 * @author zhao xubin
 * @date 2022/06/29
 * https://support.huawei.com/enterprise/zh/doc/EDOC1100055170/d494430a
 */
@Data
public class V9HeaderParser {
    // 报文头
    // 2个Byte: 1Byte 等于 2位16进制

    /**
     * 版本号 V9
     */
    byte[] version;
    /**
     * 该报文包含的FlowSet records(包括Template和Data)的数目
     */
    int count;

    byte[] systemUpTime;
    byte[] unixSecs;
    byte[] packageSequence;
    /**
     * 设备唯一标识
     */
    byte[] sourceId;
    int sourceIdInt;

    private ByteBuf buf;

    public V9HeaderParser(ByteBuf buf) {
        this.buf = buf;
    }

    public void parse() throws Exception {
        version = readByte(this.buf, BYTE_LENGTH_2);
        count = intByByteBuf(this.buf, BYTE_LENGTH_2);
        systemUpTime = readByte(this.buf, BYTE_LENGTH_4);
        unixSecs = readByte(this.buf, BYTE_LENGTH_4);
        packageSequence = readByte(this.buf, BYTE_LENGTH_4);
        sourceId = readByte(this.buf, BYTE_LENGTH_4);
        sourceIdInt = intByByte(sourceId);
    }
}






