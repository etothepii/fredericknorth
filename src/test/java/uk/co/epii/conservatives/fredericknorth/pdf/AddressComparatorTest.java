package uk.co.epii.conservatives.fredericknorth.pdf;


import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 16/03/2014
 * Time: 11:45
 */
public class AddressComparatorTest {

    private AddressComparator addressComparator;

    @Before
    public void setUp() {
        addressComparator = new AddressComparator();
    }

    @Test
    public void comapreTest1() {
        String a = "1 - 10 MAGPIE HOUSE AT 33, SYCAMORE AVENUE";
        String b = "1 - 10 ROWAN HOUSE 5, HORNBEAM SQUARE";
        assertTrue(addressComparator.compare(a, b) > 0);
    }

    @Test
    public void comapreTest2() {
        String a = "2, 4 & 6 HERON HOUSE AT 35, SYCAMORE AVENUE";
        String b = "1 - 6 LAUREL HOUSE 4, HORNBEAM SQUARE";
        assertTrue(addressComparator.compare(a, b) > 0);
    }

    @Test
    public void comapreTest3() {
        String a = "1, 3, 3, 5 & 5 - 29 odd & 4 - 44 even SYCAMORE AVENUE";
        String b = "1 - 39 WATERSIDE CLOSE";
        assertTrue(addressComparator.compare(a, b) > 0);
    }
}
