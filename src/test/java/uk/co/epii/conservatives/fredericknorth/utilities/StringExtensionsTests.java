package uk.co.epii.conservatives.fredericknorth.utilities;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 05/09/2013
 * Time: 23:44
 */
public class StringExtensionsTests {

    @Test
    public void getAbbreviationTest1() {
        String result = StringExtentions.getAbbreviation("2014 - Bethnal Green", 2);
        String expected = "BG";
        assertEquals(expected, result);
    }

    @Test
    public void getAbbreviationTest2() {
        String result = StringExtentions.getAbbreviation("2014 - Blackwall and Cubitt Town", 2);
        String expected = "BC";
        assertEquals(expected, result);
    }

    @Test
    public void getAbbreviationTest3() {
        String result = StringExtentions.getAbbreviation("2014 - Bow East", 2);
        String expected = "BE";
        assertEquals(expected, result);
    }

    @Test
    public void getAbbreviationTest4() {
        String result = StringExtentions.getAbbreviation("2014 - Bow West", 2);
        String expected = "BW";
        assertEquals(expected, result);
    }

    @Test
    public void getAbbreviationTest5() {
        String result = StringExtentions.getAbbreviation("2014 - Lansbury", 2);
        String expected = "LA";
        assertEquals(expected, result);
    }

    @Test
    public void getAbbreviationTest6() {
        String result = StringExtentions.getAbbreviation("2014 - Blackwall and Cubitt Town", 6);
        String expected = "BCTOWN";
        assertEquals(expected, result);
    }

    @Test
    public void getAbbreviationTest7() {
        String result = StringExtentions.getAbbreviation("2014 - Blackwall and Cubitt Town", 12);
        String expected = "BLCUBITTTOWN";
        assertEquals(expected, result);
    }

    @Test
    public void getCommonTest1() {
        String a = "A Road";
        String b = "B Road";
        assertEquals(" Road", StringExtentions.getCommonEnding(a, b));
    }

}
