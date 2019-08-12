package cn.yescallop.googletrans4j.internal;

import cn.yescallop.googletrans4j.TransClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Objects;

/**
 * The implementation of {@link TransClient.Builder}.
 *
 * @author Scallop Ye
 */
public final class TransClientBuilderImpl implements TransClient.Builder {

    HttpClient httpClient;
    Duration requestTimeout;
    String host = "translate.google.com";

    @Override
    public TransClientBuilderImpl httpClient(HttpClient client) {
        httpClient = Objects.requireNonNull(client);
        return this;
    }

    @Override
    public TransClientBuilderImpl requestTimeout(Duration duration) {
        if (duration.isNegative() || Duration.ZERO.equals(duration))
            throw new IllegalArgumentException("Invalid duration: " + duration);
        requestTimeout = duration;
        return this;
    }

    @Override
    public TransClientBuilderImpl host(String host) {
        this.host = Objects.requireNonNull(host);
        return this;
    }

    @Override
    public TransClientImpl build() {
        return new TransClientImpl(this);
    }
}
