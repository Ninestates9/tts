import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

public class ChineseToIpa {

    public ChineseToIpa() {
    }

    private static final Map<Character, Character> numberMap = new HashMap<>();

    static {
        numberMap.put('0', '零');
        numberMap.put('1', '一');
        numberMap.put('2', '二');
        numberMap.put('3', '三');
        numberMap.put('4', '四');
        numberMap.put('5', '五');
        numberMap.put('6', '六');
        numberMap.put('7', '七');
        numberMap.put('8', '八');
        numberMap.put('9', '九');
    }

    private static final Map<Integer, Character> positionMap = new HashMap<>();

    static {
        positionMap.put(1, '\0');
        positionMap.put(2, '十');
        positionMap.put(3, '百');
        positionMap.put(4, '千');
        positionMap.put(5, '万');
        positionMap.put(6, '十');
        positionMap.put(7, '百');
        positionMap.put(8, '千');
        positionMap.put(9, '亿');
    }

    static Map<Character, String> latin_bopomofo = new HashMap<>();

    static {
        latin_bopomofo.put('a', "ㄟˉ");
        latin_bopomofo.put('b', "ㄅㄧˋ");
        latin_bopomofo.put('c', "ㄙㄧˉ");
        latin_bopomofo.put('d', "ㄉㄧˋ");
        latin_bopomofo.put('e', "ㄧˋ");
        latin_bopomofo.put('f', "ㄝˊㄈㄨˋ");
        latin_bopomofo.put('g', "ㄐㄧˋ");
        latin_bopomofo.put('h', "ㄝˇㄑㄩˋ");
        latin_bopomofo.put('i', "ㄞˋ");
        latin_bopomofo.put('j', "ㄐㄟˋ");
        latin_bopomofo.put('k', "ㄎㄟˋ");
        latin_bopomofo.put('l', "ㄝˊㄛˋ");
        latin_bopomofo.put('m', "ㄝˊㄇㄨˋ");
        latin_bopomofo.put('n', "ㄣˉ");
        latin_bopomofo.put('o', "ㄡˉ");
        latin_bopomofo.put('p', "ㄆㄧˉ");
        latin_bopomofo.put('q', "ㄎㄧㄡˉ");
        latin_bopomofo.put('r', "ㄚˋ");
        latin_bopomofo.put('s', "ㄝˊㄙˋ");
        latin_bopomofo.put('t', "ㄊㄧˋ");
        latin_bopomofo.put('u', "ㄧㄡˉ");
        latin_bopomofo.put('v', "ㄨㄧˉ");
        latin_bopomofo.put('w', "ㄉㄚˋㄅㄨˋㄌㄧㄡˋ");
        latin_bopomofo.put('x', "ㄝˉㄎㄨˋㄙˋ");
        latin_bopomofo.put('y', "ㄨㄞˋ");
        latin_bopomofo.put('z', "ㄗㄟˋ");
    }

    static Map<String, String> bopomofo_zhuyin = new HashMap<>();

