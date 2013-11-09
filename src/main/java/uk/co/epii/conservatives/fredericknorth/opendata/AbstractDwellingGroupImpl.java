package uk.co.epii.conservatives.fredericknorth.opendata;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 07/11/2013
 * Time: 00:13
 */
public abstract class AbstractDwellingGroupImpl implements DwellingGroup {

    protected String commonName;
    private String name;
    private NumericIdentifierSummary numericIdentifierSummary;
    private NonNumericIdentifierSummary nonNumericsIdentifierSummary;

    public AbstractDwellingGroupImpl(String commonName) {
        this(commonName, null);
    }

    public AbstractDwellingGroupImpl(String commonName, String name) {
        this.commonName = commonName;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        if (name != null) {
            return name;
        }
        String identifierSummary;
        if (size() <= 1 || (identifierSummary = establishIdentifierSummary()).length() == 0) {
            return commonName;
        }
        return String.format("%s %s", identifierSummary, commonName);
    }

    private String establishIdentifierSummary() {
        if (size() == 0) {
            throw new RuntimeException("Impossible to get summary from no houses");
        }
        if (size() == 1) {
            return getDwellings().iterator().next().getName();
        }
        filterIdentifierSummaries();
        if (nonNumericsIdentifierSummary.isEmpty()) {
            return getNumericIdentifierSummary(numericIdentifierSummary, " & ");
        }
        if (nonNumericsIdentifierSummary.unmatched.size() + nonNumericsIdentifierSummary.matchedFlats.size() <= 5) {
            StringBuilder stringBuilder = new StringBuilder(getNumericIdentifierSummary(numericIdentifierSummary, ", "));
            List<String> nonNumericGroupings = nonNumericsIdentifierSummary.getGroupings();
            for (int i = 0; i < nonNumericGroupings.size(); i++) {
                if (stringBuilder.length() > 0) {
                    if (i == nonNumericGroupings.size() - 1) {
                        stringBuilder.append(" & ");
                    } else {
                        stringBuilder.append(", ");
                    }
                }
                stringBuilder.append(nonNumericGroupings.get(i));
            }
            return stringBuilder.toString();
        }
        return "";
    }



    private String getNumericIdentifierSummary(
            NumericIdentifierSummary numericIdentifierSummary, String joiningOddsAndEvens) {
        if (numericIdentifierSummary.all.isEmpty()) {
            return "";
        }
        String evenAndOdd = getNumericIdentifierOddEvenSummary(numericIdentifierSummary, joiningOddsAndEvens);
        String all = summarize(getGroupings(numericIdentifierSummary.all, 1), "").toString();
        return evenAndOdd.length() < all.length() ? evenAndOdd : all;
    }

    private String getNumericIdentifierOddEvenSummary(
            NumericIdentifierSummary numericIdentifierSummary, String joiningOddsAndEvens) {
        StringBuilder stringBuilder = new StringBuilder();
        if (numericIdentifierSummary.all.isEmpty()) {
            return "";
        }
        else if (numericIdentifierSummary.evens.isEmpty()) {
            stringBuilder.append(summarize(getGroupings(numericIdentifierSummary.odds, 2, ""), " ODDS ONLY"));
        }
        else if (numericIdentifierSummary.odds.isEmpty()) {
            stringBuilder.append(summarize(getGroupings(numericIdentifierSummary.evens, 2, ""), " EVENS ONLY"));
        }
        else {
            List<List<Integer>> evensGroupings = getGroupings(numericIdentifierSummary.evens, 2);
            List<List<Integer>> oddsGroupings = getGroupings(numericIdentifierSummary.odds, 2);
            if (numericIdentifierSummary.evens.get(0) < numericIdentifierSummary.odds.get(0)) {
                stringBuilder.append(summarize(evensGroupings, " EVENS ONLY"));
                stringBuilder.append(joiningOddsAndEvens);
                stringBuilder.append(summarize(oddsGroupings, " ODDS ONLY"));
            }
            else {
                stringBuilder.append(summarize(oddsGroupings, " ODDS ONLY"));
                stringBuilder.append(joiningOddsAndEvens);
                stringBuilder.append(summarize(evensGroupings, " EVENS ONLY"));
            }
        }
        return stringBuilder.toString();
    }

    private List<List<Integer>> getGroupings(List<Integer> numbers, int delta) {
        return getGroupings(numbers, delta, "");
    }

    private List<List<Integer>> getGroupings(List<Integer> numbers, int delta, String suffix) {
        List<List<Integer>> groupings = new ArrayList<List<Integer>>();
        List<Integer> group = new ArrayList<Integer>();
        for (int number : numbers) {
            if (!group.isEmpty() && group.get(group.size() - 1) + delta != number) {
                if (group.size() == 2) {
                    groupings.add(Arrays.asList(group.get(0)));
                    groupings.add(Arrays.asList(group.get(1)));
                }
                else {
                    groupings.add(group);
                }
                group = new ArrayList<Integer>();
            }
            group.add(number);
        }

        if (group.size() == 2) {
            groupings.add(Arrays.asList(group.get(0)));
            groupings.add(Arrays.asList(group.get(1)));
        }
        else {
            groupings.add(group);
        }
        return groupings;
    }

