package uk.co.epii.conservatives.fredericknorth.routes;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ResourceHelper;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroupTestFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 23/06/2013
 * Time: 20:44
 */
public class WardTest {

    @Test
    public void wardWithOneRouteToXmlTest() throws Exception {
        Ward ward = new WardImpl("E05000583", "");
        ward.removeAll();
        Route route = ward.createRoute("Route 2");
        route.getDwellingGroups().add(DwellingGroupTestFactory.getInstance(
                "CHAPEL HOUSE STREET, LONDON", "CHAPEL HOUSE STREET, LONDON", "E14 3AS"));
        route.getDwellingGroups().add(DwellingGroupTestFactory.getInstance(
                "THERMOPYLAE GATE, LONDON", "THERMOPYLAE GATE, LONDON", "E14 3AX"));
        route.getDwellingGroups().add(DwellingGroupTestFactory.getInstance(
                "MACQUARIE WAY, LONDON", "MACQUARIE WAY, LONDON", "E14 3AU"));
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element xml = ward.toXml(document);
        document.appendChild(xml);
        String result = ResourceHelper.toString(document);
        String expected = ResourceHelper.readResource(WardTest.class.getResource("/simpleWard.xml"));
        assertEquals(expected, result);
    }

}
