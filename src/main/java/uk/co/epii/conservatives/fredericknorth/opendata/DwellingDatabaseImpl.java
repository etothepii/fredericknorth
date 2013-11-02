package uk.co.epii.conservatives.fredericknorth.opendata;

import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.BLPU;
import uk.co.epii.conservatives.williamcavendishbentinck.tables.Postcode;
import uk.co.epii.spencerperceval.extensions.ArrayExtensions;
import uk.co.epii.spencerperceval.util.Groupable;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 15:04
 */
public class DwellingDatabaseImpl implements Dwelling {

    private uk.co.epii.conservatives.williamcavendishbentinck.tables.Dwelling dbDwelling;
    private BLPU blpu;
    private Postcode postcode;
    private String identifier;
    private DwellingGroup dwellingGroup;

    @Override
    public String getIdentifier() {
        if (identifier != null) {
            return identifier;
        }
        return dbDwelling.getVoaAddress();
    }

    @Override
    public DwellingGroup getDwellingGroup() {
        return dwellingGroup;
    }

    @Override
    public char getCouncilTaxBand() {
        return dbDwelling.getCouncilTaxBand();
    }

    @Override
    public void setDwellingGroup(DwellingGroup dwellingGroup) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public void setPoint(Point point) {
        throw new UnsupportedOperationException("This operation is not supported");
    }

    @Override
    public String getName() {
        return getIdentifier();
    }

    @Override
    public Point getPoint() {
        if (blpu != null) {
            return PointExtensions.fromFloat(new Point2D.Float(blpu.getXCoordinate(), blpu.getYCoordinate()));
        }
        else if (postcode != null) {
            return PointExtensions.fromFloat(new Point2D.Float(postcode.getXCoordinate(), postcode.getYCoordinate()));
        }
        return null;
    }
}
