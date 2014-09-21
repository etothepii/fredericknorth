package uk.co.epii.conservatives.fredericknorth.opendata;

import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingGroupFactoryDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 02:34
 */
public class DwellingGroupFactoryRegistrar {

    public static void registerToContext(ApplicationContext applicationContext) {
        applicationContext.registerDefaultInstance(DwellingGroupFactory.class, new DwellingGroupFactoryDatabaseImpl());
    }
}
