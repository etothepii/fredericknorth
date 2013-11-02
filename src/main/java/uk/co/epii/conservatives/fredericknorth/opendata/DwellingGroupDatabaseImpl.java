package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.spencerperceval.util.Group;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 15:18
 */
public class DwellingGroupDatabaseImpl implements DwellingGroup, Group<DwellingDatabaseImpl> {

    private String displayName;

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        if (displayName == null) {
            return getIdentifierSummary();
        }
        return displayName;
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Point getPoint() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setUniquePart(String uniquePart) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPoint(Point point) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PostcodeDatum getPostcode() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<? extends Dwelling> getDwellings() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void load(ApplicationContext applicationContext, Element dwellingGroupElt) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getIdentifierSummary() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUniquePart() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(DwellingGroup dwellingGroup) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
