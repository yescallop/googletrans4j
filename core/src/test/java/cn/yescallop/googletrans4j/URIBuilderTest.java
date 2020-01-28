package cn.yescallop.googletrans4j;

import cn.yescallop.googletrans4j.internal.util.URIBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Scallop Ye
 */
public class URIBuilderTest {

    @Test
    public void build() {
        URIBuilder b = new URIBuilder("https://yescallop.cn/test")
                .parameter("k1", "v1")
                .parameter("k2", "v2")
                .parameter("键", new String[]{"值1", "值2"});

        assertEquals("https://yescallop.cn/test?k1=v1&k2=v2&%E9%94%AE=%E5%80%BC1&%E9%94%AE=%E5%80%BC2",
                b.toString());
    }
}
