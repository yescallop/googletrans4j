package cn.yescallop.googletrans4j;

import cn.yescallop.googletrans4j.internal.TransRequestBuilderImpl;

import java.util.Set;

/**
 * A <i>Google Translate</i> request.
 *
 * @author Scallop Ye
 */
public interface TransRequest {

    /**
     * Creates a new {@code TransRequest} builder.
     *
     * @return a new builder.
     */
    static Builder newBuilder() {
        return new TransRequestBuilderImpl();
    }

    /**
     * Creates a new {@code TransRequest} builder with the given text.
     *
     * @param text the text.
     * @return a new builder.
     */
    static Builder newBuilder(String text) {
        return newBuilder().text(text);
    }

    /**
     * Creates a new {@code TransRequest} for detecting the language of the given text.
     *
     * @param text the text.
     * @return a new request.
     */
    static TransRequest langDetecting(String text) {
        return newBuilder(text)
                .parameters()
                .build();
    }

    /**
     * A builder of {@code TransRequest}.
     */
    interface Builder {

        /**
         * Sets the source language.
         * <p>
         * If not invoked, "auto" will be used.
         *
         * @param lang the source language.
         * @return this builder.
         */
        Builder sourceLang(String lang);

        /**
         * Sets the target language.
         * <p>
         * If not invoked, this value will remain empty.
         *
         * @param lang the target language.
         * @return this builder.
         */
        Builder targetLang(String lang);

        /**
         * Sets the text of the request.
         *
         * @param text the text.
         * @return this builder.
         */
        Builder text(String text);

        /**
         * Sets the parameters of the request.
         * <p>
         * If not invoked, {@link TransParameter#TRANSLATION} will be used.
         *
         * @param params the parameters.
         * @return this builder.
         */
        Builder parameters(TransParameter... params);

        /**
         * Builds the request.
         *
         * @return the new request.
         */
        TransRequest build();
    }

    /**
     * Returns the source language of this request.
     *
     * @return the source language.
     */
    String sourceLang();

    /**
     * Returns the target language of this request.
     *
     * @return the target language.
     */
    String targetLang();

    /**
     * Returns the text of this request.
     *
     * @return the text.
     */
    String text();

    /**
     * Returns the parameters of this request.
     *
     * @return the parameters.
     */
    Set<TransParameter> parameters();
}
