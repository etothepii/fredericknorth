package uk.co.epii.conservatives.fredericknorth.maps;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 18/08/2013
 * Time: 18:41
 */
public class OSMapLoaderTests {

    private static OSMapLoaderImpl osMapLoader;

    @BeforeClass
    public static void createOSMapLoader() {
        TestApplicationContext applicationContext = new TestApplicationContext();
        OSMapLoaderRegistrar.registerToContext(applicationContext);
        osMapLoader = (OSMapLoaderImpl)applicationContext.getDefaultInstance(OSMapLoader.class);
    }

    @Test
    public void testGetFileNameStreetView() {
        String expected = "StreetView/OS Street View SP/data/sp/sp03ne_06.tif";
        String result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.STREET_VIEW, "sp", 3, "ne", null, 6));
        assertEquals(expected, result);
        result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.STREET_VIEW, "SP", 3, "ne", null, 6));
        assertEquals(expected, result);
        result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.STREET_VIEW, "sp", 3, "NE", null, 6));
        assertEquals(expected, result);
    }

    @Test
    public void testGetFileNameVectorMap() {
        String expected = "VectorMap/OS VectorMap District (Full Colour Raster) TQ/data/TQ09_07.tif";
        String result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.VECTOR_MAP, "tq", 9, null, 7, null));
        assertEquals(expected, result);
        result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.VECTOR_MAP, "TQ", 9, null, 7, null));
        assertEquals(expected, result);
    }



    @Test
    public void testGetFileNameRaster() {
        String expected = "ras250_gb/data/TQ_05.tif";
        String result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.RASTER, "tq", 5, null, null, null));
        assertEquals(expected, result);
        result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.RASTER, "TQ", 5, null, null, null));
        assertEquals(expected, result);
    }



    @Test
    public void testGetFileNameMini() {
        String expected = "minisc_gb/data/RGB_TIF_COMPRESSED/DATA/MiniScale_(standard)_R15_SK.tif";
        String result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.MINI, "sk", null, null, null, null));
        assertEquals(expected, result);
        result = osMapLoader.getPostRootLocation(new OSMapImpl(OSMapType.MINI, "SK", null, null, null, null));
        assertEquals(expected, result);
    }



}
