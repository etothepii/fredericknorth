package uk.co.epii.conservatives.fredericknorth.routes;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.dummydata.TestCouncilWard;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;

/**
 * User: James Robinson
 * Date: 13/11/2013
 * Time: 06:27
 */
public class DefaultRoutableAreaTest {

    @Before
    public void setUp() {
        TestCouncilWard.reset();
    }

    @Test
    public void toXmlTest() {
        TestCouncilWard.initiateRoutes();
        try {
            String expected = FileUtils.fileRead(FileUtils.toFile(
                    DefaultRoutableAreaTest.class.getResource(
                            "/uk/co/epii/conservatives/fredericknorth/routes/DefaultRoutableAreaTest1.xml")));
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder;
            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            }
            catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            Document document = documentBuilder.newDocument();
            Element routableAreasElt = TestCouncilWard.councilWardRoutes.toXml(document);
            document.appendChild(routableAreasElt);
            String result = new XMLSerializerImpl().toString(document);
            assertEquals(expected, result);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Test
    public void loadTest() {
        TestCouncilWard.councilWardRoutes.load(new XMLSerializerImpl().fromFile(FileUtils.toFile(
                DefaultRoutableAreaTest.class.getResource(
                        "/uk/co/epii/conservatives/fredericknorth/routes/DefaultRoutableAreaTest1.xml"))).
                getDocumentElement());
        assertEquals(3, TestCouncilWard.councilWardRoutes.getRouteCount());
        assertEquals(3, TestCouncilWard.councilWardRoutes.getRoutedDwellingGroups().size());
        assertEquals(3, TestCouncilWard.councilWardRoutes.getUnroutedDwellingGroups().size());
        assertEquals(1, TestCouncilWard.postalDistrictCW1Routes.getRouteCount());
        assertEquals(1, TestCouncilWard.postalDistrictCW1Routes.getRoutedDwellingGroups().size());
        assertEquals(1, TestCouncilWard.postalDistrictCW1Routes.getUnroutedDwellingGroups().size());
        assertEquals(1, TestCouncilWard.postalDistrictCW2Routes.getRouteCount());
        assertEquals(1, TestCouncilWard.postalDistrictCW2Routes.getRoutedDwellingGroups().size());
        assertEquals(1, TestCouncilWard.postalDistrictCW2Routes.getUnroutedDwellingGroups().size());

    }

}
