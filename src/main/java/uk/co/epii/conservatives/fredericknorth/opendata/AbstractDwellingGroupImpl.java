package uk.co.epii.conservatives.fredericknorth.opendata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingDatabaseImpl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 07/11/2013
 * Time: 00:13
 */
public abstract class AbstractDwellingGroupImpl implements DwellingGroup {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractDwellingGroupImpl.class);

  private static final int MAX_IDENTIFIER_SUMMARY_LENGTH = 20;
  protected String commonName;
    private String name;
    private NumericIdentifierSummary numericIdentifierSummary;
    private NonNumericIdentifierSummary nonNumericsIdentifierSummary;
    private String identifierSummary;

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
        String identifierSummary = getIdentifierSummary();
        return identifierSummary == null ? commonName : String.format("%s %s", identifierSummary, commonName);
    }

    @Override
    public String getIdentifierSummary() {
        if (size() == 0 || (identifierSummary = establishIdentifierSummary()) == null ||
                identifierSummary.length() == 0 || identifierSummary.equals("null")) {
            return null;
        }
        if (identifierSummary.length() < MAX_IDENTIFIER_SUMMARY_LENGTH) {
            return identifierSummary;
        }
        return null;
    }

    private String establishIdentifierSummary() {
        if (identifierSummary != null) {
          return identifierSummary;
        }
        if (size() == 0) {
            throw new RuntimeException("Impossible to get summary from no houses");
        }
        if (size() == 1) {
            return getDwellings().iterator().next().getName();
        }
        filterIdentifierSummaries();
        if (nonNumericsIdentifierSummary.isEmpty()) {
            return numericIdentifierSummary.summarize(" & ");
        }
        if (nonNumericsIdentifierSummary.unmatched.size() + nonNumericsIdentifierSummary.matchedFlats.size() <= 5) {
            StringBuilder stringBuilder = new StringBuilder(numericIdentifierSummary.summarize(", "));
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
    public String getCommonName() {
        return commonName;
    }

    public void filterIdentifierSummaries() {
        numericIdentifierSummary = new NumericIdentifierSummaryImpl();
        nonNumericsIdentifierSummary = new NonNumericIdentifierSummary();
        for (Location dwelling : getDwellings()) {
            try {
                numericIdentifierSummary.add(Integer.parseInt(dwelling.getName()));
            }
            catch (NumberFormatException nfe) {
                if (dwelling.getName() == null) {
                  if (dwelling instanceof DwellingDatabaseImpl) {
                    LOG.error("Dwelling with null name UPRN: " +
                            ((DwellingDatabaseImpl)dwelling).getDeliveryPointAddress().getUprn());
                  }
                  else {
                    LOG.error("Dwelling with null name");
                  }
                }
                nonNumericsIdentifierSummary.add(dwelling.getName());
            }
        }
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
            if (identifier == null) {
                unmatched.add("");
                return;
            }
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
                matchedFlats.put(prefixAndSuffix, new NumericIdentifierSummaryImpl());
            }
            NumericIdentifierSummary numericIdentifierSummary = matchedFlats.get(prefixAndSuffix);
            numericIdentifierSummary.add(Integer.parseInt(matcher.group(2)));
        }

        public int size() {
            return count;
        }

        public void sort() {
            Collections.sort(unmatched);
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
                stringBuilder.append(entry.getValue().summarize(" & "));
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
}
