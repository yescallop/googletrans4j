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
                .token("438839.2101722932")
                .insecure()
                .build();
        JapaneseConverter jc = JapaneseConverter.create(client);
        try {
            jc.convert(Files.lines(Path.of("convert_test.txt")))
                    .forEach(l -> {
                        if (l == null) {
                            System.out.println();
                            return;
                        }

                        System.out.println(l.raw());
                        if (l.kanaNoted() != null) {
                            System.out.println(l.kanaNoted());
                        } else {
                            System.out.println("ERROR: " + l.raw());
                            l.exception().ifPresent(Throwable::printStackTrace);
                        }
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
