package cn.yescallop.googletrans4j;

import cn.yescallop.googletrans4j.internal.TransResponseImpl;
import com.google.gson.JsonArray;

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
}
