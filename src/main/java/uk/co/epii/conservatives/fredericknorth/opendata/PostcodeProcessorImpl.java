package uk.co.epii.conservatives.fredericknorth.opendata;

import uk.co.epii.conservatives.fredericknorth.maps.OSMap;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapLocator;

import java.awt.*;
import java.util.*;

/**
 * User: James Robinson
 * Date: 21/06/2013
 * Time: 19:17
 */

class PostcodeProcessorImpl implements PostcodeProcessor {

    private final OSMapLocator osMapLocator;
    private final HashMap<String, HashSet<String>> wards;
    private final PostcodeDatumFactory postcodeDatumFactory;

    PostcodeProcessorImpl(OSMapLocator osMapLocator, HashMap<String, HashSet<String>> wards,
                          PostcodeDatumFactory postcodeDatumFactory) {
        this.osMapLocator = osMapLocator;
        this.wards = wards;
        this.postcodeDatumFactory = postcodeDatumFactory;
    }

    @Override
    public int getDwellingCount(String postcode) {
        PostcodeDatum datum = postcodeDatumFactory.getInstance(postcode);
        if (datum == null) {
            throw new IllegalArgumentException("Unknown postcode " + postcode);
        }
        return datum.getDwellingCount();
    }

    @Override
    public int[] getCouncilBandCount(String postcode) {
        PostcodeDatum datum = postcodeDatumFactory.getInstance(postcode);
        if (datum == null) {
            throw new IllegalArgumentException("Unknown postcode " + postcode);
        }
        return datum.getCouncilBandCount();
    }

    @Override
    public Point getLocation(String postcode) {
        PostcodeDatum datum = postcodeDatumFactory.getInstance(postcode);
        if (datum == null) {
            throw new IllegalArgumentException("Unknown postcode " + postcode);
        }
        return datum.getPoint();
    }

    @Override
    public String getAdminWardId(String postcode) {
        PostcodeDatum datum = postcodeDatumFactory.getInstance(postcode);
        if (datum == null) {
            throw new IllegalArgumentException("Unknown postcode " + postcode);
        }
        return datum.getWardCode();
    }

    @Override
    public OSMap getContainingMap(String postcode) {
        Point location = getLocation(postcode);
        if (location == null) return null;
        return osMapLocator.getMap(location);
    }

    @Override
    public Set<OSMap> getContainingMaps(Collection<String> postcodes) {
        HashSet<OSMap> containingMaps = new HashSet<OSMap>();
        for (String postcode : postcodes) {
            OSMap containingMap = getContainingMap(postcode);
            if (containingMap != null)
            containingMaps.add(containingMap);
        }
        return containingMaps;
    }

    @Override
    public Set<String> getWard(String wardId) {
        return wards.get(wardId);
    }

    @Override
    public Set<String> getWards() {
        return wards.keySet();
    }
}
