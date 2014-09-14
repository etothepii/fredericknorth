package uk.co.epii.conservatives.fredericknorth.opendata.db;

import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.Dwelling;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatum;
import uk.co.epii.politics.williamcavendishbentinck.tables.Postcode;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 03/11/2013
 * Time: 11:32
 */
public class PostcodeDatumDatabaseImpl implements PostcodeDatum {

    private Postcode postcode;
    private Map<String, DwellingGroupDatabaseImpl> dwellingGroups;

    public PostcodeDatumDatabaseImpl(Postcode postcode, Map<String, DwellingGroupDatabaseImpl> dwellingGroups) {
        this.postcode = postcode;
        this.dwellingGroups = dwellingGroups;
    }

    @Override
    public Iterable<Dwelling> getDwellings() {
        Collection<Dwelling> all = new ArrayList<Dwelling>(size());
        for (DwellingGroupDatabaseImpl dwellingGroup : dwellingGroups.values()) {
            for (Dwelling dwelling : dwellingGroup.getDwellings()) {
                all.add(dwelling);
            }
        }
        return all;
    }

    @Override
    public int size() {
        int size = 0;
        for (DwellingGroupDatabaseImpl dwellingGroup : dwellingGroups.values()) {
            size += dwellingGroup.size();
        }
        return size;
    }

    private static int getArrayIndexForCouncilBand(char band) {
        int councilTaxBandIndex = band - 65;
        if (Math.abs(councilTaxBandIndex - 4) > 4) throw new IllegalArgumentException("Only bands A through I are supported: " + band);
        return councilTaxBandIndex;
    }

    @Override
    public int[] getCouncilBandCount() {
        int[] councilTaxBand = new int[9];
        for (Dwelling dwelling : getDwellings()) {
            councilTaxBand[getArrayIndexForCouncilBand(dwelling.getCouncilTaxBand())]++;
        }
        return councilTaxBand;
    }

    @Override
    public String getName() {
        return postcode.getPostcode();
    }

    @Override
    public Point getPoint() {
        return PointExtensions.fromFloat(new Point2D.Float(postcode.getXCoordinate(), postcode.getYCoordinate()));
    }

    public Map<String, DwellingGroupDatabaseImpl> getDwellingGroups() {
        return dwellingGroups;
    }
}