    private StringBuilder summarize(List<List<Integer>> groupings, String suffix) {
        StringBuilder stringBuilder = new StringBuilder(groupings.size() * 16);
        stringBuilder.append(getString(groupings.get(0)));
        for (int i = 1; i < groupings.size() - 1; i++) {
            stringBuilder.append(", ");
            stringBuilder.append(getString(groupings.get(i)));
        }
        if (groupings.size() > 1) {
            stringBuilder.append(" & ");
            stringBuilder.append(getString(groupings.get(groupings.size() - 1)));
        }
        stringBuilder.append(suffix);
        return stringBuilder;
    }

    private String getString(List<Integer> integers) {
        if (integers.size() == 1) {
            return integers.get(0) + "";
        }
        else if (integers.size() == 2) {
            return integers.get(0) + ", " + integers.get(1);
        }
        else {
            return integers.get(0) + " - " + integers.get(integers.size() - 1);
        }
    }

    public void filterIdentifierSummaries() {
        numericIdentifierSummary = new NumericIdentifierSummary();
        nonNumericsIdentifierSummary = new NonNumericIdentifierSummary();
        for (Dwelling dwelling : getDwellings()) {
            try {
                int number = Integer.parseInt(dwelling.getName());
                numericIdentifierSummary.all.add(number);
                if (number % 2 == 0) {
                    numericIdentifierSummary.evens.add(number);
                }
                else {
                    numericIdentifierSummary.odds.add(number);
                }
            }
            catch (NumberFormatException nfe) {
                nonNumericsIdentifierSummary.add(dwelling.getName());
            }
        }
        numericIdentifierSummary.sort();
        nonNumericsIdentifierSummary.sort();
    }

    private class NonNumericIdentifierSummary {
        final List<String> unmatched = new ArrayList<String>(size());
        final HashMap<PrefixAndSuffix, NumericIdentifierSummary> matchedFlats =
                new HashMap<PrefixAndSuffix, NumericIdentifierSummary>();
        private final Pattern flatMidMatcher;
        private final Pattern flatPreMatcher;
        private final Pattern flatPostMatcher;
        private int count = 0;
        private List<String> groupings;

        public NonNumericIdentifierSummary() {
            flatMidMatcher = Pattern.compile("(.* )([0-9][0-9]*)( .*)");
            flatPreMatcher = Pattern.compile("(^)([0-9][0-9]*)( .*)");
            flatPostMatcher = Pattern.compile("(.* )([0-9][0-9]*)($)");
        }

        public void add(String identifier) {
            count++;
            Matcher midMatcher = flatMidMatcher.matcher(identifier);
            Matcher preMatcher = flatPreMatcher.matcher(identifier);
            Matcher postMatcher = flatPostMatcher.matcher(identifier);
            if (midMatcher.matches()) {
                add(midMatcher);
            }
            else if (preMatcher.matches()) {
                add(preMatcher);
            }
            else if (postMatcher.matches()) {
                add(postMatcher);
            }
            else {
                unmatched.add(identifier);
            }
        }

        private void add(Matcher matcher) {
            PrefixAndSuffix prefixAndSuffix = new PrefixAndSuffix(matcher.group(1), matcher.group(3));
            if (!matchedFlats.containsKey(prefixAndSuffix)) {
                matchedFlats.put(prefixAndSuffix, new NumericIdentifierSummary());
            }
            NumericIdentifierSummary numericIdentifierSummary = matchedFlats.get(prefixAndSuffix);
            int number = Integer.parseInt(matcher.group(2));
            if (number % 2 == 0) {
                numericIdentifierSummary.evens.add(number);
            }
            else {
                numericIdentifierSummary.odds.add(number);
            }
            numericIdentifierSummary.all.add(number);

        }

        public int size() {
            return count;
        }

        public void sort() {
            Collections.sort(unmatched);
            for (NumericIdentifierSummary numericIdentifierSummary : matchedFlats.values()) {
                numericIdentifierSummary.sort();
            }
        }

        public boolean isEmpty() {
            return count == 0;
        }

        public List<String> getGroupings() {
            if (groupings != null) return groupings;
            groupings = new ArrayList<String>(unmatched.size() + matchedFlats.size());
            groupings.addAll(unmatched);
            for (Map.Entry<PrefixAndSuffix, NumericIdentifierSummary> entry : matchedFlats.entrySet()) {
                StringBuilder stringBuilder = new StringBuilder(64);
                if (entry.getKey().prefix.length() > 0) {
                    stringBuilder.append(entry.getKey().prefix.substring(0, entry.getKey().prefix.length() - 1)) ;
                    stringBuilder.append("S ");
                }
                stringBuilder.append(getNumericIdentifierSummary(entry.getValue(), " & "));
                stringBuilder.append(entry.getKey().suffix);
                groupings.add(stringBuilder.toString());
            }
            return groupings;
        }
    }

    private class PrefixAndSuffix {
        final String prefix;
        final String suffix;

        private PrefixAndSuffix(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrefixAndSuffix that = (PrefixAndSuffix) o;

            if (!prefix.equals(that.prefix)) return false;
            if (!suffix.equals(that.suffix)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = prefix.hashCode();
            result = 31 * result + suffix.hashCode();
            return result;
        }
    }

    private class NumericIdentifierSummary {
        final List<Integer> odds = new ArrayList<Integer>(size());
        final List<Integer> evens = new ArrayList<Integer>(size());
        final List<Integer> all = new ArrayList<Integer>(size());

        public void sort() {
            Collections.sort(odds);
            Collections.sort(evens);
            Collections.sort(all);
        }
    }

}
