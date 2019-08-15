package cn.yescallop.googletrans4j;

/**
 * Declares the possible values for the query parameter key "dt"
 * in an HTTP request for <i>Google Translate</i>.
 *
 * @author Scallop Ye
 */
public enum TransParameter {
    ALTERNATE_TRANSLATIONS("at"),
    ALL_TRANSLATIONS("bd"),
    EXAMPLES("ex"),
    UNKNOWN_LD("ld"),
    DEFINITIONS("md"),
    SPELLING_CORRECTION("qca"),
    SEE_ALSO("rw"),
    TRANSLITERATION("rm"),
    SYNONYMS("ss"),
    TRANSLATION("t"),
    UNKNOWN_GT("gt");

    private final String value;

    TransParameter(String value) {
        this.value = value;
    }

    /**
     * Returns the parameter value of this enum constant.
     *
     * @return the parameter value.
     */
    public String value() {
        return value;
    }
}
