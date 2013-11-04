package uk.co.epii.conservatives.fredericknorth.pdf;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

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

    @Ignore
    @Test
    public void createSimpleRouteTest() {
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
//        Ward ward = council.getWard("E05000583");
//        Route route = ward.createRoute("Route 2");
//        route.getDwellingGroups().add(dwellingProcessor.getDwellingGroup("E14 3AS", "CHAPEL HOUSE STREET, LONDON"));
//        route.getDwellingGroups().add(dwellingProcessor.getDwellingGroup("E14 3AX", "THERMOPYLAE GATE, LONDON"));
//        route.getDwellingGroups().add(dwellingProcessor.getDwellingGroup("E14 3AU", "MACQUARIE WAY, LONDON"));
//        File file = new File(System.getProperty("user.home").concat("/frederickNorth/TestOutput.pdf"));
//        pdfRenderer.buildRouteGuide(route, file);
        fail("This test is not yet finished");
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
