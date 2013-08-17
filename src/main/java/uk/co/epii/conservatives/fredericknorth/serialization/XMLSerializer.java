package uk.co.epii.conservatives.fredericknorth.serialization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 18:51
 */
public interface XMLSerializer {

    public String toString(Document xmlDocument);
    public Document fromFile(File file);
    public Document fromString(String s);
    public String getSingleOrDefault(Element element, String subTag);

}
