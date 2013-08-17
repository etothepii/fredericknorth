package uk.co.epii.conservatives.fredericknorth.utilities;

import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: jrrpl
 * Date: 22/06/2013
 * Time: 23:40
 */
public class ResourceHelper {

    public static Set<String> loadLinesAsSet(URL resourceLocation) {
        Set<String> uniquePostcodes = new HashSet<String>();
        for (String line : new BufferedResourceReader(resourceLocation)) {
            uniquePostcodes.add(line);
        }
        return uniquePostcodes;
    }

    public static List<String> loadLinesAsList(URL resourceLocation) {
        List<String> uniquePostcodes = new ArrayList<String>();
        for (String line : new BufferedResourceReader(resourceLocation)) {
            uniquePostcodes.add(line);
        }
        return uniquePostcodes;
    }

    public static String readResource(URL resource) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : new BufferedResourceReader(resource)) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        if (stringBuilder.length() > 0) stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public static String toString(Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(domSource, result);
        return writer.toString();
    }
}
