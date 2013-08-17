package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 21/06/13
 * Time: 00:51
 */
class DwellingGroupImpl implements DwellingGroup {

    private final String name;
    private String displayName;
    private String uniquePart;
    private PostcodeDatum postcode;
    private final List<Dwelling> dwellings;
    private NumericIdentifierSummary numericIdentifierSummary;
    private NonNumericIdentifierSummary nonNumericsIdentifierSummary;
    private Point point;
    private String identifierSummary;

    public DwellingGroupImpl(String name, String displayName, PostcodeDatum postcode) {
        this.name = name;
        this.displayName = displayName;
        this.postcode = postcode;
        this.dwellings = new ArrayList<Dwelling>();
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        if (displayName == null) {
            StringBuilder stringBuilder = new StringBuilder(getIdentifierSummary());
            if (stringBuilder.length() > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(getUniquePart());
            return stringBuilder.toString();
        }
        return displayName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setUniquePart(String uniquePart) {
        this.uniquePart = uniquePart;
    }

    @Override
    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public Point getPoint() {
        return point == null ? postcode.getPoint() : point;
    }

    @Override
    public int size() {
        return dwellings.size();
    }

    @Override
    public void add(Dwelling dwelling) {
        dwellings.add(dwelling);
        postcode.addHouse(dwelling.getCouncilTaxBand());
    }

    @Override
    public void load(ApplicationContext applicationContext, Element dwellingGroupElt) {
        if (!dwellingGroupElt.getTagName().equals("DwellingGroup")) throw new IllegalArgumentException("You have not provided a Route node");
        String postcode = dwellingGroupElt.getElementsByTagName("Postcode").item(0).getTextContent();
        if (!this.postcode.getPostcode().equals(postcode)) {
            throw new RuntimeException("This is not the DwellingGroup for this node as the Postcodes differ");
        }
        String name = dwellingGroupElt.getElementsByTagName("Name").item(0).getTextContent();
        if (!this.name.equals(name)) {
            throw new RuntimeException("This is not the DwellingGroup for this node as the names differ");
        }
    }

    @Override
    public String getIdentifierSummary() {
        if (identifierSummary == null) {
            identifierSummary = establishIdentifierSummary();
        }
        return identifierSummary;
    }

    private String establishIdentifierSummary() {
        if (dwellings.isEmpty()) {
            throw new RuntimeException("Impossible to get summary from no houses");
        }
        if (dwellings.size() == 1) {
            return dwellings.get(0).getIdentifier();
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

    @Override
    public String getUniquePart() {
        return uniquePart == null ? name : uniquePart;
    }

    private String getNumericIdentifierSummary(
            NumericIdentifierSummary numericIdentifierSummary, String joiningOddsAndEvens) {
        StringBuilder stringBuilder = new StringBuilder();
        if (numericIdentifierSummary.all.isEmpty()) {
            return "";
        }
        else if (numericIdentifierSummary.evens.isEmpty()) {
            stringBuilder.append(summarize(getGroupings(numericIdentifierSummary.odds, 2, "")));
            stringBuilder.append(" ODDS ONLY");
        }
        else if (numericIdentifierSummary.odds.isEmpty()) {
            stringBuilder.append(summarize(getGroupings(numericIdentifierSummary.evens, 2, "")));
            stringBuilder.append(" EVENS ONLY");
        }
        else {
            List<String> allGroupings = getGroupings(numericIdentifierSummary.all, 1);
            List<String> evensGroupings = getGroupings(numericIdentifierSummary.evens, 2);
            List<String> oddsGroupings = getGroupings(numericIdentifierSummary.odds, 2);
            if (evensGroupings.size() + oddsGroupings.size() < allGroupings.size()) {
                if (numericIdentifierSummary.evens.get(0) > numericIdentifierSummary.odds.get(0)) {
                    stringBuilder.append(summarize(evensGroupings));
                    stringBuilder.append(" EVENS ONLY");
                    stringBuilder.append(joiningOddsAndEvens);
                    stringBuilder.append(summarize(oddsGroupings));
                    stringBuilder.append(" ODDS ONLY");
                }
                else {
                    stringBuilder.append(summarize(evensGroupings));
                    stringBuilder.append(" EVENS ONLY");
                    stringBuilder.append(joiningOddsAndEvens);
                    stringBuilder.append(summarize(oddsGroupings));
                    stringBuilder.append(" ODDS ONLY");
                }
            }
            else {
                stringBuilder.append(summarize(allGroupings));
            }
        }
        return stringBuilder.toString();
    }

    private List<String> getGroupings(List<Integer> numbers, int delta) {
        return getGroupings(numbers, delta, "");
    }

        private List<String> getGroupings(List<Integer> numbers, int delta, String suffix) {
        List<String> groupings = new ArrayList<String>();
        Integer firstInGroup = null;
        Integer previous = null;
        for (int number : numbers) {
            if (firstInGroup == null) {
                firstInGroup = number;
                previous = number;
            }
            else if (previous + delta < number) {
                if (firstInGroup.equals(previous)) {
                    groupings.add(firstInGroup + suffix);
                }
                else {
                    groupings.add(firstInGroup + " - " + previous + suffix);
                }
                firstInGroup = number;
                previous = number;
            }
            else {
                previous = number;
            }
        }
        if (firstInGroup == previous) {
            groupings.add(firstInGroup + suffix);
        }
        else {
            groupings.add(firstInGroup + " - " + previous + suffix);
        }
        return groupings;
    }

    private StringBuilder summarize(List<String> groupings) {
        StringBuilder stringBuilder = new StringBuilder(groupings.size() * 16);
        stringBuilder.append(groupings.get(0));
        for (int i = 1; i < groupings.size() - 1; i++) {
            stringBuilder.append(", ");
            stringBuilder.append(groupings.get(i));
        }
        if (groupings.size() > 1) {
            stringBuilder.append(" & ");
            stringBuilder.append(groupings.get(groupings.size() - 1));
        }
        return stringBuilder;
    }

    public void filterIdentifierSummaries() {
        numericIdentifierSummary = new NumericIdentifierSummary();
        nonNumericsIdentifierSummary = new NonNumericIdentifierSummary();
        for (Dwelling dwelling : dwellings) {
            try {
                int number = Integer.parseInt(dwelling.getIdentifier());
                numericIdentifierSummary.all.add(number);
                if (number % 2 == 0) {
                    numericIdentifierSummary.evens.add(number);
                }
                else {
                    numericIdentifierSummary.odds.add(number);
                }
            }
            catch (NumberFormatException nfe) {
                nonNumericsIdentifierSummary.add(dwelling.getIdentifier());
            }
        }
        numericIdentifierSummary.sort();
        nonNumericsIdentifierSummary.sort();
    }

    private class NonNumericIdentifierSummary {
        final List<String> unmatched = new ArrayList<String>(dwellings.size());
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
        final List<Integer> odds = new ArrayList<Integer>(dwellings.size());
        final List<Integer> evens = new ArrayList<Integer>(dwellings.size());
        final List<Integer> all = new ArrayList<Integer>(dwellings.size());

        public void sort() {
            Collections.sort(odds);
            Collections.sort(evens);
            Collections.sort(all);
        }
    }

    @Override
    public PostcodeDatum getPostcode() {
        return postcode;
    }

    @Override
    public List<? extends Dwelling> getDwellings() {
        return dwellings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DwellingGroupImpl that = (DwellingGroupImpl) o;
        if (!name.equals(that.name)) return false;
        if (!postcode.equals(that.postcode)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + postcode.hashCode();
        return result;
    }

    @Override
    public int compareTo(DwellingGroup o) {
        return getDisplayName().compareTo(o.getDisplayName());
    }
}
