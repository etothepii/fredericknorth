package uk.co.epii.conservatives.fredericknorth.pdf;

import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * User: James Robinson
 * Date: 26/06/2013
 * Time: 00:14
 */
public interface PDFRenderer {

    public RenderedRoute buildRouteGuide(Route route, File file);
    public Collection<RenderedRoute> buildRoutesGuide(Collection<? extends Route> routes, File file, ProgressTracker progressTracker);
    public Collection<RenderedRoute> buildRoutesGuide(RoutableArea routableArea, File file, ProgressTracker progressTracker);
    public void setMeetingPoints(List<? extends Location> meetingPoints);

}
