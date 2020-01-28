package cn.yescallop.googletrans4j;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A test for translating.
 *
 * @author Scallop Ye
 */
public class TransClientTest {

    private static HttpClient httpClient;
    private static TransClient client;

    @BeforeAll
    public static void init() {
        httpClient = HttpClient.newHttpClient();
        client = TransClient.newBuilder()
                .httpClient(httpClient)
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
        assertEquals("Manjushage", resp.sourceTransliteration().orElseThrow());
    }

    @Test
    public void transAsync() {
        CompletableFuture<?>[] cfs = Stream.of("Así es la vida", "それは人生です", "这是生活", "It's life")
                .map(t -> TransRequest.newBuilder(t)
                        .parameters(TransParameter.TRANSLATION)
                        .targetLang("fr")
                        .build())
                .map(r -> client.sendAsync(r)
                        .thenAccept(res -> assertEquals("C'est la vie", res.translation().orElseThrow())))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(cfs).join();
    }

    @Test
    public void transWithInvalidToken() throws Exception {
        TransClient invalidClient = TransClient.newBuilder()
                .httpClient(httpClient)
                .insecure()
                .token("233333.2333333333")
                .build();
        TransRequest req = TransRequest.langDetecting("test");
        try {
            invalidClient.send(req);
            fail();
        } catch (ResponseUnavailableException e) {
            assertEquals(403, e.statusCode());
        }
    }
}
