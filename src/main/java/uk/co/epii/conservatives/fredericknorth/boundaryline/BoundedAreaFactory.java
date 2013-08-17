package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

/**
 * User: James Robinson
 * Date: 28/07/2013
 * Time: 17:02
 */
public interface BoundedAreaFactory {

    public BoundedArea load(File file);
    public BoundedArea load(Element element);
    public BoundedArea load(Document document);
    public BoundedArea load(String string);

}
