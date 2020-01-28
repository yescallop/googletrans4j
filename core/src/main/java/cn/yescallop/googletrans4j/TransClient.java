package cn.yescallop.googletrans4j;

import cn.yescallop.googletrans4j.internal.TransClientBuilderImpl;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * A <i>Google Translate</i> client.
 *
 * @author Scallop Ye
 */
public interface TransClient {

    /**
     * The user agent used for HTTP requests.
     */
    String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/76.0.3809.87 Safari/537.36";

    /**
     * Returns a new {@code TransClient} with default settings.
     *
     * @return a new client.
     */
    static TransClient newTransClient() {
        return newBuilder().build();
    }

    /**
     * Creates a new {@code TransClient} builder.
     *
     * @return a new builder.
     */
    static Builder newBuilder() {
        return new TransClientBuilderImpl();
    }

    /**
     * A builder of {@code TransClient}.
     */
    interface Builder {

        /**
         * Sets the {@code HttpClient} used for HTTP requests.
         * <p>
         * If not invoked, a default {@code HttpClient} will be used.
         *
         * @param client the client.
         * @return this builder.
         */
        Builder httpClient(HttpClient client);

        /**
         * Sets the timeout for an HTTP request.
         *
         * @param duration the timeout duration.
         * @return this builder.
         */
        Builder requestTimeout(Duration duration);

        /**
         * Sets the host of <i>Google Translate</i> for the client.
         * <p>
         * If not invoked, "translate.googleapis.com" will be used.
         *
         * @param host the host.
         * @return this builder.
         */
        Builder host(String host);

        /**
         * Sets the token for the client.
         *
         * @param token the token.
         * @return this builder.
         * @see TransClient.Builder#tokenHost(String)
         */
        Builder token(String token);

        /**
         * Sets the host for acquiring token for the client.
         * <p>
         * If not invoked, "translate.google.com" will be used.
         * If the token is already set, this method will not
         * have any effect.
         *
         * @param host the token host.
         * @return this builder.
         * @see TransClient.Builder#token(String)
         */
        Builder tokenHost(String host);

        /**
         * Uses insecure HTTP connections for the client.
         *
         * @return this builder.
         */
        Builder insecure();

        /**
         * Builds the client.
         *
         * @return a new client.
         */
        TransClient build();
    }

    /**
     * Returns an {@code Optional} containing the {@code HttpClient} used by
     * this client. If no {@code HttpClient} was set in the client's builder,
     * then the {@code Optional} is empty.
     *
     * @return the {@code HttpClient}.
     */
    Optional<HttpClient> httpClient();

    /**
     * Returns an {@code Optional} containing the request timeout.
     *
     * @return an {@code Optional<Duration>}.
     */
    Optional<Duration> requestTimeout();

    /**
     * Returns the host of <i>Google Translate</i> for this client.
     *
     * @return the host.
     */
    String host();

    /**
     * Returns whether this client uses insecure HTTP connections.
     */
    boolean insecure();

    /**
     * Sends the given request using this client.
     *
     * @param request the request.
     * @return the response.
     * @throws IOException if an I/O error occurs in the HTTP request.
     * @throws InterruptedException if the operation is interrupted.
     */
    TransResponse send(TransRequest request) throws IOException, InterruptedException;

    /**
     * Sends the given request using this client asynchronously.
     *
     * @param request the request.
     * @return a {@code CompletableFuture} of the response.
     */
    CompletableFuture<TransResponse> sendAsync(TransRequest request);
}
