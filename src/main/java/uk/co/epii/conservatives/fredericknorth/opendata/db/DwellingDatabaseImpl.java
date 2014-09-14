package uk.co.epii.conservatives.fredericknorth.opendata.db;

import uk.co.epii.conservatives.fredericknorth.opendata.Dwelling;
import uk.co.epii.politics.williamcavendishbentinck.tables.DeliveryPointAddress;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 21:16
 */
public class DwellingDatabaseImpl implements Dwelling {

    private char councilTaxBand;
    private String name;
    private Point point;

    public DwellingDatabaseImpl(char councilTaxBand, String name, Point point) {
        this.councilTaxBand = councilTaxBand;
        this.name = name;
        this.point = point;
    }

    @Override
    public char getCouncilTaxBand() {
        return councilTaxBand;
    }

    public void setCouncilTaxBand(char councilTaxBand) {
        this.councilTaxBand = councilTaxBand;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }


}
