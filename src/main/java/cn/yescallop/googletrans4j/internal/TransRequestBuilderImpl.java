package cn.yescallop.googletrans4j.internal;

import cn.yescallop.googletrans4j.TransParameter;
import cn.yescallop.googletrans4j.TransRequest;

import java.util.Objects;
import java.util.Set;

/**
 * The implementation of {@link TransRequest.Builder}.
 *
 * @author Scallop Ye
 */
public final class TransRequestBuilderImpl implements TransRequest.Builder {

    String sourceLang = "auto";
    String targetLang = "";
    String text;
    Set<TransParameter> params = Set.of(TransParameter.TRANSLATION);

    @Override
    public TransRequestBuilderImpl sourceLang(String lang) {
        sourceLang = Objects.requireNonNull(lang);
        return this;
    }

    @Override
    public TransRequestBuilderImpl targetLang(String lang) {
        targetLang = Objects.requireNonNull(lang);
        return this;
    }

    @Override
    public TransRequestBuilderImpl text(String text) {
        this.text = Objects.requireNonNull(text);
        return this;
    }

    @Override
    public TransRequestBuilderImpl parameters(TransParameter... params) {
        this.params = Set.of(params);
        return this;
    }

    @Override
    public TransRequestImpl build() {
        return new TransRequestImpl(this);
    }
}