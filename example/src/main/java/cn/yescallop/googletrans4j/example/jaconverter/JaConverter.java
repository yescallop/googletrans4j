package cn.yescallop.googletrans4j.example.jaconverter;

import cn.yescallop.googletrans4j.TransClient;
import cn.yescallop.googletrans4j.TransParameter;
import cn.yescallop.googletrans4j.TransRequest;
import cn.yescallop.googletrans4j.TransResponse;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Scallop Ye
 */
public final class JaConverter {

    private static final Pattern PATTERN_SYLLABLE =
            Pattern.compile("n(?![aiueoāīūēō])|[wrtypsdfghjkzcvbnm~]*[aiueoāīūēō]|\\b \\b| ?\\u0304");

    private static final int HIRAGANA_START = 0x3041;
    private static final int HIRAGANA_END = 0x3094;
    private static final int KATAKANA_START = 0x30A1;
    private static final int KATAKANA_END = 0x30F4;
    private static final int HIRA_KATA_OFFSET = KATAKANA_START - HIRAGANA_START;
    private static final String[] ROMAJI = {
            "~a", "a", "~i", "i", "~u", "u", "~e", "e", "~o", "o",
            "ka", "ga", "ki", "gi", "ku", "gu", "ke", "ge", "ko", "go",
            "sa", "za", "shi", "ji", "su", "zu", "se", "ze", "so", "zo",
            "ta", "da", "chi", "dji", "~tsu", "tsu", "dzu", "te", "de", "to", "do",
            "na", "ni", "nu", "ne", "no",
            "ha", "ba", "pa", "hi", "bi", "pi", "fu", "bu", "pu", "he", "be", "pe", "ho", "bo", "po",
            "ma", "mi", "mu", "me", "mo",
            "~ya", "ya", "~yu", "yu", "~yo", "yo",
            "ra", "ri", "ru", "re", "ro",
            "~wa", "wa", "wi", "we", "wo",
            "n", "vu"
    };
    private static final int VA_LINE_START = 0x30F7;
    private static final int VA_LINE_END = 0x30FA;
    private static final String[] VA_LINE = {"va", "vi", "ve", "vo"};
    private static final Map<String, Character> ROMAJI_TO_KANA = new HashMap<>(ROMAJI.length + VA_LINE.length);

    static {
        for (int i = 0; i < ROMAJI.length; i++) {
            ROMAJI_TO_KANA.put(ROMAJI[i], (char) (HIRAGANA_START + i));
        }

        for (int i = 0; i < VA_LINE.length; i++) {
            ROMAJI_TO_KANA.put(VA_LINE[i], (char) (VA_LINE_START + i));
        }
    }

    private final TransClient client;

    private JaConverter(TransClient client) {
        this.client = Objects.requireNonNull(client);
    }

    public static JaConverter create(TransClient client) {
        return new JaConverter(client);
    }

