package uk.co.epii.conservatives.fredericknorth.utilities;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: James Robinson
 * Date: 21/06/2013
 * Time: 18:02
 */
public interface ApplicationContext {

    public String getProperty(String property);
    public <T> T getDefaultInstance(Class<T> clazz);
    public <T> T getNamedInstance(Class<T> clazz, String name);
    public boolean isDefaultInstanceLoaded(Class clazz);
    public <T> void registerDefaultInstance(Class<T> clazz, T toRegister);
    public <T> void registerNamedInstance(Class<T> clazz, String name, T toRegister);
    public boolean isNamedInstanceLoaded(Class clazz, String name);

}
