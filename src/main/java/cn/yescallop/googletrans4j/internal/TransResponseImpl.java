package cn.yescallop.googletrans4j.internal;

import cn.yescallop.googletrans4j.TransResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

/**
 * The implementation of {@link TransResponse}.
 *
 * @author Scallop Ye
 */
public final class TransResponseImpl implements TransResponse {

    private static final Gson GSON = new Gson();

    private final String raw;
    private final JsonArray arr;

    public TransResponseImpl(String json) {
        raw = json;
        arr = GSON.fromJson(json, JsonArray.class);
    }

    @Override
    public String rawResponse() {
        return raw;
    }

    @Override
    public JsonArray jsonArray() {
        return arr;
    }
}
