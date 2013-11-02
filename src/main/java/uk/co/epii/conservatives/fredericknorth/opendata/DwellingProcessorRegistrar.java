package uk.co.epii.conservatives.fredericknorth.opendata;

import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 23:40
 */
public class DwellingProcessorRegistrar {

    private static final Logger LOG = LoggerFactory.getLogger(DwellingProcessorRegistrar.class);

    private static final String DwellingsDataDirectoryKey = "DwellingsDataDirectory";

    private static final Pattern dwellingsRegex =
            Pattern.compile("([^,]*), (.*), (.*) ([A-Z][A-Z0-9]* [0-9][A-Z][A-Z])~([A-I])~");
    private static final Pattern textFileRexgex =
            Pattern.compile("^(.*)\\.[Tt][xX][tT]$");

    private static String dwellingGroupsCommonEnding = null;
    private static HashMap<String, HashMap<String, DwellingGroupImpl>> activeDwellingGroups;
    private static HashMap<String, HashMap<String, DwellingGroupImpl>> allDwellingGroups;
    private static PostcodeDatumFactory postcodeDatumFactory;

    public static void registerToContext(ApplicationContext applicationContext, @NotNull ProgressTracker progressTracker) {
        registerToContext(applicationContext, progressTracker, new File(
                applicationContext.getNamedInstance(File.class, Keys.DATA_FOLDER).toString() +
                        File.separator + applicationContext.getProperty(DwellingsDataDirectoryKey)));
    }

    public static void registerToContext(ApplicationContext applicationContext,
                                         @NotNull ProgressTracker progressTracker, Object data) {
        allDwellingGroups = new HashMap<String, HashMap<String, DwellingGroupImpl>>();
        postcodeDatumFactory = applicationContext.getDefaultInstance(PostcodeDatumFactory.class);
        LOG.info("Loading dwelling data");
        if (data instanceof File) {
            loadDwellingsFolder((File)data, progressTracker);
        }
        else if (data instanceof InputStream) {
            loadDwellingsFromInputStream((InputStream)data);
        }
        LOG.info("Data loaded successfully");
        DwellingProcessorImpl dwellingProcessor = new DwellingProcessorImpl(allDwellingGroups);
        applicationContext.registerDefaultInstance(DwellingProcessor.class, dwellingProcessor);
    }

