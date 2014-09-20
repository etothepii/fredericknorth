package uk.co.epii.conservatives.fredericknorth.reports;

import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroupFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;

/**
 * User: James Robinson
 * Date: 05/08/2013
 * Time: 16:14
 */
public class DwellingCountReportBuilderRegistrar {
    public static void registerToContext(ApplicationContext applicationContext) {
        DwellingGroupFactory dwellingGroupFactory = applicationContext.getDefaultInstance(DwellingGroupFactory.class);
        MapViewGenerator mapViewGenerator = applicationContext.getNamedInstance(MapViewGenerator.class, Keys.PDF_GENERATOR);
        applicationContext.registerDefaultInstance(DwellingCountReportBuilder.class,
                new DwellingCountReportBuilderImpl(dwellingGroupFactory, mapViewGenerator));
    }
}
