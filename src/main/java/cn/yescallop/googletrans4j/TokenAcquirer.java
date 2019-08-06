package cn.yescallop.googletrans4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author Scallop Ye
 */
public class TokenAcquirer {

    private static final Pattern PATTERN_TKK = Pattern.compile("tkk:'(.+?)'");

    private final HttpClient httpClient;
    private final HttpRequest request;

    private long[] tkk = new long[2];

    public TokenAcquirer(HttpClient httpClient, String host) {
        this.httpClient = httpClient;
        this.request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + host))
                .header("User-Agent", Constants.USER_AGENT)
                .timeout(Duration.ofSeconds(5))
                .build();
    }

    private void updateTkk() throws IOException, InterruptedException {
        long curHours = System.currentTimeMillis() / 3600000;
        if (curHours == tkk[0])
            return;

        String resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
        Matcher matcher = PATTERN_TKK.matcher(resp);
        if (!matcher.find())
            throw new RuntimeException("tkk not found");

        String tkkStr = matcher.group(1);
        int dot = tkkStr.indexOf('.');
        tkk[0] = Long.parseLong(tkkStr, 0, dot, 10);
        tkk[1] = Long.parseLong(tkkStr, dot + 1, tkkStr.length(), 10);
    }

    public String acquire(String text) throws IOException, InterruptedException {
        updateTkk();
        return calc(text);
    }

    private String calc(String text) {
        int len = text.length();
        IntStream.Builder e = IntStream.builder();

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

        long a = e.build()
                .asLongStream()
                .reduce(tkk[0], (b, c) -> uo(b + c, "+-a^+6"));
        a = uo(a, "+-3^+b+-f");
        a ^= tkk[1];
        if (a < 0) a = (a & 2147483647) + 2147483648L;
        a %= 1E6;
        return a + "." + (a ^ tkk[0]);
    }

    private static long uo(long a, String b) {
        int len = b.length();
        for (int c = 0; c < len - 2; c += 3) {
            int d = b.charAt(c + 2);
            d = d >= 'a' ? d - 87 : ((d >= '0' && d <= '9') ? d - '0' : 0);
            long dl = b.charAt(c + 1) == '+' ? a >>> d : a << d;
            a = b.charAt(c) == '+' ? a + dl & 4294967295L : a ^ dl;
        }
        return (int) a;
    }
}
