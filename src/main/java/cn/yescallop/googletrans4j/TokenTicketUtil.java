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
 * A utility class for acquiring tokens and tickets for <i>Google Translate</i> requests.
 * <p>
 * Definitions:
 * 1) token: tkk
 * 2) ticket: tk
 *
 * @author Scallop Ye
 */
public final class TokenTicketUtil {

    /**
     * A {@code Pattern} matching the token in the page.
     */
    private static final Pattern PATTERN_TOKEN = Pattern.compile("tkk:'(.+?)'");

    private TokenTicketUtil() {
        //no instance
    }

    /**
     * Parses the token from a string into an int array.
     *
     * @param str the token string.
     * @param token the int array.
     */
    public static void parseToken(String str, int[] token) {
        int dot = str.indexOf('.');
        token[0] = (int) Long.parseLong(str, 0, dot, 10);
        token[1] = (int) Long.parseLong(str, dot + 1, str.length(), 10);
    }

    /**
     * Acquires a token with the given arguments.
     *
     * @param httpClient the {@code HttpClient} used for HTTP requests.
     * @param host the host of <i>Google Translate</i>.
     * @param requestTimeout the timeout of an HTTP request.
     * @return the token.
     */
    public static String acquireToken(HttpClient httpClient, String host, Duration requestTimeout)
            throws IOException, InterruptedException {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create("https://" + Objects.requireNonNull(host)))
                .header("User-Agent", TransClient.USER_AGENT);
        if (requestTimeout != null)
            b.timeout(requestTimeout);
        HttpRequest req = b.build();
        HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        Matcher matcher = PATTERN_TOKEN.matcher(resp.body());
        if (!matcher.find())
            throw new IOException("token not found");
        return matcher.group(1);
    }

    /**
     * Acquires a ticket by the given text and token.
     *
     * @param text the text.
     * @param token the token.
     * @return the ticket.
     */
    public static String acquireTicket(String text, int[] token) {
        int len = text.length();
        Reducer e = new Reducer(token[0]);

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
        a ^= token[1];
        a = (int) ((a & 0xffffffffL) % 1000000);
        return a + "." + (a ^ token[0]);
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
