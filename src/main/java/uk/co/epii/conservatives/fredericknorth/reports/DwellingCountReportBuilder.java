package uk.co.epii.conservatives.fredericknorth.reports;

import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 05/08/2013
 * Time: 15:56
 */
public interface DwellingCountReportBuilder {

    public Map<BoundedArea, int[]> countDwellings(BoundedArea boundedArea);
    public List<BoundedArea> flatten(BoundedArea boundedArea);
    public BufferedImage getImage(BoundedArea masterArea, List<BoundedArea> boundedAreas,
                          Map<BoundedAreaType, Color> colours, Dimension dimension);
}
