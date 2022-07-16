package site.yan.netstream.decode;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhao xubin
 * @date 2022/06/30
 */
@Data
@AllArgsConstructor
public class TemplatePack {
    private String[] fieldNames;
    private int[] fieldLens;
}
