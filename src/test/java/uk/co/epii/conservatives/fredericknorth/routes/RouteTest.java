package uk.co.epii.conservatives.fredericknorth.routes;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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

}
