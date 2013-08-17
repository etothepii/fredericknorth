package uk.co.epii.conservatives.fredericknorth.serialization;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 18:55
 */
public class XMLSerializerRegistrar {

    public static void registerToContext(ApplicationContext applicationContext) {
        applicationContext.registerDefaultInstance(XMLSerializer.class, new XMLSerializerImpl());
    }

}