    static {
        bopomofo_zhuyin.put("b", "ㄅ");
        bopomofo_zhuyin.put("p", "ㄆ");
        bopomofo_zhuyin.put("m", "ㄇ");
        bopomofo_zhuyin.put("f", "ㄈ");
        bopomofo_zhuyin.put("d", "ㄉ");
        bopomofo_zhuyin.put("t", "ㄊ");
        bopomofo_zhuyin.put("n", "ㄋ");
        bopomofo_zhuyin.put("l", "ㄌ");
        bopomofo_zhuyin.put("g", "ㄍ");
        bopomofo_zhuyin.put("k", "ㄎ");
        bopomofo_zhuyin.put("h", "ㄏ");
        bopomofo_zhuyin.put("j", "ㄐ");
        bopomofo_zhuyin.put("q", "ㄑ");
        bopomofo_zhuyin.put("x", "ㄒ");
        bopomofo_zhuyin.put("zh", "ㄓ");
        bopomofo_zhuyin.put("zhi", "ㄓ");
        bopomofo_zhuyin.put("ch", "ㄔ");
        bopomofo_zhuyin.put("chi", "ㄔ");
        bopomofo_zhuyin.put("sh", "ㄕ");
        bopomofo_zhuyin.put("shi", "ㄕ");
        bopomofo_zhuyin.put("r", "ㄖ");
        bopomofo_zhuyin.put("ri", "ㄖ");
        bopomofo_zhuyin.put("z", "ㄗ");
        bopomofo_zhuyin.put("zi", "ㄗ");
        bopomofo_zhuyin.put("c", "ㄘ");
        bopomofo_zhuyin.put("ci", "ㄘ");
        bopomofo_zhuyin.put("s", "ㄙ");
        bopomofo_zhuyin.put("si", "ㄙ");
        bopomofo_zhuyin.put("y", "ㄧ");
        bopomofo_zhuyin.put("w", "ㄨ");

        bopomofo_zhuyin.put("a", "ㄚ");
        bopomofo_zhuyin.put("o", "ㄛ");
        bopomofo_zhuyin.put("e", "ㄜ");
        bopomofo_zhuyin.put("ai", "ㄞ");
        bopomofo_zhuyin.put("ei", "ㄟ");
        bopomofo_zhuyin.put("ao", "ㄠ");
        bopomofo_zhuyin.put("ou", "ㄡ");
        bopomofo_zhuyin.put("an", "ㄢ");
        bopomofo_zhuyin.put("en", "ㄣ");
        bopomofo_zhuyin.put("ang", "ㄤ");
        bopomofo_zhuyin.put("eng", "ㄥ");
        bopomofo_zhuyin.put("er", "ㄦ");
        bopomofo_zhuyin.put("i", "ㄧ");
        bopomofo_zhuyin.put("yi", "ㄧ");
        bopomofo_zhuyin.put("ie", "ㄧㄝ");
        bopomofo_zhuyin.put("ye", "ㄧㄝ");
        bopomofo_zhuyin.put("iu", "ㄧㄡ");
        bopomofo_zhuyin.put("in", "ㄧㄣ");
        bopomofo_zhuyin.put("yin", "ㄧㄣ");
        bopomofo_zhuyin.put("ing", "ㄧㄥ");
        bopomofo_zhuyin.put("ying", "ㄧㄥ");
        bopomofo_zhuyin.put("u", "ㄨ");
        bopomofo_zhuyin.put("wu", "ㄨ");
        bopomofo_zhuyin.put("ui", "ㄨㄟ");
        bopomofo_zhuyin.put("un", "ㄨㄣ");
        bopomofo_zhuyin.put("ong", "ㄨㄥ");
        bopomofo_zhuyin.put("ü", "ㄩ");
        bopomofo_zhuyin.put("yu", "ㄩ");
        bopomofo_zhuyin.put("üe", "ㄩㄝ");
        bopomofo_zhuyin.put("yue", "ㄩㄝ");
        bopomofo_zhuyin.put("ün", "ㄩㄣ");
        bopomofo_zhuyin.put("yun", "ㄩㄣ");
        bopomofo_zhuyin.put("yuan", "ㄩㄢ");

        bopomofo_zhuyin.put("1", "ˉ");
        bopomofo_zhuyin.put("2", "ˊ");
        bopomofo_zhuyin.put("3", "ˇ");
        bopomofo_zhuyin.put("4", "ˋ");
        bopomofo_zhuyin.put("5", "˙");
    }

    static Map<String, String> zhuyin_ipa = new HashMap<>();

