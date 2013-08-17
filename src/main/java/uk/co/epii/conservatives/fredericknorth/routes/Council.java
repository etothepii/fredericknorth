package uk.co.epii.conservatives.fredericknorth.routes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 23/06/2013
 * Time: 01:12
 */
public interface Council {

    public Collection<? extends Ward> getWards();
    public Ward getWard(String wardId);
    public List<? extends DwellingGroup> getAllDwellingGroups();
    public Element toXml(Document document);
    public List<? extends Location> getMeetingPoints();
    public Rectangle getUniverse();
    public void save(File selectedFile);
    public void load(ApplicationContext applicationContext, File selectedFile);
    public void autoGenerate(int targetSize, boolean unroutedOnly);
}