    private static void loadDwellingsFolder(File dwellingsFolder, ProgressTracker progressTracker) {
        LOG.info("dwellingsFolder: {}", dwellingsFolder);
        String[] textFiles = dwellingsFolder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return textFileRexgex.matcher(name).matches();
            }
        });
        progressTracker.startSubsection(textFiles.length);
        for (String file : textFiles) {
            progressTracker.setMessage(String.format("Loading %s", file));
            loadDwellingsFromTextFile(new File(dwellingsFolder.toString() + File.separator + file));
            progressTracker.increment();
        }
    }

    private static void loadDwellingsFromTextFile(File file) {
        activeDwellingGroups = new HashMap<String, HashMap<String, DwellingGroupImpl>>();
        dwellingGroupsCommonEnding = null;
        loadTextFile(file);
        if (dwellingGroupsCommonEnding.length() > 0) {
            removeCommonEnding();
        }
        addNewlyLoadedDwellings();
    }

    static void loadDwellingsFromInputStream(InputStream inputStream) {
        activeDwellingGroups = new HashMap<String, HashMap<String, DwellingGroupImpl>>();
        dwellingGroupsCommonEnding = null;
        loadTextStream(inputStream);
        if (dwellingGroupsCommonEnding.length() > 0) {
            removeCommonEnding();
        }
        addNewlyLoadedDwellings();
    }

    private static void addNewlyLoadedDwellings() {
        for (Map.Entry<String, HashMap<String, DwellingGroupImpl>> entry : activeDwellingGroups.entrySet()) {
            HashMap<String, DwellingGroupImpl> dwellingGroups = allDwellingGroups.get(entry.getKey());
            if (dwellingGroups != null) {
                for (Map.Entry<String, DwellingGroupImpl> reentry : entry.getValue().entrySet()) {
                    DwellingGroupImpl dwellingGroup = dwellingGroups.get(reentry.getKey());
                    if (dwellingGroup != null) {
                        for (Dwelling dwelling : reentry.getValue().getDwellings()) {
                            dwellingGroup.add(dwelling);
                        }
                    }
                    else {
                        dwellingGroups.put(reentry.getKey(), reentry.getValue());
                    }
                }
            }
            else {
                allDwellingGroups.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private static void loadTextFile(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            readReader(fileReader);
            fileReader.close();
        }
        catch (IOException ioe) {
            LOG.error("Problem reading file: {}", file.toString());
        }
    }

    private static void readReader(Reader fileReader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String in;
        while ((in = bufferedReader.readLine()) != null) {
            processDwelling(in);
        }
        bufferedReader.close();
    }

    private static void loadTextStream(InputStream inputStream) {
        try {
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            readReader(fileReader);
            fileReader.close();
        }
        catch (IOException ioe) {
            LOG.error("Problem reading InputStream");
        }
    }

    private static void processDwelling(String dwelling) {
        Matcher matcher = dwellingsRegex.matcher(dwelling);
        if (!matcher.find()) {
            return;
        }
        String dwellingName = matcher.group(1);
        String dwellingGroupName = matcher.group(2);
        String county = matcher.group(3);
        if (dwellingGroupsCommonEnding == null) {
            dwellingGroupsCommonEnding = dwellingGroupName;
        }
        else {
            while (dwellingGroupsCommonEnding.length() > 0 && !dwellingGroupName.endsWith(dwellingGroupsCommonEnding)) {
                String[] parts = dwellingGroupsCommonEnding.split(", *", 2);
                if (parts.length == 1) {
                    dwellingGroupsCommonEnding = "";
                }
                else {
                    dwellingGroupsCommonEnding = parts[1];
                }
            }
        }
        String postCode = matcher.group(4);
        String band = matcher.group(5);
        DwellingGroupImpl dwellingGroup = getOrCreateDwellingGroup(postCode, dwellingGroupName);
        dwellingGroup.add(new DwellingImpl(dwellingName, band.charAt(0), dwellingGroup));
    }

    private static void removeCommonEnding() {
        Pattern commonEndingRemover = Pattern.compile("(.*)(, *|^)".concat(dwellingGroupsCommonEnding).concat("$"));
        for (HashMap<String, DwellingGroupImpl> map : activeDwellingGroups.values()) {
            for (DwellingGroupImpl dwellingGroup : map.values()) {
                removeCommonEnding(dwellingGroup, commonEndingRemover);
            }
        }
    }

    private static void removeCommonEnding(DwellingGroupImpl dwellingGroup, Pattern commonEndingRemover) {
        Matcher matcher = commonEndingRemover.matcher(dwellingGroup.getName());
        if (matcher.find()) {
            dwellingGroup.setUniquePart(matcher.group(1));
        }
        else {
            LOG.warn("Matcher failed on: '" + dwellingGroup.getName() + "'");
        }
    }

    private static DwellingGroupImpl getOrCreateDwellingGroup(String postcode, String dwellingGroupName) {
        HashMap<String, DwellingGroupImpl> dwellingGroups = activeDwellingGroups.get(postcode);
        if (dwellingGroups == null) {
            dwellingGroups = new HashMap<String, DwellingGroupImpl>();
            activeDwellingGroups.put(postcode, dwellingGroups);
        }
        DwellingGroupImpl dwellingGroup = dwellingGroups.get(dwellingGroupName);
        if (dwellingGroup == null) {
            dwellingGroup = new DwellingGroupImpl(dwellingGroupName, null,
                    postcodeDatumFactory.getInstance(postcode));
            dwellingGroups.put(dwellingGroupName, dwellingGroup);
        }
        return dwellingGroup;
    }
}
