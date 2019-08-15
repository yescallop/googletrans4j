package cn.yescallop.googletrans4j;

import cn.yescallop.googletrans4j.internal.TransResponseImpl;
import com.google.gson.JsonArray;

import java.util.Map;
import java.util.Optional;

/**
 * Declares a response of a {@code TransRequest}.
 *
 * @author Scallop Ye
 */
public interface TransResponse {

    /**
     * Creates a response parsed from the given json {@code String}.
     *
     * @param json the json {@code String}.
     * @return a new response.
     */
    static TransResponse parse(String json) {
        return new TransResponseImpl(json);
    }

    /**
     * Returns the raw response as {@code String}.
     *
     * @return the raw response.
     */
    String rawResponse();

    /**
     * Returns the parsed {@code JsonArray}.
     *
     * @return the parsed {@code JsonArray}.
     */
    JsonArray jsonArray();

    /**
     * Returns the source language.
     *
     * @return the source language.
     */
    String sourceLang();

    /**
     * Returns an {@code Optional} containing the translation text.
     *
     * @return an {@code Optional<String>}.
     */
    Optional<String> translation();

    /**
     * Returns an {@code Optional} containing the transliteration of the source text.
     *
     * @return an {@code Optional<String>}.
     */
    Optional<String> sourceTransliteration();

    /**
     * Returns an {@code Optional} containing the transliteration of the translation.
     *
     * @return an {@code Optional<String>}.
     */
    Optional<String> targetTransliteration();

    /**
     * Returns the language detection map, mapping each language to its confidence.
     *
     * @return the language detection map.
     */
    Map<String, Float> langDetection();
}
