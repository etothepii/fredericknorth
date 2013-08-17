package uk.co.epii.conservatives.fredericknorth.routes;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.BufferedResourceReader;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.maps.LocationFactory;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingProcessor;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeProcessor;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import java.awt.*;
import java.net.URL;
import java.util.*;

/**
 * User: James Robinson
 * Date: 23/06/2013
 * Time: 01:14
 */
public class CouncilRegistrar {

    private static final Logger LOG = Logger.getLogger(CouncilRegistrar.class);

    private static final String WardsListKey = "WardsList";
    private static final String MeetingPointsKey = "MeetingPoints";
    private static final String TrivialDwellingCountKey = "TrivialDwellingCount";

    private static PostcodeProcessor postcodeProcessor;
    private static ArrayList<Location> meetingPoints;
    private static HashMap<String, WardImpl> wards;
    private static LocationFactory locationFactory;
    private static DwellingProcessor dwellingProcessor;

    public static void registerToContext(ApplicationContext applicationContext) {
        registerToContext(applicationContext,
                CouncilImpl.class.getResource(applicationContext.getProperty(WardsListKey)),
                CouncilImpl.class.getResource(applicationContext.getProperty(MeetingPointsKey)));
    }

    public static void registerToContext(ApplicationContext applicationContext,
                                         URL wardsResourceLocation, URL meetingPointsResourceLocation) {
        meetingPoints = new ArrayList<Location>();
        wards = new HashMap<String, WardImpl>();
        postcodeProcessor = applicationContext.getDefaultInstance(PostcodeProcessor.class);
        dwellingProcessor = applicationContext.getDefaultInstance(DwellingProcessor.class);
        locationFactory = applicationContext.getDefaultInstance(LocationFactory.class);
        LOG.debug("Loading wards");
        loadWards(wardsResourceLocation);
        LOG.debug("Loading meeting points");
        loadMeetingPoints(meetingPointsResourceLocation);
        LOG.debug("Load complete");
        applicationContext.registerDefaultInstance(Council.class, new CouncilImpl(meetingPoints, wards,
                applicationContext.getDefaultInstance(LocationFactory.class),
                Integer.parseInt(applicationContext.getProperty(TrivialDwellingCountKey)), dwellingProcessor,
                applicationContext.getDefaultInstance(XMLSerializer.class)));
    }

    private static void loadMeetingPoints(URL meetingPointsResourceLocation) {
        for (String line : new BufferedResourceReader(meetingPointsResourceLocation)) {
            processMeetingPointLine(line);
        }
    }

    private static void processMeetingPointLine(String meetingPoint) {
        String[] parts = meetingPoint.split(",");
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        meetingPoints.add(locationFactory.getInstance(parts[0], new Point(x, y)));
    }

    private static void loadWards(URL wardsResourceLocation) {
        for (String line : new BufferedResourceReader(wardsResourceLocation)) {
            processWardLine(line);
        }
    }

    private static void processWardLine(String line) {
        String[] parts = line.split("~");
        String name = parts[0];
        String wardId = parts[1];
        java.util.List<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
        for (String postcode : postcodeProcessor.getWard(wardId)) {
            dwellingGroups.addAll(dwellingProcessor.getDwellingGroups(postcode));
        }
        wards.put(wardId, new WardImpl(name, dwellingGroups, wardId));
    }

}
