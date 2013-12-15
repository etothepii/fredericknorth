package uk.co.epii.conservatives.fredericknorth.routes;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.opendata.DummyDwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroupImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumImpl;
import uk.co.epii.conservatives.fredericknorth.utilities.ResourceHelper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 23/06/2013
 * Time: 16:16
 */
public class RouteTest {

    @Test
    public void emptyRouteToXmlTest() throws Exception {
        Route route = new RouteImpl(null, "Route 1");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element xml = route.toXml(document);
        document.appendChild(xml);
        String result = ResourceHelper.toString(document);
        String expected = ResourceHelper.readResource(RouteTest.class.getResource("/EmptyRoute.xml"));
        assertEquals(expected, result);
    }

    @Test
    public void simpleRouteToXmlTest() throws Exception {
        Route route = new RouteImpl(null, "Route 2");
        route.getDwellingGroups().add(new DwellingGroupImpl("CHAPEL HOUSE STREET, LONDON",
                "CHAPEL HOUSE STREET, LONDON", new PostcodeDatumImpl("A1 1AA", new Point(324, 467))));
        route.getDwellingGroups().add(new DwellingGroupImpl("THERMOPYLAE GATE, LONDON", "THERMOPYLAE GATE, LONDON",
                new PostcodeDatumImpl("A1 1AB", new Point(324, 467))));
        route.getDwellingGroups().add(new DwellingGroupImpl("MACQUARIE WAY, LONDON", "MACQUARIE WAY, LONDON",
                new PostcodeDatumImpl("A1 1AC", new Point(324, 467))));
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element xml = route.toXml(document);
        document.appendChild(xml);
        String result = ResourceHelper.toString(document);
        String expected = ResourceHelper.readResource(RouteTest.class.getResource("/simpleRoute.xml"));
        assertEquals(expected, result);
    }

}
