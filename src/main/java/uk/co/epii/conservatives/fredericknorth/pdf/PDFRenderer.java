package uk.co.epii.conservatives.fredericknorth.pdf;

import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.routes.Council;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.routes.Ward;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 26/06/2013
 * Time: 00:14
 */
public interface PDFRenderer {

    public void buildRouteGuide(Route route, File file);
    public void buildRoutesGuide(Collection<? extends Route> routes, File file);
    public void buildRoutesGuide(Ward ward, File file);
    public void buildRoutesGuide(Council council, File file);
    public void setMeetingPoints(List<? extends Location> meetingPoints);

}
