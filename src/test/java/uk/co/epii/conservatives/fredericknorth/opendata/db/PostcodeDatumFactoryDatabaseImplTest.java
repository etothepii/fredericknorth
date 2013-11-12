package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.util.LocaleServiceProviderPool;
import uk.co.epii.conservatives.fredericknorth.opendata.Dwelling;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatum;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 04/11/2013
 * Time: 00:54
 */
public class PostcodeDatumFactoryDatabaseImplTest {

    private static final Logger LOG = LoggerFactory.getLogger(PostcodeDatumFactoryDatabaseImpl.class);

    private static PostcodeDatumFactoryDatabaseImpl postcodeDatumFactory;

    @BeforeClass
    public static void setUp() {
        ApplicationContext springContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        postcodeDatumFactory = (PostcodeDatumFactoryDatabaseImpl)springContext.getBean("postcodeDatumFactory");
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

    private Rectangle getExtents(PostcodeDatum dwellingGroup) {
        int minX = dwellingGroup.getPoint().x;
        int maxX = dwellingGroup.getPoint().x;
        int minY = dwellingGroup.getPoint().y;
        int maxY = dwellingGroup.getPoint().y;
        for (Dwelling dwelling : dwellingGroup.getDwellings()) {
            if (dwelling.getPoint() == null) {
                continue;
            }
            if (dwelling.getPoint().x < minX) minX = dwelling.getPoint().x;
            if (dwelling.getPoint().x > maxX) maxX = dwelling.getPoint().x;
            if (dwelling.getPoint().y < minY) minY = dwelling.getPoint().y;
            if (dwelling.getPoint().y > maxY) maxY = dwelling.getPoint().y;
        }
        return new Rectangle(minX, minY, Math.max(1, maxX - minX), Math.max(1, maxY - minY));
    }

}