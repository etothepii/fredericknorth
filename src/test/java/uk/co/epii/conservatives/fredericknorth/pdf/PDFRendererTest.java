package uk.co.epii.conservatives.fredericknorth.pdf;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundaryLineControllerRegistrar;
import uk.co.epii.conservatives.fredericknorth.dummydata.TestCouncilWard;
import uk.co.epii.conservatives.fredericknorth.maps.LocationFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.MapLabelFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGeneratorRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapLocatorRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingProcessorRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.fail;

/**
 * User: James Robinson
 * Date: 26/06/13
 * Time: 20:15
 */
public class PDFRendererTest {

    private static final Logger LOG = Logger.getLogger(PDFRendererTest.class);

    @Before
    public void setUp() {
        TestCouncilWard.reset();
        TestCouncilWard.initiateRoutes();
    }

    @Test
    public void createSimpleRouteTest() {
        TestApplicationContext testApplicationContext = new TestApplicationContext();
        MapLabelFactoryRegistrar.registerToContext(testApplicationContext);
        MapViewGeneratorRegistrar.registerNamedToContext(testApplicationContext, Keys.PDF_GENERATOR, new NullProgressTracker());
        BoundaryLineControllerRegistrar.registerToContext(testApplicationContext);
        LocationFactoryRegistrar.registerToContext(testApplicationContext);
        PDFRendererRegistrar.registerToContext(testApplicationContext);
        PDFRenderer pdfRenderer = testApplicationContext.getDefaultInstance(PDFRenderer.class);
        String simpleRouteOutput = System.getProperty("user.home");
        simpleRouteOutput +=
                (simpleRouteOutput.endsWith("/") || simpleRouteOutput.endsWith("\\")) ? "" : "/";
        simpleRouteOutput += testApplicationContext.getProperty("WorkingDirectory");
        simpleRouteOutput +=
                (simpleRouteOutput.endsWith("/") || simpleRouteOutput.endsWith("\\")) ? "" : "/";
        simpleRouteOutput += "simpleRoute.pdf";
        pdfRenderer.buildRouteGuide(TestCouncilWard.postalDistrictCW1Routes.getRoutes().iterator().next(),
                new File(simpleRouteOutput));
    }

    @Ignore
    @Test
    public void createMultipleSimpleRoutesTest() {
//        ApplicationContext applicationContext = new TestApplicationContext();
//        PostcodeDatumFactoryRegistrar.registerToContext(applicationContext);
//        DwellingProcessorRegistrar.registerToContext(applicationContext, new NullProgressTracker(),
//                PDFRendererTest.class.getResourceAsStream("/millwallDwellingsSubset.txt"));
//        OSMapLocatorRegistrar.registerToContext(applicationContext);
//        PostcodeProcessorRegistrar.registerToContext(applicationContext,
//                PDFRendererTest.class.getResource("/millwallPostcodeSubset.txt"), 0);
//        LocationFactoryRegistrar.registerToContext(applicationContext);
//        MapLabelFactoryRegistrar.registerToContext(applicationContext);
//        XMLSerializerRegistrar.registerToContext(applicationContext);
//        CouncilRegistrar.registerToContext(applicationContext,
//                PDFRendererTest.class.getResource("/millwallWard.txt"),
//                PDFRendererTest.class.getResource("/millwallMeetingPointsSubset.csv"));
//        Rectangle universe =
//                applicationContext.getDefaultInstance(Council.class).getUniverse();
//        universe = new Rectangle(universe.x - 500, universe.y - 500, universe.width + 1000, universe.height + 1000);
//        applicationContext.registerNamedInstance(Rectangle.class, Keys.UNIVERSE, universe);
//        OSMapLoaderRegistrar.registerToContext(applicationContext);
//        MapViewGeneratorRegistrar.registerNamedToContext(applicationContext, Keys.PDF_GENERATOR);
//        MapImageFactoryRegistrar.registerToContext(applicationContext);
//        PDFRendererRegistrar.registerToContext(applicationContext);
//        PDFRenderer pdfRenderer = applicationContext.getDefaultInstance(PDFRenderer.class);
//        Council council = applicationContext.getDefaultInstance(Council.class);
//        DwellingProcessor dwellingProcessor = applicationContext.getDefaultInstance(DwellingProcessor.class);
//        URL millwallRoutesLocation = PDFRendererImpl.class.getResource("/millwallRoutesSubset.xml");
//        String tempFontLocation = System.getProperty("java.io.tmpdir");
//        tempFontLocation += (tempFontLocation.endsWith("/") || tempFontLocation.endsWith("\\")) ? "" : "/";
//        tempFontLocation += "millwallRoutesSubset.xml";
//        File file = new File(tempFontLocation);
//        try {
//            FileUtils.copyURLToFile(millwallRoutesLocation, file);
//        }
//        catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        council.load(applicationContext, file);
//        File output = new File(System.getProperty("user.home").concat("/frederickNorth/TestMultiOutput.pdf"));
//        pdfRenderer.buildRoutesGuide(council, output);
        fail("This test is not yet finished");
    }


}
