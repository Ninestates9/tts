import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanText {

    public CleanText() {
    }
    public static String chineseToIpa(String text) {

        var chinesetoipa = new ChineseToIpa();
        text = chinesetoipa.convert(text);

        return text;

    }

    public static String textProcessor(String text) {

        text = text.replaceAll("\\s+$", "");

        Pattern pattern = Pattern.compile("([^.,!?\\-…~])$");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            text = matcher.replaceAll("$1.");
        }

        return text;

    }

    public String clean(String text) {
        text = chineseToIpa(text);
        text = textProcessor(text);

        return text;

    }
}
