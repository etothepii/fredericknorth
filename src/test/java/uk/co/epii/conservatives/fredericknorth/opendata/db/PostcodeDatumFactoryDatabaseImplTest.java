package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatum;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactory;

import java.awt.*;
import java.util.Collection;

/**
 * User: James Robinson
 * Date: 04/11/2013
 * Time: 00:54
 */
public class PostcodeDatumFactoryDatabaseImplTest {

    private static PostcodeDatumFactory postcodeDatumFactory;

    @BeforeClass
    public static void setUp() {
        ApplicationContext springContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        postcodeDatumFactory = (PostcodeDatumFactory)springContext.getBean("postcodeDatumFactory");
    }

    @Test
    public void getPostcodeDatumsWithBoundsTest() {
        Collection<? extends PostcodeDatum> postcodes =
                postcodeDatumFactory.getPostcodes(new Rectangle(537820, 178220, 210, 120));
        for (PostcodeDatum postcodeDatum : postcodes) {

        }
    }

}
