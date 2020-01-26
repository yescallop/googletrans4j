package cn.yescallop.googletrans4j.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Scallop Ye
 */
public class JapaneseConverterTest {

    @Test
    public void convert() {
        JapaneseConverter.Line res = JapaneseConverter.convert(
                "天気が良いから、散歩しましょう。",
                "tenkigaii kara, sanpo shimashou."
        );
        assertEquals(res.kana(), "てんきがいい から/さんぽ しましょう/");
        assertEquals(res.kanaNoted(), "天気(てんき)が良(い)いから、散歩(さんぽ)しましょう。");
    }
}
