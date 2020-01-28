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

    private final String base;
    private final Map<String, List<String>> paramsMap = new LinkedHashMap<>();

    public QueryBuilder(String base) {
        this.base = base;
    }

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

    public boolean empty() {
        return base == null && paramsMap.isEmpty();
    }

    public void appendTo(StringBuilder sb) {
        boolean first;
        if (base != null) {
            sb.append(base);
            first = false;
        } else first = true;

        for (Map.Entry<String, List<String>> e : paramsMap.entrySet()) {
            String name = URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8);
            for (String value : e.getValue()) {
                if (!first) sb.append('&');
                else first = false;

                sb.append(name);
                sb.append('=');
                sb.append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        }
    }
}
