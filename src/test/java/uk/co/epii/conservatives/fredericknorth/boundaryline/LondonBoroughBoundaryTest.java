package uk.co.epii.conservatives.fredericknorth.boundaryline;

import com.vividsolutions.jts.geom.MultiPolygon;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerRegistrar;
import uk.co.epii.conservatives.robertwalpole.DataSet;

import java.io.File;
import java.io.IOException;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 11:59
 */
public class LondonBoroughBoundaryTest {

    private static final Logger LOG = LoggerFactory.getLogger(LondonBoroughBoundaryTest.class);

    @Ignore
    @Test
    public void loadTowerHamlets() throws IOException {
        DataSet councils = DataSet.createFromResource(
                LondonBoroughBoundaryTest.class.getResource("/district_borough_unitary_region.shp"));
        DataSet wards = DataSet.createFromResource(
                LondonBoroughBoundaryTest.class.getResource("/district_borough_unitary_ward_region.shp"));
        SimpleFeature towerHamlets = councils.getEqualFilterTo("NAME", "Tower Hamlets London Boro").features().next();
        MultiPolygon towerHamletsPolygon = (MultiPolygon)towerHamlets.getAttribute("the_geom");

        SimpleFeatureIterator wardIterator = wards.getFeatureSource().getFeatures().features();
        BoundaryLineFeature towerHamletsBoundary =
                new BoundaryLineFeature(towerHamlets, BoundedAreaType.UNITARY_DISTRICT);
        while (wardIterator.hasNext()) {
            SimpleFeature ward = wardIterator.next();
            MultiPolygon wardMultiPolygon = (MultiPolygon)ward.getAttribute("the_geom");
            if (towerHamletsPolygon.contains(wardMultiPolygon)) {
                towerHamletsBoundary.addChild(new BoundaryLineFeature(ward, BoundedAreaType.UNITARY_DISTRICT_WARD));
            }
        }
        ApplicationContext applicationContext = new TestApplicationContext();
        XMLSerializerRegistrar.registerToContext(applicationContext);
        towerHamletsBoundary.save(applicationContext.getDefaultInstance(XMLSerializer.class),
                new File("/Users/jrrpl/frederickNorth/towerHamletsBoundries.xml"));
    }



}
