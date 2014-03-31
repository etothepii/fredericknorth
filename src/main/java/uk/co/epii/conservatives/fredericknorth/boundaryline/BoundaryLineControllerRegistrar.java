package uk.co.epii.conservatives.fredericknorth.boundaryline;

import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.robertwalpole.DataSet;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/**
 * User: James Robinson
 * Date: 11/08/2013
 * Time: 22:24
 */
public class BoundaryLineControllerRegistrar {

    private static final String BoundaryLineDataDirectoryKey = "BoundaryLineDataDirectory";

    public static void registerToContext(ApplicationContext applicationContext) {
        String boundaryLineDirectory = applicationContext.getNamedInstance(File.class, Keys.DATA_FOLDER).toString() +
                File.separator + applicationContext.getProperty(BoundaryLineDataDirectoryKey);
        Map<BoundedAreaType, DataSet> dataSets = new EnumMap<BoundedAreaType, DataSet>(BoundedAreaType.class);
        for (BoundedAreaType boundedAreaType : BoundedAreaType.values()) {
            if (boundedAreaType.getFileName() == null) continue;
            dataSets.put(boundedAreaType,
                    DataSet.createFromFile(new File(boundaryLineDirectory + boundedAreaType.getFileName())));
        }
        applicationContext.registerDefaultInstance(BoundaryLineController.class, new BoundaryLineControllerImpl(dataSets));

    }

}
