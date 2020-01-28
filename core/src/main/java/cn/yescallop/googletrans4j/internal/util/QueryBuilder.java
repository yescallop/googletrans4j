package cn.yescallop.googletrans4j.internal.util;

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

    @Override
    public String toString() {
        StringBuilder sb = base == null ? new StringBuilder() : new StringBuilder(base);
        for (Map.Entry<String, List<String>> e : paramsMap.entrySet()) {
            for (String value : e.getValue()) {
                if (sb.length() != 0) {
                    sb.append('&');
                }
                sb.append(e.getKey());
                sb.append('=');
                sb.append(value);
            }
        }
        return sb.toString();
    }
}
