package uk.co.epii.conservatives.fredericknorth.routes;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.awt.*;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 19/07/2013
 * Time: 18:55
 */
public interface RoutableArea extends BoundedArea {

    public List<? extends Route> getRoutes();
    public Collection<? extends DwellingGroup> getUnroutedDwellingGroups();
    public List<? extends DwellingGroup> getDwellingGroups();
    public Element toXml(Document document);
    public void autoGenerate(int targetSize, boolean unroutedOnly);
    public List<? extends Route> proposeRoutes(int targetSize, boolean unroutedOnly);

}
