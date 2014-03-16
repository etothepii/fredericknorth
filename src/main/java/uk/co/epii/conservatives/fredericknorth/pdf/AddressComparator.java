package uk.co.epii.conservatives.fredericknorth.pdf;

import java.util.Comparator;

/**
 * User: James Robinson
 * Date: 16/03/2014
 * Time: 11:44
 */
public class AddressComparator implements Comparator<String> {
    @Override
    public int compare(String a, String b) {
        for (int i = 1; i <= Math.min(a.length(), b.length()); i++) {
            char A = a.charAt(a.length() - i);
            char B = b.charAt(b.length() - i);
            if (A != B) {
                return A - B;
            }
        }
        return a.length() - b.length();
    }
}
