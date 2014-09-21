package uk.co.epii.conservatives.fredericknorth.pdf;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundaryLineControllerRegistrar;
import uk.co.epii.conservatives.fredericknorth.dummydata.TestCouncilWard;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.maps.LocationFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.MapLabelFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGeneratorRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwelling;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;
import uk.co.epii.spencerperceval.tuple.Duple;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
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

    public void truncateTest(String[] street, String[] full, String[] expected) {
        String commonEnding = PDFRendererImpl.getCommonEnding(Arrays.asList(street));
        List<Duple<String, List<Location>>> duples = new ArrayList<Duple<String, List<Location>>>();
        for (String string : full) {
            List<Location> dwellings = new ArrayList<Location>();
            for (int i = 1; i <= 2; i++) {
                dwellings.add(new DummyDwelling(i + "", null));
            }
            duples.add(new Duple<String, List<Location>>(string, dwellings));
        }
        PDFRendererImpl.truncate(duples, commonEnding);
        String[] result = new String[duples.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = duples.get(i).getFirst();
        }
        assertArrayEquals(expected, result);
    }

    @Test
    public void truncateTest1() {
        String[] street = new String[] {
                "17, PENNYFIELDS, LONDON",
                "BIRCHFIELD HOUSE, BIRCHFIELD STREET, LONDON",
                "ELDERFIELD HOUSE, PENNYFIELDS, LONDON",
                "GORSEFIELD HOUSE, EAST INDIA DOCK ROAD, LONDON",
                "PENNYFIELDS, LONDON",
                "PINEFIELD CLOSE, LONDON",
                "ROSEFIELD GARDENS, LONDON",
                "THORNFIELD HOUSE, ROSEFIELD GARDENS, LONDON"
        };
        String[] full = new String[] {
                "17, PENNYFIELDS, LONDON",
                "2 - 24 BIRCHFIELD HOUSE, BIRCHFIELD STREET, LONDON",
                "2 - 12 & MAIS 1ST & 2ND FLRSS 1 ELDERFIELD HOUSE, PENNYFIELDS, LONDON",
                "1 - 13 GORSEFIELD HOUSE, EAST INDIA DOCK ROAD, LONDON",
                "A, B & C PENNYFIELDS, LONDON",
                "1 - 12, 14, 16, 18, 20, 22 & 24, 1A, 1B & 1C PINEFIELD CLOSE, LONDON",
                "1 - 41 ODDS ONLY & 2 - 70 EVENS ONLY ROSEFIELD GARDENS, LONDON",
                "1 - 75 THORNFIELD HOUSE, ROSEFIELD GARDENS, LONDON"
        };
        String[] expected = new String[] {
                "17, PENNYFIELDS",
                "2 - 24 BIRCHFIELD HOUSE, BIRCHFIELD STREET",
                "2 - 12 & MAIS 1ST & 2ND FLRSS 1 ELDERFIELD HOUSE, PENNYFIELDS",
                "1 - 13 GORSEFIELD HOUSE, EAST INDIA DOCK ROAD",
                "A, B & C PENNYFIELDS",
                "1 - 12, 14, 16, 18, 20, 22 & 24, 1A, 1B & 1C PINEFIELD CLOSE",
                "1 - 41 ODDS ONLY & 2 - 70 EVENS ONLY ROSEFIELD GARDENS",
                "1 - 75 THORNFIELD HOUSE, ROSEFIELD GARDENS"
        };
        truncateTest(street, full, expected);
    }

    @Test
    public void truncateTest2() {
        String[] street = new String[] {
                "PENNYFIELDS, LONDON",
        };
        String[] full = new String[] {
                "17 - 19, PENNYFIELDS, LONDON",
        };
        String[] expected = new String[] {
                "17 - 19, PENNYFIELDS"
        };
        truncateTest(street, full, expected);
    }

    @Test
    public void truncateTest3() {
        String[] street = new String[] {
                "PENNYFIELDS STREET, LONDON",
                "PENNYMEADOW STREET, LONDON",
        };
        String[] full = new String[] {
                "17 - 19, PENNYFIELDS STREET, LONDON",
                "17 - 19, PENNYMEADOW STREET, LONDON",
        };
        String[] expected = new String[] {
                "17 - 19, PENNYFIELDS STREET",
                "17 - 19, PENNYMEADOW STREET"
        };
        truncateTest(street, full, expected);
    }

    @Ignore
    @Test
    public void createMultipleSimpleRoutesTest() {
//        ApplicationContext applicationContext = new TestApplicationContext();
//        DwellingGroupFactoryRegistrar.registerToContext(applicationContext);
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
