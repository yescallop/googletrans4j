package cn.yescallop.googletrans4j.internal.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A builder for query strings.
 *
 * @author Scallop Ye
 */
public final class QueryBuilder {

    private final Map<String, List<String>> paramsMap = new LinkedHashMap<>();

    public void addParameter(String name, String value) {
        paramsMap.computeIfAbsent(name, k -> new ArrayList<>(1))
                .add(value);
    }

    public void addParameter(String name, String[] values) {
        if (values.length == 0)
            throw new IllegalArgumentException("empty values");
        Collections.addAll(
                paramsMap.computeIfAbsent(name, k -> new ArrayList<>(values.length)),
                values
        );
    }

    public void clear() {
        paramsMap.clear();
    }

    public Map<String, List<String>> map() {
        return paramsMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> e : paramsMap.entrySet()) {
            String name = URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8);
            for (String value : e.getValue()) {
                if (sb.length() != 0) {
                    sb.append('&');
                }
                sb.append(name);
                sb.append('=');
                sb.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        }
        return sb.toString();
    }
}
