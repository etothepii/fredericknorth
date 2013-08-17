package uk.co.epii.conservatives.fredericknorth.routes;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.maps.*;
import uk.co.epii.conservatives.fredericknorth.opendata.*;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 23/06/2013
 * Time: 01:11
 */
class CouncilImpl implements Council {

    private static final Logger LOG = Logger.getLogger(CouncilImpl.class);

    private final ArrayList<Location> meetingPoints;
    private final HashMap<String, WardImpl> wards;
    private final LocationFactory locationFactory;
    private final int trivialWardCount;
    private final DwellingProcessor dwellingProcessor;
    private final XMLSerializer xmlSerializer;

    CouncilImpl(ArrayList<Location> meetingPoints, HashMap<String, WardImpl> wards, LocationFactory locationFactory,
                int trivialWardCount, DwellingProcessor dwellingProcessor, XMLSerializer xmlSerializer) {
        this.meetingPoints = meetingPoints;
        this.wards = wards;
        this.locationFactory = locationFactory;
        this.trivialWardCount = trivialWardCount;
        this.dwellingProcessor = dwellingProcessor;
        this.xmlSerializer = xmlSerializer;
    }

    public void addWard(WardImpl wardImpl) {
        wards.put(wardImpl.getId(), wardImpl);
    }

    @Override
    public Collection<? extends Ward> getWards() {
        return wards.values();
    }

    @Override
    public Ward getWard(String wardId) {
        return wards.get(wardId);
    }

    @Override
    public List<? extends DwellingGroup> getAllDwellingGroups() {
        List<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
        for (Ward ward : getWards()) {
            if (ward.getDwellingGroups().size() > trivialWardCount) {
                dwellingGroups.addAll(ward.getDwellingGroups());
            }
        }
        return dwellingGroups;
    }

    @Override
    public Element toXml(Document document) {
        Element councilElt = document.createElement("Council");
        Element wardsElt = document.createElement("Wards");
        councilElt.appendChild(wardsElt);
        for (WardImpl ward : wards.values()) {
            wardsElt.appendChild(ward.toXml(document));
        }
        return councilElt;
    }

    @Override
    public List<Location> getMeetingPoints() {
        return meetingPoints;
    }

    @Override
    public Rectangle getUniverse() {
        List<? extends DwellingGroup> allDwellingGroups = getAllDwellingGroups();
        List<Location> meetingPoints = getMeetingPoints();
        List<Location> allLocations = new ArrayList<Location>(meetingPoints.size() + allDwellingGroups.size());
        allLocations.addAll(meetingPoints);
        allLocations.addAll(allDwellingGroups);
        return locationFactory.calculatePaddedRectangle(allLocations);
    }

    @Override
    public void save(File selectedFile) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = documentBuilder.newDocument();
        Element xml = toXml(document);
        document.appendChild(xml);
        String toWrite = xmlSerializer.toString(document);
        try {
            FileUtils.writeStringToFile(selectedFile, toWrite);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void load(ApplicationContext applicationContext, File selectedFile) {
        Document council = xmlSerializer.fromFile(selectedFile);
        Element councilElt = council.getDocumentElement();
        if (!councilElt.getTagName().equals("Council")) throw new IllegalArgumentException(
                "The element provided is not a Council Tag");
        Element wardsElt = (Element)councilElt.getElementsByTagName("Wards").item(0);
        NodeList routesNodeList = wardsElt.getElementsByTagName("Ward");
        for (int i = 0; i < routesNodeList.getLength(); i++) {
            Element wardElt = (Element)routesNodeList.item(i);
            String wardId = wardElt.getElementsByTagName("Id").item(0).getTextContent();
            Ward ward = getWard(wardId);
            ward.load(applicationContext, wardElt);
        }
    }

    @Override
    public void autoGenerate(int targetSize, boolean unroutedOnly) {
        for (Ward ward : wards.values()) {
            LOG.info("Auto Generating: " + ward.getName());
            ward.autoGenerate(targetSize, unroutedOnly);
        }
    }

}
