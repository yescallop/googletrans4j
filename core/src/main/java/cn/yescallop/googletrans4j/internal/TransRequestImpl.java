package cn.yescallop.googletrans4j.internal;

import cn.yescallop.googletrans4j.TransParameter;
import cn.yescallop.googletrans4j.TransRequest;

import java.util.Objects;
import java.util.Set;

/**
 * The implementation of {@link TransRequest}.
 *
 * @author Scallop Ye
 */
public final class TransRequestImpl implements TransRequest {

    private final String sourceLang;
    private final String targetLang;
    private final String text;
    private final Set<TransParameter> params;

    TransRequestImpl(TransRequestBuilderImpl builder) {
        sourceLang = builder.sourceLang;
        targetLang = builder.targetLang;
        text = Objects.requireNonNull(builder.text);
        params = builder.params;
    }

    @Override
    public String sourceLang() {
        return sourceLang;
    }

    @Override
    public String targetLang() {
        return targetLang;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public Set<TransParameter> parameters() {
        return params;
    }
}
