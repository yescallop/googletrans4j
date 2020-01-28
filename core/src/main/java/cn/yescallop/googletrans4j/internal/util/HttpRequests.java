package cn.yescallop.googletrans4j.internal.util;

import cn.yescallop.googletrans4j.TransClient;
import cn.yescallop.googletrans4j.TransParameter;
import cn.yescallop.googletrans4j.TransRequest;

import java.net.URI;
import java.net.URISyntaxException;
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

    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=UTF-8";
    private static final String TRANSLATE_PATH = "/translate_a/single";

    private HttpRequests() {
        //no instance
    }

    public static HttpRequest translating(TransClient client, TransRequest request, String ticket) {
        URI uri = buildTranslateURI(client, request, ticket);
        String query = "q=" + URLEncoder.encode(request.text(), StandardCharsets.UTF_8);
        HttpRequest.Builder b = HttpRequest.newBuilder(uri)
                .header("User-Agent", TransClient.USER_AGENT)
                .header("Content-Type", CONTENT_TYPE)
                .POST(HttpRequest.BodyPublishers.ofString(query));
        client.requestTimeout().ifPresent(b::timeout);
        return b.build();
    }

    private static URI buildTranslateURI(TransClient client,
                                         TransRequest req,
                                         String ticket) {
        URI base;
        try {
            base = new URI(client.insecure() ? "http" : "https",
                    client.host(),
                    TRANSLATE_PATH,
                    null);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        URIBuilder b = new URIBuilder(base)
                .parameter("client", "t")
                .parameter("sl", req.sourceLang())
                .parameter("tl", req.targetLang());
        Set<TransParameter> params = req.parameters();

        if (!params.isEmpty()) {
            b.parameter("dt", params.stream()
                    .map(TransParameter::value)
                    .toArray(String[]::new));
        }
        return b.parameter("tk", ticket).toURI();
    }
}
