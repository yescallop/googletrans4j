package cn.yescallop.googletrans4j;

import cn.yescallop.googletrans4j.util.JapaneseConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Scallop Ye
 */
public class JapaneseConverterTest {

    public static void main(String[] args) {
        TransClient client = TransClient.newBuilder()
                .host("translate.google.cn")
                .build();
        JapaneseConverter jc = JapaneseConverter.create(client);
        try {
            jc.convert(Files.lines(Path.of("convert_test.txt")))
                    .forEach(l -> {
                        System.out.println(l.raw());
                        System.out.println(l.kanaNoted());
                        System.out.println(l.transliteration());
                        System.out.println(l.kana());
                        System.out.println(l.regex());
                        System.out.println();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            //delay or get banned
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
