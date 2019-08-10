package cn.yescallop.googletrans4j.internal;

import cn.yescallop.googletrans4j.TokenAcquirer;
import cn.yescallop.googletrans4j.TransClient;
import cn.yescallop.googletrans4j.TransRequest;
import cn.yescallop.googletrans4j.TransResponse;
import cn.yescallop.googletrans4j.internal.util.HttpRequests;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * The implementation of {@link TransClient}.
 *
 * @author Scallop Ye
 */
public final class TransClientImpl implements TransClient {

    private final HttpClient httpClient;
    private final Duration requestTimeout;
    private final String host;
    private final TokenAcquirer tokenAcquirer;

    TransClientImpl(TransClientBuilderImpl builder) {
        if (builder.httpClient == null) {
            httpClient = HttpClient.newHttpClient();
        } else {
            httpClient = builder.httpClient;
        }
        requestTimeout = builder.requestTimeout;
        host = Objects.requireNonNull(builder.host);
        tokenAcquirer = new TokenAcquirer(httpClient, host, requestTimeout);
    }

    @Override
    public HttpClient httpClient() {
        return httpClient;
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
    public TransResponse send(TransRequest request) throws IOException, InterruptedException {
        String token = tokenAcquirer.acquire(request.text());
        HttpRequest hr = HttpRequests.translating(this, request, token);
        String json = httpClient.send(hr, HttpResponse.BodyHandlers.ofString()).body();
        return TransResponse.parse(json);
    }

    @Override
    public CompletableFuture<TransResponse> sendAsync(TransRequest request) {
        return tokenAcquirer.acquireAsync(request.text())
                .thenCompose(token -> httpClient.sendAsync(
                        HttpRequests.translating(this, request, token),
                        HttpResponse.BodyHandlers.ofString()))
                .thenApply(resp -> TransResponse.parse(resp.body()));
    }
}
