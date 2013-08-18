package uk.co.epii.conservatives.fredericknorth.routeableareabuildergui;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.boundaryline.*;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.*;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerRegistrar;

import java.awt.*;
import java.io.File;
import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

/**
 * User: James Robinson
 * Date: 29/07/2013
 * Time: 11:55
 */
public class BuilderMapFrameModelTest {

    private static final Logger LOG = LoggerFactory.getLogger(BuilderMapFrameModelTest.class);

    private ApplicationContext applicationContext;
    private Rectangle tinyRectangle = new Rectangle(536860, 179019, 100, 100);

    @Before
    public void setUp() {
        LOG.info("Loading Config");
        applicationContext =
                new TestApplicationContext();
        LOG.info("Loading OS Map Locator");
        OSMapLocatorRegistrar.registerToContext(applicationContext);
        LOG.info("Loading XML Serializer");
        XMLSerializerRegistrar.registerToContext(applicationContext);
        applicationContext.registerNamedInstance(Rectangle.class, Keys.UNIVERSE, tinyRectangle);
        LOG.info("Loading OS Map Loader");
        OSMapLoaderRegistrar.registerToContext(applicationContext);
        LOG.info("Loading Map View Generators");
        MapViewGeneratorRegistrar.registerToContext(applicationContext);
        LOG.info("Loading Bounded Area Factory");
        BoundedAreaFactoryRegistrar.registerToContext(applicationContext);
        LOG.info("Setup Complete");
    }

    @Test
    public void loadDataIntoComboBoxesTest() {
        BuilderMapFrameModel builderMapFrameModel =
                new BuilderMapFrameModel(applicationContext, false);
        builderMapFrameModel.load(new File("/Users/jrrpl/frederickNorth/towerHamletsBoundries.xml"));
        assertEquals(2, builderMapFrameModel.getBoundedAreaSelectionModel()
                .getComboBoxModel(BoundedAreaType.UNITARY_DISTRICT).getSize());
        builderMapFrameModel.getBoundedAreaSelectionModel().getComboBoxModel(
                BoundedAreaType.UNITARY_DISTRICT).setSelectedItem(
                        builderMapFrameModel.getBoundedAreaSelectionModel().getComboBoxModel(
                                BoundedAreaType.UNITARY_DISTRICT).getElementAt(1));
        assertEquals(18, builderMapFrameModel.getBoundedAreaSelectionModel()
                .getComboBoxModel(BoundedAreaType.UNITARY_DISTRICT_WARD).getSize());
        assertEquals(1, builderMapFrameModel.getBoundedAreaSelectionModel()
                .getComboBoxModel(BoundedAreaType.POLLING_DISTRICT).getSize());
    }

}
