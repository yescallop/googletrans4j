package cn.yescallop.googletrans4j.internal.util;

import cn.yescallop.googletrans4j.TransClient;
import cn.yescallop.googletrans4j.TransParameter;
import cn.yescallop.googletrans4j.TransRequest;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * A utility class for creating HttpRequests.
 *
 * @author Scallop Ye
 */
public final class HttpRequests {

    private HttpRequests() {
        //no instance
    }

    public static HttpRequest translating(TransClient client, TransRequest request, String token) {
        URI uri = buildTranslateURI(client.host(), request, token);
        String query = "q=" + URLEncoder.encode(request.text(), StandardCharsets.UTF_8);
        HttpRequest.Builder b = HttpRequest.newBuilder(uri)
                .header("User-Agent", TransClient.USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(query));
        client.requestTimeout().ifPresent(b::timeout);
        return b.build();
    }

    private static URI buildTranslateURI(String host,
                                         TransRequest req,
                                         String token) {
        URIBuilder b = new URIBuilder("https://" + host + "/translate_a/single")
                .parameter("client", "webapp")
                .parameter("sl", req.sourceLang())
                .parameter("tl", req.targetLang());
        Set<TransParameter> params = req.parameters();

        String[] dtv = params.stream()
                .map(TransParameter::value)
                .toArray(String[]::new);

        if (dtv.length != 0) {
            b.parameter("dt", dtv);
        }
        if (params.contains(TransParameter.TRANSLITERATION)) {
            b.parameter("dt", TransParameter.TRANSLATION.value());
        }
        return b.parameter("tk", token).toURI();
    }
}
