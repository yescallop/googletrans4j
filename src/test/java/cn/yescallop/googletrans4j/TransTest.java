package cn.yescallop.googletrans4j;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A test for translating.
 *
 * @author Scallop Ye
 */
public class TransTest {

    private static TransClient client;

    @BeforeAll
    public static void init() {
        client = TransClient.newBuilder()
                .insecure()
                .tokenHost("translate.google.cn")
                .build();
    }

    @Test
    public void transSync() throws Exception {
        TransRequest req = TransRequest.newBuilder()
                .text("曼珠沙華")
                .sourceLang("ja")
                .parameters(TransParameter.TRANSLITERATION)
                .build();
        TransResponse resp = client.send(req);
        assertEquals(resp.sourceTransliteration().orElseThrow(), "Manjushage");
    }

    @Test
    public void transAsync() {
        CompletableFuture<?>[] cfs = Stream.of("Así es la vida", "それは人生です", "这是生活", "It's life")
                .map(t -> TransRequest.newBuilder(t)
                        .parameters(TransParameter.TRANSLATION)
                        .targetLang("fr")
                        .build())
                .map(r -> client.sendAsync(r)
                        .thenAccept(res -> assertEquals(res.translation().orElseThrow(), "C'est la vie")))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(cfs).join();
    }
}
