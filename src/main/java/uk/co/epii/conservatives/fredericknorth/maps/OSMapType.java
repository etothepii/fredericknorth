package uk.co.epii.conservatives.fredericknorth.maps;

import java.io.File;

/**
 * User: James Robinson
 * Date: 18/08/2013
 * Time: 14:25
 */
public enum OSMapType {

    STREET_VIEW(0.4, Double.MAX_VALUE, 1, "StreetView"),
    VECTOR_MAP(0.04, 0.4, 0.4,  "VectorMap"),
    RASTER(0.01, 0.04, 0.04, "Raster"),
    MINI(0, 0.01, 0.01, "Mini");

    private double minScale;
    private double maxScale;
    private double scale;
    private String name;

    OSMapType(double minScale, double maxScale, double imageScale, String name) {
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.scale = imageScale;
        this.name = name;
    }

    public double getScale() {
        return scale;
    }

    public static OSMapType getMapType(double scale) {
        for (OSMapType osMapType : values()) {
            if (scale < osMapType.maxScale && scale >= osMapType.minScale) {
                return osMapType;
            }
        }
        throw new IllegalArgumentException("No map type supports the scale provided");
    }

    public String getName() {
        return name;
    }
}