    public Stream<Line> convert(String text) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        return convert(br.lines());
    }

    public Stream<Line> convert(Stream<String> stream) {
        return stream.map(s -> s.isBlank() ? null : convertLine(s));
    }

    private Line convertLine(String line) {
        Line res = new Line();
        res.raw = line;
        int len = line.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int c = line.codePointAt(i);
            if (c == '　' || c == ' ') {
                c = '/';
            }
            sb.appendCodePoint(c);
        }
        line = sb.toString();
        TransRequest req = TransRequest.newBuilder(line)
                .parameters(TransParameter.TRANSLITERATION)
                .sourceLang("ja")
                .build();
        TransResponse resp;
        try {
            resp = client.send(req);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (resp.sourceTransliteration().isEmpty())
            return res;
        res.transliteration = resp.sourceTransliteration()
                .get().toLowerCase();
        try {
            res.kana = romajiToKana(res.transliteration);
        } catch (Exception e) {
            res.exception = e;
            return res;
        }
        List<Integer> indexes = new ArrayList<>();
        res.regex = kanaToRegex(line, indexes);
        res.kanaNoted = noteKana(res.raw, res.kana, res.regex, indexes);
        return res;
    }

    private static String noteKana(String line, String kana, String regex, List<Integer> indexes) {
        int size = indexes.size();
        if (size == 0)
            return line;
        Matcher m = Pattern.compile(regex).matcher(kana);
        if (!m.matches())
            return null;
        StringBuilder sb = new StringBuilder();
        int last = 0;
        for (int i = 0; i < size; i++) {
            int cur = indexes.get(i);
            String res = m.group(i + 1);
            res = res.trim();
            sb.append(line, last, cur);
            if (!res.isEmpty()) {
                if (res.indexOf('/') >= 0)
                    res = "ERROR";
                sb.append('(').append(res).append(')');
            }
            last = cur;
        }
        sb.append(line, last, line.length());
        return sb.toString();
    }

    private static String romajiToKana(String romaji) {
        StringBuilder sb = new StringBuilder();
        Matcher m = PATTERN_SYLLABLE.matcher(romaji);
        int lastEnd = 0;
        while (m.find()) {
            int diff = m.start() - lastEnd;
            char c = romaji.charAt(lastEnd);
            if (diff == 1 && c == ' ') {
                sb.append(' ');
            } else if (diff != 0 &&
                    !(lastEnd != 0 && diff == 1 && (c == '\'' || c == '-'))) {
                sb.append('/');
            }
            lastEnd = m.end();
            String syllable = m.group();
            int len = syllable.length();
            if (syllable.equals(" ")) {
                sb.append(' ');
                continue;
            } else if (syllable.charAt(len - 1) == '\u0304') {
                sb.append('ー');
                continue;
            }
            if (len != 1) {
                char c0 = syllable.charAt(0);
                char c1 = syllable.charAt(1);
                if (c0 == c1 || (c0 == 't' && c1 == 'c')) {
                    syllable = syllable.substring(1);
                    len--;
                    sb.append('っ');
                }
            }
            int last = syllable.charAt(len - 1);
            int i = "āīūēō".indexOf(last);
            if (i >= 0) {
                syllable = syllable.substring(0, len - 1) + "aiueo".charAt(i);
                romajiSyllableToKana(syllable, sb);
                sb.append("あいうえう".charAt(i));
            } else {
                romajiSyllableToKana(syllable, sb);
            }
        }
        if (lastEnd != romaji.length())
            sb.append('/');
        return sb.toString();
    }

    private static final Pattern YOUON_1 = Pattern.compile("[kgnhbpmr]y[aueo]");
    private static final Pattern YOUON_2 = Pattern.compile("(?:sh|j|ch|dj)[aueo]");
    private static final Pattern YOUON_3 = Pattern.compile("f[aieo]");

    private static void romajiSyllableToKana(String s, StringBuilder sb) {
        Character kana = ROMAJI_TO_KANA.get(s);
        if (kana == null) {
            if (YOUON_1.matcher(s).matches()) {
                sb.append(ROMAJI_TO_KANA.get(s.charAt(0) + "i"));
                char c = s.charAt(2);
                sb.append(c == 'e' ? 'ぇ' : ROMAJI_TO_KANA.get("~y" + c));
            } else if (YOUON_2.matcher(s).matches()) {
                sb.append(ROMAJI_TO_KANA.get(s.substring(0, s.length() - 1) + 'i'));
                char c = s.charAt(s.length() - 1);
                sb.append(c == 'e' ? 'ぇ' : ROMAJI_TO_KANA.get("~y" + c));
            } else if (YOUON_3.matcher(s).matches()) {
                sb.append('ふ');
                sb.append(ROMAJI_TO_KANA.get("~" + s.charAt(1)));
            } else {
                throw new IllegalArgumentException("No such kana: " + s);
            }
        } else {
            sb.append(kana);
        }
    }

    private static final String PSYCHO_IDEOGRAPHIC = "々〇・";

    private static String kanaToRegex(String s, List<Integer> indexes) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        int last = 0;
        int flag = -1;
        for (int i = 0; i < len; i++) {
            int c = s.codePointAt(i);
            boolean vaLine = inVaLine(c);
            boolean kata = isKatakana(c) || vaLine;
            boolean hira = isHiragana(c);
            if (!kata && !hira && c != 'ー') {
                if (PSYCHO_IDEOGRAPHIC.indexOf(c) < 0 && !Character.isIdeographic(c)) {
                    if (flag == 0) indexes.add(i);
                    if (flag != 1) {
                        sb.append("/ ?");
                        flag = 1;
                    }
                } else if (flag != 0) {
                    sb.append("( .+?|.+? |.+?)");
                    flag = 0;
                }
            } else {
                if (flag == 0) indexes.add(i);
                if (kata) {
                    switch (c) {
                        case 'オ':
                            sb.append("[おう]");
                            break;
                        case 'ァ':
                        case 'ィ':
                        case 'ゥ':
                        case 'ェ':
                        case 'ォ':
                            int ck = c - HIRA_KATA_OFFSET;
                            sb.append('[')
                                    .appendCodePoint(ck)
                                    .appendCodePoint(ck + 1)
                                    .append(']');
                            break;
                        default:
                            sb.appendCodePoint(vaLine ? c : c - HIRA_KATA_OFFSET);
                    }
                } else if (c == 'ー') {
                    if (isKatakana(last)) {
                        String r = ROMAJI[last - KATAKANA_START];
                        int idx = "aiueo".indexOf(r.charAt(r.length() - 1));
                        sb.append("あいうえう".charAt(idx));
                    } else if (inVaLine(last)) {
                        sb.append("あいえう".charAt(last - VA_LINE_START));
                    } else {
                        sb.append('ー');
                    }
                } else {
                    switch (c) {
                        case 'お':
                            sb.append("[おう]");
                            break;
                        case 'は':
                            sb.append("[はわ]");
                            break;
                        case 'へ':
                            sb.append("[へえ]");
                            break;
                        case 'を':
                            sb.append("[をお]");
                            break;
                        case 'ぁ':
                        case 'ぃ':
                        case 'ぅ':
                        case 'ぇ':
                        case 'ぉ':
                            sb.append('[')
                                    .appendCodePoint(c)
                                    .appendCodePoint(c + 1)
                                    .append(']');
                            break;
                        default:
                            sb.appendCodePoint(c);
                    }
                }
                last = c;
                flag = -1;
                sb.append(" ?");
            }
        }
        if (flag == 0) indexes.add(len);
        return sb.toString();
    }

    private static boolean isHiragana(int c) {
        return c >= HIRAGANA_START && c <= HIRAGANA_END;
    }

    private static boolean isKatakana(int c) {
        return c >= KATAKANA_START && c <= KATAKANA_END;
    }

    private static boolean inVaLine(int c) {
        return c >= VA_LINE_START && c <= VA_LINE_END;
    }

    public static class Line {

        private String raw;
        private String transliteration;
        private String kana;
        private String regex;
        private String kanaNoted;

        private Exception exception;

        private Line() {
        }

        public String raw() {
            return raw;
        }

        public String transliteration() {
            return transliteration;
        }

        public String kana() {
            return kana;
        }

        public String regex() {
            return regex;
        }

        public String kanaNoted() {
            return kanaNoted;
        }

        public Optional<Exception> exception() {
            return Optional.ofNullable(exception);
        }
    }
}
