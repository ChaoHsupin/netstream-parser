package site.yan.netstream.decode;

import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static site.yan.netstream.decode.ConstTools.BYTE_LENGTH_2;
import static site.yan.netstream.decode.ConstTools.intByByteBuf;

/**
 * @author zhao xubin
 * @date 2022/06/30
 * 解析NetStream UDP报文
 * 参考华为文档：https://support.huawei.com/enterprise/zh/doc/EDOC1100055170/d494430a
 */
@Data
public class V9NetStreamParser {

    private ByteBuf buf;
    private List<Map<String, ByteValue>> parsed = new LinkedList<>();
    public static final Map<String, TemplatePack> TEMPLATE_CACHE = new HashMap<>(16);

    public V9NetStreamParser(ByteBuf buf) {
        this.buf = buf;
    }

    public void parse() throws Exception {
        if (!buf.isReadable() || buf.readableBytes() < 1) {
            throw new Exception("buff 区不可读");
        }
        // 读取头部
        V9HeaderParser header = new V9HeaderParser(buf);
        header.parse();
        int recordCount = header.count;

        while (buf.readableBytes() > 0 && recordCount > 0) {
            // Export Packet 处理
            int prefix = intByByteBuf(buf, BYTE_LENGTH_2);
            // 回退读取的2个字节
            buf = buf.setIndex(buf.readerIndex() - BYTE_LENGTH_2, buf.writerIndex());

            // 小于256是模版区，大于
            if (0 <= prefix && prefix < 255) {
                // 读取模版区
                V9TemplateFlowParser templateFlowSet = new V9TemplateFlowParser(buf);
                recordCount -= templateFlowSet.parse();

                Map<Integer, TemplatePack> packMap = templateFlowSet.getPacks();
                if (packMap != null && packMap.size() > 0) {
                    packMap.forEach((temId, tem) -> {
                                final String key = header.getSourceIdInt() + "-" + temId;
                                TEMPLATE_CACHE.put(key, tem);
                            }
                    );
                }
            } else {
                // 读取数据区
                V9DataFlowParser dataFlowSet = new V9DataFlowParser(buf, header.getSourceIdInt(), TEMPLATE_CACHE);
                recordCount -= dataFlowSet.parse();
                parsed.addAll(dataFlowSet.getParsed());
            }
        }
    }

}