    static {
        zhuyin_ipa.put("ㄅㄛ", "p⁼wo");
        zhuyin_ipa.put("ㄆㄛ", "pʰwo");
        zhuyin_ipa.put("ㄇㄛ", "mwo");
        zhuyin_ipa.put("ㄈㄛ", "fwo");
        zhuyin_ipa.put("ㄅ", "p⁼");
        zhuyin_ipa.put("ㄆ", "pʰ");
        zhuyin_ipa.put("ㄇ", "m");
        zhuyin_ipa.put("ㄈ", "f");
        zhuyin_ipa.put("ㄉ", "t⁼");
        zhuyin_ipa.put("ㄊ", "tʰ");
        zhuyin_ipa.put("ㄋ", "n");
        zhuyin_ipa.put("ㄌ", "l");
        zhuyin_ipa.put("ㄍ", "k⁼");
        zhuyin_ipa.put("ㄎ", "kʰ");
        zhuyin_ipa.put("ㄏ", "x");
        zhuyin_ipa.put("ㄐ", "tʃ⁼");
        zhuyin_ipa.put("ㄑ", "tʃʰ");
        zhuyin_ipa.put("ㄒ", "ʃ");
        zhuyin_ipa.put("ㄓ", "ts`⁼");
        zhuyin_ipa.put("ㄔ", "ts`ʰ");
        zhuyin_ipa.put("ㄕ", "s`");
        zhuyin_ipa.put("ㄖ", "ɹ`");
        zhuyin_ipa.put("ㄗ", "ts⁼");
        zhuyin_ipa.put("ㄘ", "tsʰ");
        zhuyin_ipa.put("ㄙ", "s");
        zhuyin_ipa.put("ㄚ", "a");
        zhuyin_ipa.put("ㄛ", "o");
        zhuyin_ipa.put("ㄜ", "ə");
        zhuyin_ipa.put("ㄝ", "ɛ");
        zhuyin_ipa.put("ㄞ", "aɪ");
        zhuyin_ipa.put("ㄟ", "eɪ");
        zhuyin_ipa.put("ㄠ", "ɑʊ");
        zhuyin_ipa.put("ㄡ", "oʊ");
        zhuyin_ipa.put("ㄧㄢ", "jɛn");
        zhuyin_ipa.put("ㄩㄢ", "ɥæn");
        zhuyin_ipa.put("ㄢ", "an");
        zhuyin_ipa.put("ㄧㄣ", "in");
        zhuyin_ipa.put("ㄩㄣ", "ɥn");
        zhuyin_ipa.put("ㄣ", "ən");
        zhuyin_ipa.put("ㄤ", "ɑŋ");
        zhuyin_ipa.put("ㄧㄥ", "iŋ");
        zhuyin_ipa.put("ㄨㄥ", "ʊŋ");
        zhuyin_ipa.put("ㄩㄥ", "jʊŋ");
        zhuyin_ipa.put("ㄥ", "əŋ");
        zhuyin_ipa.put("ㄦ", "əɻ");
        zhuyin_ipa.put("ㄧ", "i");
        zhuyin_ipa.put("ㄨ", "u");
        zhuyin_ipa.put("ㄩ", "ɥ");
        zhuyin_ipa.put("ˉ", "→");
        zhuyin_ipa.put("ˊ", "↑");
        zhuyin_ipa.put("ˇ", "↓↑");
        zhuyin_ipa.put("ˋ", "↓");
        zhuyin_ipa.put("˙", "");
        zhuyin_ipa.put("，", ",");
        zhuyin_ipa.put("。", ".");
        zhuyin_ipa.put("！", "!");
        zhuyin_ipa.put("？", "?");
        zhuyin_ipa.put("—", "-");
    }

    public static String numberToChinese(String text) {

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(text);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String number = matcher.group();
            String chineseNumber = convertToChineseNumber(number);
            matcher.appendReplacement(result, chineseNumber);
        }
        matcher.appendTail(result);

