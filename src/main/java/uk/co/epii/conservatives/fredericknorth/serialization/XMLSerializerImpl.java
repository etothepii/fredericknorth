package uk.co.epii.conservatives.fredericknorth.serialization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 18:52
 */
public class XMLSerializerImpl implements XMLSerializer {

    DocumentBuilderFactory dbFactory;
    DocumentBuilder documentBuilder;
    TransformerFactory transformerFactory;
    Transformer transformer;

    public XMLSerializerImpl() {
        dbFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = dbFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        transformerFactory = TransformerFactory.newInstance();
        try {
            transformer = transformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    }

    @Override
    public String toString(Document document) {
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        try {
            transformer.transform(domSource, result);
        }
        catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    @Override
    public Document fromFile(File file) {
        try {
            return documentBuilder.parse(file);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Document fromString(String s) {
        try {
            return documentBuilder.parse(new ByteArrayInputStream(s.getBytes()));
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSingleOrDefault(Element element, String subTag) {
        NodeList nodeList = element.getElementsByTagName(subTag);
        switch (nodeList.getLength()) {
            case 0: return null;
            case 1: return nodeList.item(0).getTextContent();
        }
        throw new RuntimeException("There are more than one element of Tag: " + subTag);
    }
}
