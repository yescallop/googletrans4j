package cn.yescallop.googletrans4j.internal;

import cn.yescallop.googletrans4j.TransResponse;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The implementation of {@link TransResponse}.
 *
 * @author Scallop Ye
 */
public final class TransResponseImpl implements TransResponse {

    private static final Gson GSON = new Gson();

    private final String raw;
    private final JsonArray arr;

    private String sourceLang;
    private String translation;
    private String[] transliterations = new String[2];
    private Map<String, Float> langDetection;

    public TransResponseImpl(String json) {
        raw = json;
        try {
            arr = GSON.fromJson(raw, JsonArray.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            System.out.println(raw);
            throw e;
        }
        parse();
    }

    private void parse() {
        sourceLang = arr.get(2).getAsString();
        if (arr.get(0).isJsonArray()) {
            parseTranslation(GSON.fromJson(arr.get(0), JsonArray[].class));
        }
        parseLangDetection(arr.get(8).getAsJsonArray());
    }

    private void parseTranslation(JsonArray[] arrs) {
        if (arrs.length == 0)
            return;
        StringBuilder sb = new StringBuilder();
        for (JsonArray arr : arrs) {
            if (arr.size() < 5) {
                parseTransliteration(arr);
                break;
            }
            sb.append(arr.get(0).getAsString());
        }
        translation = sb.toString();
    }

    private void parseTransliteration(JsonArray arr) {
        int available = arr.size() - 2;
        for (int i = 0; i < available; i++) {
            JsonElement e = arr.get(i + 2);
            if (!e.isJsonNull())
                transliterations[i] = e.getAsString();
        }
    }

    private void parseLangDetection(JsonArray arr) {
        String[] lang = GSON.fromJson(arr.get(3), String[].class);
        float[] confidence = GSON.fromJson(arr.get(2), float[].class);
        langDetection = new HashMap<>(lang.length);
        for (int i = 0; i < lang.length; i++) {
            langDetection.put(lang[i], confidence[i]);
        }
    }

    @Override
    public String rawResponse() {
        return raw;
    }

    @Override
    public JsonArray jsonArray() {
        return arr;
    }

    @Override
    public String sourceLang() {
        return sourceLang;
    }

    @Override
    public Optional<String> translation() {
        return Optional.ofNullable(translation);
    }

    @Override
    public Optional<String> sourceTransliteration() {
        return Optional.ofNullable(transliterations[1]);
    }

    @Override
    public Optional<String> targetTransliteration() {
        return Optional.ofNullable(transliterations[0]);
    }

    @Override
    public Map<String, Float> langDetection() {
        return langDetection;
    }

    @Override
    public String toString() {
        return "TransResponseImpl{" +
                "sourceLang='" + sourceLang + '\'' +
                ", translation='" + translation + '\'' +
                ", transliterations=" + Arrays.toString(transliterations) +
                ", langDetection=" + langDetection +
                '}';
    }
}
