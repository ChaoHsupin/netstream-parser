package site.yan.netstream.decode;

import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static site.yan.netstream.decode.ConstTools.*;

/**
 * @author zhao xubin
 * @date 2022/06/30
 * 解析一个模版
 */
@Data
public class V9TemplateFlowParser {

    private ByteBuf buf;

    private static Map<Integer, String> FIELD_TRANS_CACHE = new HashMap<>(16);

    public final static String BYTES = "BYTES";
    public final static String PKTS = "PKTS";
    public final static String IP_SRC_ADDR = "IP_SRC_ADDR";
    public final static String L4_SRC_PORT = "L4_SRC_PORT";
    public final static String TCP_FLAGS = "TCP_FLAGS";
    public final static String PROTOCOL = "PROTOCOL";
    public final static String IP_TOS = "IP_TOS";
    public final static String SRC_MASK = "SRC_MASK";
    public final static String INPUT_SNMP = "INPUT_SNMP";
    public final static String L4_DST_PORT = "L4_DST_PORT";
    public final static String IP_DST_ADDR = "IP_DST_ADDR";
    public final static String DST_MASK = "DST_MASK";
    public final static String OUTPUT_SNMP = "OUTPUT_SNMP";
    public final static String IP_NEXT_HOP = "IP_NEXT_HOP";
    public final static String SRC_AS = "SRC_AS";
    public final static String DST_AS = "DST_AS";
    public final static String BGP_NEXT_HOP = "BGP_NEXT_HOP";
    public final static String LAST_SWITCHED = "LAST_SWITCHED";
    public final static String FIRST_SWITCHED = "FIRST_SWITCHED";
    public final static String SRC_VLAN = "SRC_VLAN";
    public final static String DST_VLAN = "DST_VLAN";
    public final static String DIRECTION = "DIRECTION";
    public final static String FORWARDING_STATUS = "FORWARDING_STATUS";
    public final static String PADDING_OCTETS = "paddingOctets";
    public final static String RESPONDER_OCTETS = "responderOctets";

    static {
        FIELD_TRANS_CACHE.put(1, BYTES);
        FIELD_TRANS_CACHE.put(2, PKTS);
        FIELD_TRANS_CACHE.put(8, IP_SRC_ADDR);
        FIELD_TRANS_CACHE.put(7, L4_SRC_PORT);
        FIELD_TRANS_CACHE.put(6, TCP_FLAGS);
        FIELD_TRANS_CACHE.put(4, PROTOCOL);
        FIELD_TRANS_CACHE.put(5, IP_TOS);
        FIELD_TRANS_CACHE.put(9, SRC_MASK);
        FIELD_TRANS_CACHE.put(10, INPUT_SNMP);
        FIELD_TRANS_CACHE.put(11, L4_DST_PORT);
        FIELD_TRANS_CACHE.put(12, IP_DST_ADDR);
        FIELD_TRANS_CACHE.put(13, DST_MASK);
        FIELD_TRANS_CACHE.put(14, OUTPUT_SNMP);
        FIELD_TRANS_CACHE.put(15, IP_NEXT_HOP);
        FIELD_TRANS_CACHE.put(16, SRC_AS);
        FIELD_TRANS_CACHE.put(17, DST_AS);
        FIELD_TRANS_CACHE.put(18, BGP_NEXT_HOP);
        FIELD_TRANS_CACHE.put(21, LAST_SWITCHED);
        FIELD_TRANS_CACHE.put(22, FIRST_SWITCHED);
        FIELD_TRANS_CACHE.put(58, SRC_VLAN);
        FIELD_TRANS_CACHE.put(59, DST_VLAN);
        FIELD_TRANS_CACHE.put(61, DIRECTION);
        FIELD_TRANS_CACHE.put(89, FORWARDING_STATUS);
        FIELD_TRANS_CACHE.put(210, PADDING_OCTETS);
        FIELD_TRANS_CACHE.put(232, RESPONDER_OCTETS);
    }

    private static String transField(int fieldCode) {
        return FIELD_TRANS_CACHE.getOrDefault(fieldCode, "UnKnown");
    }

    /**
     * 解析后的模版 模版ID -> 模版
     */
    private Map<Integer, TemplatePack> packs;

    public V9TemplateFlowParser(ByteBuf buf) {
        this.buf = buf;
    }

    public int parse() throws Exception {
        readByte(this.buf, BYTE_LENGTH_2);
        int length = intByByteBuf(this.buf, BYTE_LENGTH_2);
        length -= (BYTE_LENGTH_2 * 2);

        packs = new HashMap<>(16);

        // checkpoint: buf 长度小于 length，理论上不会出现这种情况
        while (length > 0 && bufGt4Byte(buf)) {
            final int templateId = intByByteBuf(this.buf, BYTE_LENGTH_2);
            final int fieldCount = intByByteBuf(this.buf, BYTE_LENGTH_2);
            length -= (BYTE_LENGTH_2 * 2);
            final String[] fieldNames = new String[fieldCount];
            final int[] fieldLens = new int[fieldCount];

            // checkpoint: buf 长度小于 length
            for (int f = 0; f < fieldCount && bufGt4Byte(buf); f++) {
                byte[] fieldName = readByte(buf, BYTE_LENGTH_2);
                byte[] fieldLen = readByte(buf, BYTE_LENGTH_2);
                length -= (BYTE_LENGTH_2 * 2);
                final int fieldNameCode = intByByte(fieldName);
                final int fieldLenInt = intByByte(fieldLen);
                fieldNames[f] = transField(fieldNameCode);
                fieldLens[f] = fieldLenInt;
            }
            packs.put(templateId, new TemplatePack(fieldNames, fieldLens));
        }
        return packs.size();
    }
}


