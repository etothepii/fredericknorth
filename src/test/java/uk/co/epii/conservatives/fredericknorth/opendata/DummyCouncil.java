package uk.co.epii.conservatives.fredericknorth.opendata;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.routes.Council;
import uk.co.epii.conservatives.fredericknorth.routes.Ward;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 19:35
 */
public class DummyCouncil implements Council {

    private HashMap<String, Ward> wards = new HashMap<String, Ward>();

    @Override
    public Collection<? extends Ward> getWards() {
        return wards.values();
    }

    public void addWard(String wardId, Ward ward) {
        wards.put(wardId, ward);
    }

    @Override
    public Ward getWard(String wardId) {
        return wards.get(wardId);
    }

    @Override
    public List<? extends DwellingGroup> getAllDwellingGroups() {
        throw new UnsupportedOperationException("This method is not supported in a DummyCouncil");
    }

    @Override
    public Element toXml(Document document) {
        throw new UnsupportedOperationException("This method is not supported in a DummyCouncil");
    }

    @Override
    public List<? extends Location> getMeetingPoints() {
        throw new UnsupportedOperationException("This method is not supported in a DummyCouncil");
    }

    @Override
    public Rectangle getUniverse() {
        throw new UnsupportedOperationException("This method is not supported in a DummyCouncil");
    }

    @Override
    public void save(File selectedFile) {
        throw new UnsupportedOperationException("This method is not supported in a DummyCouncil");
    }

    @Override
    public void load(ApplicationContext applicationContext, File selectedFile) {
        throw new UnsupportedOperationException("This method is not supported in a DummyCouncil");
    }

    @Override
    public void autoGenerate(int targetSize, boolean unroutedOnly) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
