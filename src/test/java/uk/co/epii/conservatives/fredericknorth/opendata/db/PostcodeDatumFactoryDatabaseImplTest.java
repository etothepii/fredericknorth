package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import sun.util.LocaleServiceProviderPool;
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

    @Test
    public void getPostcodeDatumsWithBoundsTest() {
        Rectangle rectangle = new Rectangle(537820, 178220, 210, 120);
        Collection<? extends PostcodeDatum> postcodes = postcodeDatumFactory.getPostcodes(rectangle);
        for (PostcodeDatum postcodeDatum : postcodes) {
            PostcodeDatumDatabaseImpl impl = (PostcodeDatumDatabaseImpl)postcodeDatum;
            for (DwellingGroupDatabaseImpl dwellingGroup : impl.getDwellingGroups().values()) {
                LOG.debug("{} {}: ({}, {})", new Object[] {postcodeDatum.getName(), dwellingGroup.getName(),
                        dwellingGroup.getPoint().getX(), dwellingGroup.getPoint().getY()});
                assertTrue(String.format("%s in %s: ", dwellingGroup.getPoint(), rectangle),
                        rectangle.contains(dwellingGroup.getPoint()));
            }
        }
    }

}
