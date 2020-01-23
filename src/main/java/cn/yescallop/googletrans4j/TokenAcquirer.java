package cn.yescallop.googletrans4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@code TokenAcquirer} supplies tokens for <i>Google Translate</i> requests.
 *
 * @author Scallop Ye
 */
public final class TokenAcquirer {

    /**
     * A {@code Pattern} matching the tkk in the page.
     */
    private static final Pattern PATTERN_TKK = Pattern.compile("tkk:'(.+?)'");

    private final int[] tkk = new int[2];

    /**
     * Constructs a {@code TokenAcquirer} with the given tkk string.
     *
     * @param tkkStr the tkk string.
     */
    public TokenAcquirer(String tkkStr) {
        int dot = tkkStr.indexOf('.');
        tkk[0] = (int) Long.parseLong(tkkStr, 0, dot, 10);
        tkk[1] = (int) Long.parseLong(tkkStr, dot + 1, tkkStr.length(), 10);
    }

    /**
     * Creates a {@code TokenAcquirer} with the given arguments.
     *
     * @param httpClient the {@code HttpClient} used for HTTP requests.
     * @param host the host of <i>Google Translate</i>.
     * @param requestTimeout the timeout of an HTTP request.
     */
    public static TokenAcquirer create(HttpClient httpClient, String host, Duration requestTimeout)
            throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create("https://" + Objects.requireNonNull(host)))
                .header("User-Agent", TransClient.USER_AGENT);
        if (requestTimeout != null)
            b.timeout(requestTimeout);
        HttpRequest req = b.build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        Matcher matcher = PATTERN_TKK.matcher(resp.body());
        if (!matcher.find())
            throw new RuntimeException("tkk not found");
        String tkkStr = matcher.group(1);

        return new TokenAcquirer(tkkStr);
    }

    /**
     * Acquires a token by the given text.
     *
     * @param text the text.
     * @return the token.
     */
    public String acquire(String text) {
        int len = text.length();
        Reducer e = new Reducer(tkk[0]);

        for (int g = 0; g < len; g++) {
            int h = text.codePointAt(g);
            if (h < 128) {
                e.accept(h);
            } else {
                if (h < 2048) {
                    e.accept(h >> 6 | 192);
                } else {
                    if ((h & 64512) == 55296 &&
                            g + 1 < len &&
                            (text.codePointAt(g + 1) & 64512) == 56320) {
                        h = 65536 + ((h & 1023) << 10) + (text.codePointAt(++g) & 1023);
                        e.accept(h >> 18 | 240);
                        e.accept(h >> 12 & 63 | 128);
                    } else {
                        e.accept(h >> 12 | 224);
                    }
                    e.accept(h >> 6 & 63 | 128);
                }
                e.accept(h & 63 | 128);
            }
        }

        int a = e.get();
        a ^= tkk[1];
        a = (int) ((a & 0xffffffffL) % 1000000);
        return a + "." + (a ^ tkk[0]);
    }

    private static int uo(int a, String b) {
        int len = b.length();
        for (int c = 0; c < len - 2; c += 3) {
            int d = b.charAt(c + 2);
            d = d >= 'a' ? d - 87 : ((d >= '0' && d <= '9') ? d - '0' : 0);
            d = b.charAt(c + 1) == '+' ? a >>> d : a << d;
            a = b.charAt(c) == '+' ? a + d : a ^ d;
        }
        return a;
    }

    private static class Reducer {

        private int state;

        Reducer(int identity) {
            state = identity;
        }

        void accept(int v) {
            state = uo(state + v, "+-a^+6");
        }

        int get() {
            return uo(state, "+-3^+b+-f");
        }
    }
}
