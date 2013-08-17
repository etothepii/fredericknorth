package uk.co.epii.conservatives.fredericknorth.boundaryline;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

/**
 * User: James Robinson
 * Date: 29/07/2013
 * Time: 01:01
 */
public class BoundedAreaFactoryRegistrar {

    public static void registerToContext(ApplicationContext applicationContext) {
        XMLSerializer xmlSerializer = applicationContext.getDefaultInstance(XMLSerializer.class);
        applicationContext.registerDefaultInstance(
                BoundedAreaFactory.class, new BoundedAreaFactoryImpl(xmlSerializer));
    }

}
