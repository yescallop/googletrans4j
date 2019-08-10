package cn.yescallop.googletrans4j;

/**
 * Declares the possible values for the query parameter "dt"
 * in an HTTP request for Google Translate.
 *
 * @author Scallop Ye
 */
public enum TransParameter {
    ALTERNATE_TRANSLATIONS("at"),
    ALL_TRANSLATIONS("bd"),
    EXAMPLES("ex"),
    LANG_DETECTION("ld"),
    DEFINITIONS("md"),
    SPELLING_CORRECTION("qca"),
    SEE_ALSO("rw"),
    TRANSLITERATION("rm"),
    SYNONYMS("ss"),
    TRANSLATION("t");

    private final String value;

    TransParameter(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
