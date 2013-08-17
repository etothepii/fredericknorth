package uk.co.epii.conservatives.fredericknorth.opendata;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 15/07/2013
 * Time: 23:13
 */
public class DwellingGroupImplTests {

    private DwellingGroup dwellingGroup;

    @Before
    public void setUp() throws Exception {
        dwellingGroup = new DwellingGroupImpl("A Street", null, new DummyPostcodeDatum("A1 1AA"));
    }

    @Test
    public void getIdentifierSummaryTest1() {
        dwellingGroup.add(new DwellingImpl("1", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("2", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("3", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("4", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("5", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("6", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("8", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("9", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "1 - 9 A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest2() {
        dwellingGroup.add(new DwellingImpl("1", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("3", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("5", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("9", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "1 - 9 ODDS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest3() {
        dwellingGroup.add(new DwellingImpl("2", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("4", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("6", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("8", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "2 - 8 EVENS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest4() {
        dwellingGroup.add(new DwellingImpl("2", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("4", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("6", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("8", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("12", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("14", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("16", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("18", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("22", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("24", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("26", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("28", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "2 - 8, 12 - 18 & 22 - 28 EVENS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest5() {
        dwellingGroup.add(new DwellingImpl("2", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("4", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("6", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("8", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("12", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "2 - 8 & 12 EVENS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest6() {
        dwellingGroup.add(new DwellingImpl("2", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("4", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("6", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("8", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("12", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("14", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("16", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("18", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "2 - 8 & 12 - 18 EVENS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest7() {
        dwellingGroup.add(new DwellingImpl("1", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("3", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("5", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("11", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("13", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("15", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("17", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("21", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("23", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("25", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("27", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "1 - 7, 11 - 17 & 21 - 27 ODDS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest8() {
        dwellingGroup.add(new DwellingImpl("1", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("3", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("5", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("11", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("13", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("15", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("17", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "1 - 7 & 11 - 17 ODDS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest9() {
        dwellingGroup.add(new DwellingImpl("FLAT 1", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 3", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 5", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 11", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 13", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 15", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 17", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 21", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 23", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 25", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 27", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "FLATS 1 - 7, 11 - 17 & 21 - 27 ODDS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest10() {
        dwellingGroup.add(new DwellingImpl("FLAT 1", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 3", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 5", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 9", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 11", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 13", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 15", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 17", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 19", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 21", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 23", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 25", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 27", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "FLATS 1 - 27 ODDS ONLY A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest11() {
        dwellingGroup.add(new DwellingImpl("FLAT 1", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 3", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 5", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 9", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 11", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 13", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 15", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 17", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 19", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 21", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 23", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 25", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 27", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 2", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 4", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 6", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 8", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 10", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 12", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 14", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 16", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 18", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 20", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 22", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 24", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 26", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 28", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "FLATS 1 - 28 A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest12() {
        dwellingGroup.add(new DwellingImpl("FLAT 1 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 3 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 5 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 7 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 9 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 11 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 13 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 15 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 17 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 19 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 21 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 23 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 25 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 27 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 2 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 4 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 6 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 8 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 10 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 12 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 14 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 16 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 18 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 20 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 22 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 24 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 26 AT 7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("FLAT 28 AT 7", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "FLATS 1 - 28 AT 7 A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest13() {
        dwellingGroup.add(new DwellingImpl("1", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("2", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("3", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("4", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("5", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("6", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("7", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("8", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("9", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("7A", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "1 - 9 & 7A A Street";
        assertEquals(expected, result);
    }

    @Test
    public void getIdentifierSummaryTest14() {
        dwellingGroup.add(new DwellingImpl("8", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("10", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("12", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("14", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("16", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("18", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("20", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("22", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("24", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("26", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("28", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("30", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("32", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("34", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("36", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("38", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("39", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("40", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("41", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("42", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("43", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("44", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("45", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("46", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("47", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("48", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("49", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("50", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("51", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("52", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("53", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("54", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("55", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("57", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("59", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("61", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("63", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("65", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("67", 'A', dwellingGroup));
        dwellingGroup.add(new DwellingImpl("HARBINGER PRIMARY SCHOOL HOUSE", 'A', dwellingGroup));
        String result = dwellingGroup.getDisplayName();
        String expected = "8 - 54 EVENS ONLY, 39 - 67 ODDS ONLY & HARBINGER PRIMARY SCHOOL HOUSE A Street";
        assertEquals(expected, result);
    }


}
