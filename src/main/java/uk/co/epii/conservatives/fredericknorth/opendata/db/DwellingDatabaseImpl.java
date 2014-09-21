package uk.co.epii.conservatives.fredericknorth.opendata.db;

import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.politics.williamcavendishbentinck.tables.DeliveryPointAddress;

import java.awt.*;

/**
 * User: James Robinson
 * Date: 02/11/2013
 * Time: 21:16
 */
public class DwellingDatabaseImpl implements Location {

    private String name;
    private Point point;
    private DeliveryPointAddress deliveryPointAddress;

    public DwellingDatabaseImpl(String name, Point point, DeliveryPointAddress deliveryPointAddress) {
        this.name = name;
        this.point = point;
        this.deliveryPointAddress = deliveryPointAddress;
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

    public DeliveryPointAddress getDeliveryPointAddress() {
        return deliveryPointAddress;
    }

    public void setDeliveryPointAddress(DeliveryPointAddress deliveryPointAddress) {
        this.deliveryPointAddress = deliveryPointAddress;
    }
}