        return result.toString();

    }

    public static String convertToChineseNumber(String input) {

        StringBuilder output = new StringBuilder();
        int length = input.length();

        for (int i = 0; i < length; i++) {
            char digit = input.charAt(i);
            int position = length - i;

            // 跳过0，但要处理中文中的零
            if (digit == '0') {
                if (output.length() > 0 && output.charAt(output.length() - 1) != '零') {
                    output.append(numberMap.get(digit));
                }
                continue;
            }

            // 添加数字
            output.append(numberMap.get(digit));

            // 添加位置
            if (position > 1 && positionMap.get(position) != null) {
                output.append(positionMap.get(position));
            }
        }

        // 处理零的特殊情况，如 "一百零一"
        String result = output.toString().replaceAll("零+", "零").replaceAll("零$", "");

        return result;

    }

    public static String latinToBopomofo(String text) {

        StringBuilder result = new StringBuilder();
        for (char letter : text.toCharArray()) {
            if (Character.isAlphabetic(letter)) {
                char lowerCaseLetter = Character.toLowerCase(letter);
                if (latin_bopomofo.containsKey(lowerCaseLetter)) {
                    result.append(latin_bopomofo.get(lowerCaseLetter));
                } else {
                    result.append(lowerCaseLetter);
                }
            } else {
                result.append(letter);
            }
        }

        return result.toString();

    }

    public static String chineseToBopomofo(String text) {

        text = text.replace('、', '，').replace('；', '，').replace('：', '，');

        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<SegToken> words = segmenter.process(text, JiebaSegmenter.SegMode.SEARCH);

        StringBuilder result = new StringBuilder();
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITH_TONE_NUMBER);
        format.setVCharType(HanyuPinyinVCharType.WITH_U_UNICODE);

        for (SegToken word : words) {
            String wordStr = word.word;
            if (!containsChineseCharacter(wordStr)) {
                result.append(wordStr);
                continue;
            }
            StringBuilder bopomofos = new StringBuilder();
            for (char ch : wordStr.toCharArray()) {
                if (containsChineseCharacter(String.valueOf(ch))) {
                    try {
                        String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                        if (pinyinArray != null) {
                            bopomofos.append(pinyinArray[0]);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    bopomofos.append(ch);
                }
            }
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(bopomofos);
        }

        return result.toString();

    }

    private static boolean containsChineseCharacter(String text) {

        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fff]");
        Matcher matcher = pattern.matcher(text);

        return matcher.find();

    }

    public static String pinyinToZhuyin(String text) {

        StringBuilder result = new StringBuilder();
        int index = 0;
        while (index < text.length()) {
            boolean found = false;
            for (int i = 4; i >= 1; i--) {
                if (index + i <= text.length()) {
                    String substr = text.substring(index, index + i);
                    if (bopomofo_zhuyin.containsKey(substr)) {
                        result.append(bopomofo_zhuyin.get(substr));
                        index += i;
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                result.append(text.charAt(index));
                index++;
            }
        }
        return result.toString();

    }

    public static String zhuyinToIpa(String text) {

        StringBuilder output = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (i + 2 <= text.length() && zhuyin_ipa.containsKey(text.substring(i, i + 2))) {
                output.append(zhuyin_ipa.get(text.substring(i, i + 2)));
                i += 2;
            } else if (zhuyin_ipa.containsKey(text.substring(i, i + 1))) {
                output.append(zhuyin_ipa.get(text.substring(i, i + 1)));
                i++;
            } else {
                output.append(text.charAt(i));
                i++;
            }
        }
        return output.toString();

    }

    public static String replacePatterns(String text) {

        text = text.replaceAll("i([aoe])", "j$1");

        text = text.replaceAll("u([aoəe])", "w$1");

        Pattern pattern1 = Pattern.compile("([sɹ]`[⁼ʰ]?)([→↓↑ ]+|$)");
        Matcher matcher1 = pattern1.matcher(text);
        StringBuffer sb1 = new StringBuffer();
        while (matcher1.find()) {
            matcher1.appendReplacement(sb1, matcher1.group(1) + "ɹ`" + matcher1.group(2));
        }
        matcher1.appendTail(sb1);
        text = sb1.toString().replace("ɻ", "ɹ`");

        Pattern pattern2 = Pattern.compile("([s][⁼ʰ]?)([→↓↑ ]+|$)");
        Matcher matcher2 = pattern2.matcher(text);
        StringBuffer sb2 = new StringBuffer();
        while (matcher2.find()) {
            matcher2.appendReplacement(sb2, matcher2.group(1) + "ɹ" + matcher2.group(2));
        }
        matcher2.appendTail(sb2);
        text = sb2.toString();

        return text;

    }

    public String convert(String text) {

        text = numberToChinese(text);
        text = latinToBopomofo(text);
        text = chineseToBopomofo(text);
        text = pinyinToZhuyin(text);
        text = zhuyinToIpa(text);
        text = replacePatterns(text);

        return text;

    }
}
