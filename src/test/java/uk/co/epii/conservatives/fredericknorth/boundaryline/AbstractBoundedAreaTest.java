package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.junit.Ignore;
import org.opengis.filter.FilterFactory;
import org.junit.BeforeClass;
import org.geotools.data.FileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.swing.JMapFrame;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.PropertyIsEqualTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: James Robinson
 * Date: 20/07/2013
 * Time: 17:33
 */
public class AbstractBoundedAreaTest {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBoundedAreaTest.class);

    private static final String fileToLoad = "district_borough_unitary_region";
    private static DataSet dataSet;

    @BeforeClass
    public static void createFile() {
        dataSet = DataSet.createFromResource(AbstractBoundedAreaTest.class.getResource("/" + fileToLoad + ".shp"));
    }

    @Ignore
    @Test
    public void loadBoundedAreasTest() throws Exception {
        SimpleFeatureSource featureSource = dataSet.getFeatureSource();
        LOG.debug("{}", featureSource.getSchema());
        SimpleFeatureCollection simpleFeatureCollection = featureSource.getFeatures();
        FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null);
        PropertyIsEqualTo propertyIsEqualTo =
                filterFactory.equal(filterFactory.property("NAME"),
                        filterFactory.literal("Tower Hamlets London Boro"), true);
        SimpleFeatureCollection subCollection = featureSource.getFeatures(propertyIsEqualTo);
        SimpleFeatureIterator simpleFeatureIterator = subCollection.features();
        LOG.debug("size: {}", subCollection.size());
        int item = 1;
        SimpleFeature simpleFeature = simpleFeatureIterator.next();
        LOG.debug("{}: {}", item++, simpleFeature.getID());
        for (int attributeIndex = 0; attributeIndex < simpleFeature.getAttributeCount(); attributeIndex++) {
            Object object = simpleFeature.getAttribute(attributeIndex);
            String attributeName = featureSource.getSchema().getDescriptor(attributeIndex).getLocalName();
            LOG.debug("{}: {} --> {}", new Object[] {attributeIndex, attributeName, object});
        }

        //Create a map content and add our shapefile to it
        MapContent map = new MapContent();
        map.setTitle("Quickstart");

        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        Layer layer = new FeatureLayer(subCollection, style);
        map.addLayer(layer);

        // Now display the map
        JMapFrame.showMap(map);

        Thread.sleep(100000L);
    }

    @Test
    public void readDBF() {
    }

}
