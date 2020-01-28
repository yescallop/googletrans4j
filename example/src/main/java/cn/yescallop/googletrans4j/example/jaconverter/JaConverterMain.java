package cn.yescallop.googletrans4j.example.jaconverter;

import cn.yescallop.googletrans4j.TransClient;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author Scallop Ye
 */
public class JaConverterMain {

    public static void main(String[] args) {
        TransClient client = TransClient.newBuilder()
                .tokenHost("translate.google.cn")
                .insecure()
                .build();
        JaConverter jc = JaConverter.create(client);
        try {
            jc.convert(Files.lines(Path.of(ClassLoader.getSystemResource("jaconverter_test.txt").toURI())))
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
