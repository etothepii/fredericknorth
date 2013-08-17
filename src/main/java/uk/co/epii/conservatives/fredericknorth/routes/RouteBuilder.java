package uk.co.epii.conservatives.fredericknorth.routes;

import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jrrpl
 * Date: 23/06/2013
 * Time: 15:59
 */
public interface RouteBuilder {

    public List<List<DwellingGroup>> proposeRoutes(Collection<? extends DwellingGroup> dwellingGroups, int min, int max);

}
