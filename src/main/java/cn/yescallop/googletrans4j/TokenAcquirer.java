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

    private int[] tkk = new int[2];

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
        tkk[0] = (int) Long.parseLong(tkkStr, 0, dot, 10);
        tkk[1] = (int) Long.parseLong(tkkStr, dot + 1, tkkStr.length(), 10);
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

        int a = e.build()
                .reduce(tkk[0], (b, c) -> uo(b + c, "+-a^+6"));
        a = uo(a, "+-3^+b+-f");
        a ^= tkk[1];
        long res = a < 0 ?  (a & 2147483647) + 2147483648L : a;
        res %= 1E6;
        return res + "." + (res ^ tkk[0]);
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
}
