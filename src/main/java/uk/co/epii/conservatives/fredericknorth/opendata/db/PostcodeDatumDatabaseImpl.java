package uk.co.epii.conservatives.fredericknorth.opendata.db;

import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.Dwelling;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatum;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 03/11/2013
 * Time: 11:32
 */
public class PostcodeDatumDatabaseImpl implements PostcodeDatum {

    private Postcode postcode;
    private Map<String, DwellingGroupDatabaseImpl> dwellingGroups;

    @Override
    public Iterable<Dwelling> getDwellings() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int[] getCouncilBandCount() {
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return postcode.getPostcode();
    }

    @Override
    public Point getPoint() {
        return PointExtensions.fromFloat(new Point2D.Float(postcode.getXCoordinate(), postcode.getYCoordinate()));
    }
}
