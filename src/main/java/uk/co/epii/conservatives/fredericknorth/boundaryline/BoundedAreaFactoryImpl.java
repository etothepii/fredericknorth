package uk.co.epii.conservatives.fredericknorth.boundaryline;

import com.sun.tools.javac.jvm.ByteCodes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import java.io.File;

/**
 * User: James Robinson
 * Date: 28/07/2013
 * Time: 17:04
 */
class BoundedAreaFactoryImpl implements BoundedAreaFactory {

    private XMLSerializer xmlSerializer;

    BoundedAreaFactoryImpl(XMLSerializer xmlSerializer) {
        this.xmlSerializer = xmlSerializer;
    }

    @Override
    public BoundedArea load(File file) {
        return load(xmlSerializer.fromFile(file));
    }

    @Override
    public BoundedArea load(BoundedArea parent, Element element) {
        return DefaultBoundedArea.load(parent, element);
    }

    @Override
    public BoundedArea load(Document document) {
        return load(null, document.getDocumentElement());
    }

    @Override
    public BoundedArea load(String string) {
        return load(xmlSerializer.fromString(string));
    }
}
