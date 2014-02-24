package uk.co.epii.conservatives.fredericknorth.gui.routableareabuilder;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 22/02/2014
 * Time: 19:08
 */
public class MeetingPointTests {

    private static final String expected =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<MeetingPoint>\n" +
            "  <Name>Some Place</Name>\n" +
            "  <Point>\n" +
            "    <X>345.0</X>\n" +
            "    <Y>123.0</Y>\n" +
            "  </Point>\n" +
            "</MeetingPoint>\n";

    private XMLSerializerImpl xmlSerializer = new XMLSerializerImpl();

    @Test
    public void canDeserializeMeetingPoint() {
        MeetingPoint meetingPoint = MeetingPoint.parse(xmlSerializer.fromString(expected).getDocumentElement());
        assertEquals("Some Place", meetingPoint.getName());
        assertEquals(345, meetingPoint.getPoint().x);
        assertEquals(123, meetingPoint.getPoint().y);
    }

    @Test
    public void canSerializeMeetingPoint() {
        MeetingPoint meetingPoint = new MeetingPoint("Some Place", new Point(345, 123));
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = documentBuilder.newDocument();
        Element meetingPointElt = meetingPoint.toXml(document);
        document.appendChild(meetingPointElt);
        String result = xmlSerializer.toString(document);
        assertEquals(expected, result);
    }

}
