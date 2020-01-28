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
        URIBuilder b = new URIBuilder("https://yescallop.cn/test?k=v#l")
                .parameter("k1", "值1")
                .parameter("k2", new String[]{"v%1", "v&2"});

        assertEquals("https://yescallop.cn/test?k=v&k1=%E5%80%BC1&k2=v%251&k2=v%262#l",
                b.toString());
    }
}
