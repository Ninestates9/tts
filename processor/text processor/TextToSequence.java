import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class TextToSequence {

    public TextToSequence() {
    }

    private static final Map<Character, Integer> symbolToId = new HashMap<>();
    static {
        symbolToId.put('_', 0);
        symbolToId.put(',', 1);
        symbolToId.put('.', 2);
        symbolToId.put('!', 3);
        symbolToId.put('?', 4);
        symbolToId.put('-', 5);
        symbolToId.put('~', 6);
        symbolToId.put('…', 7);
        symbolToId.put('N', 8);
        symbolToId.put('Q', 9);
        symbolToId.put('a', 10);
        symbolToId.put('b', 11);
        symbolToId.put('d', 12);
        symbolToId.put('e', 13);
        symbolToId.put('f', 14);
        symbolToId.put('g', 15);
        symbolToId.put('h', 16);
        symbolToId.put('i', 17);
        symbolToId.put('j', 18);
        symbolToId.put('k', 19);
        symbolToId.put('l', 20);
        symbolToId.put('m', 21);
        symbolToId.put('n', 22);
        symbolToId.put('o', 23);
        symbolToId.put('p', 24);
        symbolToId.put('s', 25);
        symbolToId.put('t', 26);
        symbolToId.put('u', 27);
        symbolToId.put('v', 28);
        symbolToId.put('w', 29);
        symbolToId.put('x', 30);
        symbolToId.put('y', 31);
        symbolToId.put('z', 32);
        symbolToId.put('ɑ', 33);
        symbolToId.put('æ', 34);
        symbolToId.put('ʃ', 35);
        symbolToId.put('ʑ', 36);
        symbolToId.put('ç', 37);
        symbolToId.put('ɯ', 38);
        symbolToId.put('ɪ', 39);
        symbolToId.put('ɔ', 40);
        symbolToId.put('ɛ', 41);
        symbolToId.put('ɹ', 42);
        symbolToId.put('ð', 43);
        symbolToId.put('ə', 44);
        symbolToId.put('ɫ', 45);
        symbolToId.put('ɥ', 46);
        symbolToId.put('ɸ', 47);
        symbolToId.put('ʊ', 48);
        symbolToId.put('ɾ', 49);
        symbolToId.put('ʒ', 50);
        symbolToId.put('θ', 51);
        symbolToId.put('β', 52);
        symbolToId.put('ŋ', 53);
        symbolToId.put('ɦ', 54);
        symbolToId.put('⁼', 55);
        symbolToId.put('ʰ', 56);
        symbolToId.put('`', 57);
        symbolToId.put('^', 58);
        symbolToId.put('#', 59);
        symbolToId.put('*', 60);
        symbolToId.put('=', 61);
        symbolToId.put('ˈ', 62);
        symbolToId.put('ˌ', 63);
        symbolToId.put('→', 64);
        symbolToId.put('↓', 65);
        symbolToId.put('↑', 66);
        symbolToId.put(' ', 67);
    }

    public static String cleantext(String text) {
        var cleantext = new CleanText();
        text = cleantext.clean(text);

        return text;

    }

    public List<Long> toSequence(String text) {

        List<Long> sequence = new ArrayList<>();

        text = cleantext(text);

        for (char symbol : text.toCharArray()) {
            if (!symbolToId.containsKey(symbol)) {
                continue;
            }
            int symbolId = symbolToId.get(symbol);
            sequence.add((long) symbolId);
        }

        return sequence;

    }
}
