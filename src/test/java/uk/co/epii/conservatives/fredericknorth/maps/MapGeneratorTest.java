package uk.co.epii.conservatives.fredericknorth.maps;

import org.apache.log4j.Logger;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 24/06/2013
 * Time: 07:50
 */
public class MapGeneratorTest {

    private static final Logger LOG = Logger.getLogger(MapGeneratorTest.class);

//    @Ignore
//    @Test
//    public void buildMeetingPointLocationsMap() throws IOException {
//        MapViewGenerator mapViewGenerator = MapViewGeneratorRegistrar.getDefaultInstance();
//        Council council = CouncilRegistrar.getCouncil();
//        List<? extends Location> meetingPoints = council.getMeetingPoints();
//        Dimension targetSize = new Dimension(640, 480);
//        mapViewGenerator.setViewPortSize(targetSize);
//        mapViewGenerator.scaleToFitRectangle(LocationFactory.calculatePaddedRectangle(meetingPoints));
//        MapView mapView = mapViewGenerator.getView();
//        BufferedImage map = mapView.getLabelledImage(meetingPoints);
//        assertEquals(map.getWidth(), targetSize.width);
//        assertEquals(map.getHeight(), targetSize.height);
//        String outFile = System.getProperty("user.home").concat(ApplicationContext.getProperty("TestOutput").concat("meetingPoints.png"));
//        ImageIO.write(map, "PNG", new File(outFile));
//        LOG.info(outFile);
//    }

}
