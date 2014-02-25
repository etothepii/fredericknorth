package uk.co.epii.conservatives.fredericknorth.opendata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: James Robinson
 * Date: 24/02/2014
 * Time: 20:38
 */
public class NumericIdentifierSummaryImpl implements NumericIdentifierSummary {

    private final List<Integer> all;
    private final List<Integer> odds;
    private final List<Integer> evens;

    private String evensOnly = " even";
    private String oddsOnly = " odd";

    public NumericIdentifierSummaryImpl() {
        all = new ArrayList<Integer>();
        evens = new ArrayList<Integer>();
        odds = new ArrayList<Integer>();
    }

    @Override
    public void add(int dwellingNumber) {
        all.add(dwellingNumber);
        if (dwellingNumber % 2 == 0) {
            evens.add(dwellingNumber);
        }
        else {
            odds.add(dwellingNumber);
        }
    }

    @Override
    public String summarize(String finalConcatination) {
        if (all.isEmpty()) {
            return "";
        }
        Collections.sort(all);
        Collections.sort(odds);
        Collections.sort(evens);
        String[] summaries = new String[] {
                getNumericIdentifierOddEvenSummary(finalConcatination),
                summarize(getGroupings(this.all, 1), "").toString(),
        };
        String shortestSummary = null;
        for (String summary : summaries) {
            if (shortestSummary == null || summary.length() < shortestSummary.length()) {
                shortestSummary = summary;
            }
        }
        return shortestSummary;
    }

    private String getNumericIdentifierOddEvenSummary(String joiningOddsAndEvens) {
        StringBuilder stringBuilder = new StringBuilder();
        if (all.isEmpty()) {
            return "";
        }
        else if (evens.isEmpty()) {
            stringBuilder.append(summarize(getGroupings(odds, 2, ""), oddsOnly));
        }
        else if (odds.isEmpty()) {
            stringBuilder.append(summarize(getGroupings(evens, 2, ""), evensOnly));
        }
        else {
            List<List<Integer>> evensGroupings = getGroupings(evens, 2);
            List<List<Integer>> oddsGroupings = getGroupings(odds, 2);
            if (evens.get(0) < odds.get(0)) {
                stringBuilder.append(summarize(evensGroupings, evensOnly));
                stringBuilder.append(joiningOddsAndEvens);
                stringBuilder.append(summarize(oddsGroupings, oddsOnly));
            }
            else {
                stringBuilder.append(summarize(oddsGroupings, oddsOnly));
                stringBuilder.append(joiningOddsAndEvens);
                stringBuilder.append(summarize(evensGroupings, evensOnly));
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
}
