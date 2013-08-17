package uk.co.epii.conservatives.fredericknorth.opendata;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.BufferedResourceReader;
import uk.co.epii.conservatives.fredericknorth.maps.OSMapLocator;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 23:39
 */
public class PostcodeProcessorRegistrar {

    private static final Logger LOG = Logger.getLogger(PostcodeProcessorRegistrar.class);

    private static final int X_COLUMN = 2;
    private static final int Y_COLUMN = 3;
    private static final int WARD_COLUMN = 9;

    private static final String TrivialDwellingCountKey = "TrivialDwellingCount";
    private static final String PostcodeDataDirectoryKey = "PostcodeDataDirectory";

    private static final Pattern postcodeFromQuotedRegex = Pattern.compile("\"([A-Z][A-Z0-9]*) *([0-9][A-Z][A-Z])\"");

    private static PostcodeDatumFactory postcodeDatumFactory;
    private static HashMap<String, HashSet<String>> wards;

    public static void registerToContext(ApplicationContext applicationContext) {
        File dataFolder = applicationContext.getNamedInstance(File.class, Keys.DATA_FOLDER);
        registerToContext(applicationContext, dataFolder,
                Integer.parseInt(applicationContext.getProperty(TrivialDwellingCountKey)));
    }

    public static void registerToContext(ApplicationContext applicationContext, Object data, int trivialPostcodeCount) {
        applicationContext.getDefaultInstance(DwellingProcessor.class);
        postcodeDatumFactory = applicationContext.getDefaultInstance(PostcodeDatumFactory.class);
        wards = new HashMap<String, HashSet<String>>();
        LOG.info("Loading postcode Data");
        if (data instanceof File) {
            String dataFolder = data.toString() + File.separator +
                    applicationContext.getProperty(PostcodeDataDirectoryKey);
            loadPostcodeData(dataFolder, postcodeDatumFactory.getRequiredPostcodePrefices());
        }
        else if (data instanceof InputStream) {
            InputStreamReader reader = new InputStreamReader((InputStream)data);
            readReader(reader);
            try {
                reader.close();
            }
            catch (IOException ioe) {
                LOG.error(ioe);
            }
        }
        else {
            throw new IllegalArgumentException(String.format("Data sources of type %s are not supported", data.getClass()));
        }
        LOG.info("Data loaded successfully");
        removeWardsWithTrivialPostcodes(trivialPostcodeCount);
        applicationContext.registerDefaultInstance(PostcodeProcessor.class, new PostcodeProcessorImpl(
                applicationContext.getDefaultInstance(OSMapLocator.class), wards, postcodeDatumFactory
        ));
    }

    private static void removeWardsWithTrivialPostcodes(int trivial) {
        ArrayList<String> trivialWards = new ArrayList<String>();
        for (String wardId : wards.keySet()) {
            if (wards.get(wardId).size() <= trivial) {
                trivialWards.add(wardId);
            }
        }
        for (String wardId : trivialWards){
            wards.remove(wardId);
        }
    }

    private static void loadPostcodeData(String postcodeDataFolder, String[] postcodePrefices) {
        for (String prefix : postcodePrefices) {
            File file = new File(String.format("%s%s%s", postcodeDataFolder, prefix, ".csv"));
            try {
                FileReader fileReader = new FileReader(file);
                readReader(fileReader);
                fileReader.close();
            }
            catch (IOException ioe) {
                LOG.error(ioe);
                throw new RuntimeException(ioe);
            }
        }
    }

    private static void readReader(Reader fileReader) {
        try {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String in;
            while ((in = bufferedReader.readLine()) != null) {
                processPostcode(in);
            }
            bufferedReader.close();
        }
        catch (IOException ioe) {
            LOG.error(ioe);
            throw new RuntimeException(ioe);
        }
    }

    private static void processPostcode(String postcodeData) {
        String[] columns = postcodeData.split(",");
        String postcode = parsePostcode(columns[0]);
        PostcodeDatum postcodeDatum = postcodeDatumFactory.getInstance(postcode);
        if (postcodeDatum == null) return;
        if (postcodeDatum.getPoint() != null) throw new RuntimeException(
                "The location has already been set to " + postcodeDatum.getPoint() + " for " + postcode);
        postcodeDatum.setPoint(new Point(Integer.parseInt(columns[X_COLUMN]), Integer.parseInt(columns[Y_COLUMN])));
        if (postcodeDatum.getWardCode() != null) throw new RuntimeException(
                "The ward has already been set to " + postcodeDatum.getWardCode() + " for " + postcode);
        postcodeDatum.setWardCode(deQuote(columns[WARD_COLUMN]));
        HashSet<String> ward = getOrCreateWard(postcodeDatum.getWardCode());
        ward.add(postcode);
    }

    private static HashSet<String> getOrCreateWard(String wardCode) {
        HashSet<String> ward = wards.get(wardCode);
        if (ward == null) {
            ward = new HashSet<String>();
            wards.put(wardCode, ward);
        }
        return ward;
    }

    private static String deQuote(String column) {
        return column.substring(1, column.length() - 1);
    }

    private static String parsePostcode(String quotedPostcode) {
        Matcher matcher = postcodeFromQuotedRegex.matcher(quotedPostcode);
        if (!matcher.matches()) throw new IllegalArgumentException("The provided postcode is invalid: \'" + quotedPostcode + "\'");
        StringBuilder stringBuilder = new StringBuilder(8);
        stringBuilder.append(matcher.group(1));
        stringBuilder.append(" ");
        stringBuilder.append(matcher.group(2));
        return stringBuilder.toString();
    }
}
