package uk.co.epii.conservatives.fredericknorth.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * User: James Robinson
 * Date: 05/09/2013
 * Time: 23:41
 */
public class StringExtentions {

    public static String getAbbreviation(String string, int length) {
        String[] words = string.split("\\b");
        List<String> reversedWords = new ArrayList<String>();
        StringBuilder stringBuilder = new StringBuilder(length);
        for (String word : words) {
            if (word.length() == 0) {
                continue;
            }
            char firstChar = word.charAt(0);
            if (firstChar >= 'A' && firstChar <= 'Z') {
                reversedWords.add(0, word);
                stringBuilder.append(firstChar);
            }
            if (stringBuilder.length() == length) {
                break;
            }
        }
        while (stringBuilder.length() < length && !reversedWords.isEmpty()) {
            String word = reversedWords.remove(0);
            int end = Math.min(word.length(), length - stringBuilder.length() + 1);
            stringBuilder.insert(reversedWords.size() + 1, word.substring(1, end).toUpperCase());
        }
        return stringBuilder.toString();
    }

    public static String getCommonEnding(Collection<String> strings) {
        String common = null;
        for (String string : strings) {
            if (common == null) {
                common = string;
            }
            else {
                common = getCommonEnding(common, string);
            }
        }
        return common;
    }

    public static String getCommonEnding(String common, String string) {
        int i = 0;
        for (; i < string.length() && i < common.length(); i++) {
            if (string.charAt(string.length() - i - 1) != common.charAt(common.length() - i - 1)) {
                break;
            }
        }
        return common.substring(common.length() - i);
    }
}
