package cn.yescallop.googletrans4j.util;

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
public final class JapaneseConverter {

    private static final Pattern PATTERN_SYLLABLE =
            Pattern.compile("(?:n(?![aiueoāīūēō])|[wrtypsdfghjkzcvbnm~]*[aiueoāīūēō]|[ -])");

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

    private JapaneseConverter(TransClient client) {
        this.client = Objects.requireNonNull(client);
    }

    public static JapaneseConverter create(TransClient client) {
        return new JapaneseConverter(client);
    }

    public Stream<Line> convert(String text) {
        BufferedReader br = new BufferedReader(new StringReader(text));
        return convert(br.lines());
    }

    public Stream<Line> convert(Stream<String> stream) {
        return stream.filter(s -> !s.isEmpty())
                .map(this::convertLine);
    }

    private Line convertLine(String line) {
        TransRequest req = TransRequest.newBuilder(line)
                .parameters(TransParameter.TRANSLITERATION)
                .sourceLang("ja")
                .targetLang("ja")
                .build();
        TransResponse resp;
        try {
            resp = client.send(req);
        } catch (Exception e) {
            return null;
        }
        Line res = new Line();
        res.raw = line;
        res.transliteration = resp.sourceTransliteration()
                .map(String::toLowerCase)
                .get();
        res.kana = romajiToKana(res.transliteration);
        List<Integer> indexes = new ArrayList<>();
        res.regex = kanaToRegex(line, indexes);
        res.kanaNoted = noteKana(line, res.kana, res.regex, indexes);
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
            if (!res.isBlank()) {
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
        while (m.find()) {
            String syllable = m.group();
            char c0 = syllable.charAt(0);
            if (c0 == ' ' || c0 == '-') {
                sb.append(' ');
                continue;
            }
            if (syllable.length() != 1) {
                char c1 = syllable.charAt(1);
                if (c0 == c1 || (c0 == 't' && c1 == 'c')) {
                    syllable = syllable.substring(1);
                    sb.append('っ');
                }
            }
            if (syllable.length() > 3) {
                throw new IllegalArgumentException("syllable length > 3: " + syllable);
            }
            int last = syllable.charAt(syllable.length() - 1);
            int i = "āīūēō".indexOf(last);
            if (i >= 0) {
                syllable = syllable.substring(0, syllable.length() - 1) + "aiueo".charAt(i);
                romajiSyllableToKana(syllable, sb);
                sb.append("あいうえう".charAt(i));
            } else {
                romajiSyllableToKana(syllable, sb);
            }
        }
        return sb.toString();
    }

    private static final Pattern YOUON_1 = Pattern.compile("[kgnhbpmr]y[auo]");
    private static final Pattern YOUON_2 = Pattern.compile("(?:sh|j|ch|dj)[auo]");

    private static void romajiSyllableToKana(String s, StringBuilder sb) {
        Character kana = ROMAJI_TO_KANA.get(s);
        if (kana == null) {
            if (YOUON_1.matcher(s).matches()) {
                sb.append(ROMAJI_TO_KANA.get(s.charAt(0) + "i"));
                sb.append(ROMAJI_TO_KANA.get("~y" + s.charAt(2)));
            } else if (YOUON_2.matcher(s).matches()) {
                sb.append(ROMAJI_TO_KANA.get(s.substring(0, s.length() - 1) + 'i'));
                sb.append(ROMAJI_TO_KANA.get("~y" + s.charAt(s.length() - 1)));
            } else {
                throw new IllegalArgumentException("No such kana: " + s);
            }
        } else {
            sb.append(kana);
        }
    }

    private static String kanaToRegex(String s, List<Integer> indexes) {
        StringBuilder sb = new StringBuilder();
        int len = s.length();
        int last = 0;
        boolean match = false;
        for (int i = 0; i < len; i++) {
            int c = s.codePointAt(i);
            boolean kata = isKatakana(c);
            boolean hira = isHiragana(c);
            if (!kata && !hira && c != 'ー') {
                if (!Character.isIdeographic(c)) {
                    if (match) indexes.add(i);
                    match = false;
                } else if (!match) {
                    sb.append("( .*?|.*? |.*?)");
                    match = true;
                }
            } else {
                if (match) indexes.add(i);
                if (kata) {
                    last = c;
                    sb.appendCodePoint(c - HIRA_KATA_OFFSET);
                    sb.append(" ?");
                    match = false;
                    continue;
                } else if (c == 'ー' && last != 0) {
                    String r = ROMAJI[last - KATAKANA_START];
                    int idx = "aiueo".indexOf(r.charAt(r.length() - 1));
                    sb.append("あいうえう".charAt(idx));
                    match = false;
                } else {
                    switch (c) {
                        case 'は':
                            c = 'わ';
                            break;
                        case 'へ':
                            c = 'え';
                            break;
                        case 'を':
                            c = 'お';
                            break;
                    }
                    sb.appendCodePoint(c);
                    match = false;
                }
                sb.append(" ?");
            }
            last = 0;
        }
        if (match) indexes.add(len);
        return sb.toString();
    }

    private static boolean isHiragana(int c) {
        return c >= HIRAGANA_START && c <= HIRAGANA_END;
    }

    private static boolean isKatakana(int c) {
        return c >= KATAKANA_START && c <= KATAKANA_END;
    }

    public static class Line {

        private String raw;
        private String transliteration;
        private String kana;
        private String regex;
        private String kanaNoted;

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

        @Override
        public String toString() {
            return "Line{" +
                    "raw='" + raw + '\'' +
                    ", transliteration='" + transliteration + '\'' +
                    ", kana='" + kana + '\'' +
                    ", regex='" + regex + '\'' +
                    '}';
        }
    }
}
