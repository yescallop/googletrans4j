package cn.yescallop.googletrans4j.internal.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * A builder for {@link URI} with query parameters.
 *
 * @author Scallop Ye
 */
public final class URIBuilder {

    private final URI baseUri;
    private final QueryBuilder queryBuilder = new QueryBuilder();

    public URIBuilder(URI baseUri) {
        this.baseUri = Objects.requireNonNull(baseUri);
    }

    public URIBuilder(String baseUri) {
        this.baseUri = URI.create(baseUri);
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
        try {
            return new URI(
                    baseUri.getScheme(),
                    baseUri.getAuthority(),
                    baseUri.getPath(),
                    queryBuilder.toString(),
                    baseUri.getFragment()
            );
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return toURI().toString();
    }
}
