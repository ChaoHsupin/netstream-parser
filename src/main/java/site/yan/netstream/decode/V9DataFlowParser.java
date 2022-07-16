package site.yan.netstream.decode;

import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static site.yan.netstream.decode.ConstTools.*;

/**
 * @author zhao xubin
 * @date 2022/06/30
 * 解析数据区
 */

@Data
public class V9DataFlowParser {

    private ByteBuf buf;
    private int sourceId;
    private Map<String, TemplatePack> templateCache;
    private List<Map<String, ByteValue>> parsed = new LinkedList<>();

    public V9DataFlowParser(ByteBuf buf, int sourceId, Map<String, TemplatePack> templateCache) {
        this.buf = buf;
        this.sourceId = sourceId;
        this.templateCache = templateCache;
    }

    public int parse() throws Exception {
        int flowSetId = intByByteBuf(this.buf, BYTE_LENGTH_2);
        int length = intByByteBuf(this.buf, BYTE_LENGTH_2);
        length -= (BYTE_LENGTH_2 * 2);
        String key = sourceId + "-" + flowSetId;
        TemplatePack template = templateCache.get(key);
        if (template == null) {
            return parsed.size();
        }
        String[] fieldNames = template.getFieldNames();
        int[] fieldLens = template.getFieldLens();

        int index = 0;
        Map<String, ByteValue> temp = new HashMap<>(16);

        // checkpoint: buf 长度小于 length
        while (length > 0 && bufGtByte(buf, fieldLens[index])) {
            byte[] value = readByte(buf, fieldLens[index]);
            length -= fieldLens[index];
            temp.put(fieldNames[index], new ByteValue(value));
            index = (++index) % fieldNames.length;
            if (index == 0) {
                parsed.add(temp);
                temp = new HashMap<>(16);
            }
        }
        return parsed.size();
    }
}
