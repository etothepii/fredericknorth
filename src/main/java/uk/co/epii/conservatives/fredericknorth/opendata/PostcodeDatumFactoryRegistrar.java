package uk.co.epii.conservatives.fredericknorth.opendata;

import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 02:34
 */
public class PostcodeDatumFactoryRegistrar {

    public static void registerToContext(ApplicationContext applicationContext) {
        applicationContext.registerDefaultInstance(PostcodeDatumFactory.class, new PostcodeDatumFactoryImpl());
    }
}
