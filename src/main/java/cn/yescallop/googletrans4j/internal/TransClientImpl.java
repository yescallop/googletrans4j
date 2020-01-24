package cn.yescallop.googletrans4j.internal;

import cn.yescallop.googletrans4j.TokenTicketUtil;
import cn.yescallop.googletrans4j.TransClient;
import cn.yescallop.googletrans4j.TransRequest;
import cn.yescallop.googletrans4j.TransResponse;
import cn.yescallop.googletrans4j.internal.util.HttpRequests;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The implementation of {@link TransClient}.
 *
 * @author Scallop Ye
 */
public final class TransClientImpl implements TransClient {

    private final HttpClient httpClient;
    private final boolean isDefaultHttpClient;
    private final Duration requestTimeout;
    private final String host;
    private final boolean insecure;

    private final int[] token = new int[2];

    TransClientImpl(TransClientBuilderImpl builder) {
        if (builder.httpClient == null) {
            httpClient = HttpClient.newHttpClient();
            isDefaultHttpClient = true;
        } else {
            httpClient = builder.httpClient;
            isDefaultHttpClient = false;
        }
        requestTimeout = builder.requestTimeout;
        host = builder.host;
        insecure = builder.insecure;

        String tokenStr;
        if (builder.token != null) {
            tokenStr = builder.token;
        } else {
            try {
                tokenStr = TokenTicketUtil.acquireToken(httpClient, builder.tokenHost, requestTimeout);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Exception acquiring token", e);
            }
        }
        TokenTicketUtil.parseToken(tokenStr, token);
    }

    @Override
    public Optional<HttpClient> httpClient() {
        return isDefaultHttpClient ? Optional.empty() : Optional.of(httpClient);
    }

    @Override
    public Optional<Duration> requestTimeout() {
        return Optional.ofNullable(requestTimeout);
    }

    @Override
    public String host() {
        return host;
    }

    @Override
    public boolean isInsecure() {
        return insecure;
    }

    @Override
    public TransResponse send(TransRequest request) throws IOException, InterruptedException {
        String ticket = TokenTicketUtil.acquireTicket(request.text(), token);
        HttpRequest hr = HttpRequests.translating(this, request, ticket);
        String json = httpClient.send(hr, HttpResponse.BodyHandlers.ofString()).body();
        return TransResponse.parse(json);
    }

    @Override
    public CompletableFuture<TransResponse> sendAsync(TransRequest request) {
        String ticket = TokenTicketUtil.acquireTicket(request.text(), token);
        return httpClient.sendAsync(
                HttpRequests.translating(this, request, ticket),
                HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> TransResponse.parse(resp.body()));
    }
}
