package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 04/11/2013
 * Time: 00:54
 */
public class DwellingGroupFactoryDatabaseImplTest {

    private static final Logger LOG = LoggerFactory.getLogger(DwellingGroupFactoryDatabaseImpl.class);

    private static DwellingGroupFactoryDatabaseImpl postcodeDatumFactory;

    @BeforeClass
    public static void setUp() {
        ApplicationContext springContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        postcodeDatumFactory = (DwellingGroupFactoryDatabaseImpl)springContext.getBean("postcodeDatumFactory");
    }

    @Test
    public void getMedianTest1() {
        Point result = postcodeDatumFactory.calculateMedian(
                new ArrayList<Integer>(Arrays.asList(400,500,900,100)),
                new ArrayList<Integer>(Arrays.asList(100,500,900,1100)));
        Point expected = new Point(450, 700);
        assertEquals(expected, result);
    }

    @Test
    public void getMedianTest2() {
        Point result = postcodeDatumFactory.calculateMedian(
                new ArrayList<Integer>(Arrays.asList(400,500,900,100,300)),
                new ArrayList<Integer>(Arrays.asList(100,500,900,1100,50)));
        Point expected = new Point(400, 500);
        assertEquals(expected, result);
    }

}
