package cn.yescallop.googletrans4j;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * A test for asynchronous translating.
 *
 * @author Scallop Ye
 */
public class TransAsyncTest {

    public static void main(String[] args) {
        TransClient client = TransClient.newBuilder()
                .host("translate.google.cn")
                .build();

        CompletableFuture[] cfs = Stream.of("Así es la vida", "それは人生です", "这是生活", "It's life")
                .map(t -> TransRequest.newBuilder(t)
                        .parameters(TransParameter.TRANSLITERATION)
                        .targetLang("fr")
                        .build())
                .map(r -> client.sendAsync(r)
                        .thenAccept(System.out::println))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(cfs).join();
    }
}
