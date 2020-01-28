package cn.yescallop.googletrans4j.internal.util;

import java.net.URI;

/**
 * A builder for {@link URI} with query parameters.
 *
 * @author Scallop Ye
 */
public final class URIBuilder {

    private final URI baseUri;
    private final QueryBuilder queryBuilder;

    public URIBuilder(URI baseUri) {
        if (baseUri.isOpaque())
            throw new IllegalArgumentException("opaque base URI");
        this.baseUri = baseUri;
        queryBuilder = new QueryBuilder(baseUri.getRawQuery());
    }

    public URIBuilder(String baseUri) {
        this(URI.create(baseUri));
    }

    public URIBuilder parameter(String name, String value) {
        queryBuilder.addParameter(name, value);
        return this;
    }

    public URIBuilder parameter(String name, String[] values) {
        queryBuilder.addParameter(name, values);
        return this;
    }

    public URI toURI() {
        return URI.create(toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String scheme = baseUri.getScheme();
        if (scheme != null) {
            sb.append(scheme);
            sb.append(':');
        }
        String authority = baseUri.getRawAuthority();
        if (authority != null) {
            sb.append("//");
            sb.append(authority);
        }
        String path = baseUri.getRawPath();
        if (path != null) {
            sb.append(path);
        }
        if (!queryBuilder.empty()) {
            sb.append('?');
            queryBuilder.appendTo(sb);
        }
        String fragment = baseUri.getRawFragment();
        if (fragment != null) {
            sb.append('#');
            sb.append(fragment);
        }
        return sb.toString();
    }
}
