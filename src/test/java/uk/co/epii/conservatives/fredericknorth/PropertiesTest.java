package uk.co.epii.conservatives.fredericknorth;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 21/06/2013
 * Time: 18:14
 */

public class PropertiesTest {

    @Test
    public void testGetMissingProperty() {
        String result = new TestApplicationContext().getProperty("ckskjdbcksjc");
        assertEquals(result, null);
    }

    @Test
    public void testGetKnownProperty() {
        String result = new TestApplicationContext().getProperty("GridSquareReferences");
        assertEquals(result, "/ukGridSquareReferences.txt");
    }


}
