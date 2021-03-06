package uk.co.epii.conservatives.fredericknorth.routes;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;
import uk.co.epii.conservatives.fredericknorth.utilities.ResourceHelper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * User: James Robinson
 * Date: 23/06/2013
 * Time: 20:44
 */
public class RoutableAreaTest {

    @Ignore
    @Test
    public void wardWithOneRouteToXmlTest() throws Exception {
        RoutableArea routableArea = new DefaultRoutableArea(null, null);
        routableArea.removeAll();
        Route route = routableArea.createRoute("Route 2");
        route.getDwellingGroups().add(new DummyDwellingGroup("CHAPEL HOUSE STREET, LONDON", 30, new Point(324, 467)));
        route.getDwellingGroups().add(new DummyDwellingGroup("THERMOPYLAE GATE, LONDON", 20, new Point(243, 674)));
        route.getDwellingGroups().add(new DummyDwellingGroup("MACQUARIE WAY, LONDON", 10, new Point(432, 746)));
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element xml = routableArea.toXml(document);
        document.appendChild(xml);
        String result = ResourceHelper.toString(document);
        String expected = ResourceHelper.readResource(RoutableAreaTest.class.getResource("/simpleWard.xml"));
        assertEquals(expected, result);
        fail("This test should not pass as simpleWard.xml needs updating");
    }

}
