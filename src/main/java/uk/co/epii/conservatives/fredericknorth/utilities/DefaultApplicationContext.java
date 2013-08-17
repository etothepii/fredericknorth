package uk.co.epii.conservatives.fredericknorth.utilities;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * User: James Robinson
 * Date: 11/07/2013
 * Time: 21:56
 */
public final class DefaultApplicationContext implements ApplicationContext {

    private static Logger LOG = Logger.getLogger(DefaultApplicationContext.class);

    private static final String FilenamePropertyRegexKey = "FilenamePropertyRegex";

    public static final String DEFAULT_CONFIG_LOCATION = "/config.properties";

    private final HashMap<Class<?>, Object> registeredInstances;
    private final HashMap<Class<?>, HashMap<String, Object>> namedInstances;

    public DefaultApplicationContext(String propertiesResourceLocation) {
        loadProperties(propertiesResourceLocation);
        registeredInstances = new HashMap<Class<?>, Object>();
        namedInstances = new HashMap<Class<?>, HashMap<String, Object>>();
    }

    private Properties properties;

    private void loadProperties(String propertiesResourceLocation) {
        InputStream inputStream = null;
        LOG.debug("loadProperties");
        try {
            LOG.debug("loadPropertiesSafe");
            inputStream = ApplicationContext.class.getResourceAsStream(propertiesResourceLocation);
            LOG.debug("loaded strings.properties file");
            properties = new Properties();
            properties.load(inputStream);
            LOG.debug("properties successfully loaded");
            makeFileSeparatorsOperatingSystemSpecific();
        }
        catch (IOException ioe) {
            LOG.error("The impossible has occurred and the strings.properties file has failed to load");
            throw new RuntimeException(ioe);
        }
        finally {
            closeOut(inputStream);
        }
    }

    private void makeFileSeparatorsOperatingSystemSpecific() {
        if (!File.separator.equals("/")) {
            Pattern fileSystemKeyRegex = Pattern.compile(getProperty(FilenamePropertyRegexKey));
            for (String key : properties.stringPropertyNames()) {
                if (fileSystemKeyRegex.matcher(key).matches()) {
                    properties.setProperty(key, properties.getProperty(key).replace("/", File.separator));
                }
            }
        }
    }

    private void closeOut(InputStream inputStream) {
        LOG.debug("closeOut");
        try {
            inputStream.close();
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    @Override
    public <T> T getDefaultInstance(Class<T> clazz) {
        if (registeredInstances.containsKey(clazz)) {
            return (T)registeredInstances.get(clazz);
        }
        throw new IllegalArgumentException("No item has been registered of class: " + clazz.toString());
    }

    @Override
    public <T> T getNamedInstance(Class<T> clazz, String name) {
        if (!namedInstances.containsKey(clazz) || !namedInstances.get(clazz).containsKey(name)) {
            throw new IllegalArgumentException(String.format("No item has been registered of class: %s and name %s", clazz.toString(), name));
        }
        return (T)namedInstances. get(clazz).get(name);
    }

    @Override
    public boolean isDefaultInstanceLoaded(Class clazz) {
        return registeredInstances.containsKey(clazz);
    }

    @Override
    public <T> void registerDefaultInstance(Class<T> clazz, T toRegister) {
        synchronized (registeredInstances) {
            if (registeredInstances.containsKey(clazz)) {
                throw new IllegalArgumentException("An item has already been registered of class: " + clazz.toString());
            }
            registeredInstances.put(clazz, toRegister);
        }
    }

    @Override
    public <T> void registerNamedInstance(Class<T> clazz, String name, T toRegister) {
        if (!namedInstances.containsKey(clazz)) {
            namedInstances.put(clazz, new HashMap<String, Object>());
        }
        HashMap<String, Object> namedInstancesOfType = namedInstances.get(clazz);
        if (namedInstancesOfType.containsKey(name)) {
            throw new IllegalArgumentException(String.format("An item has already been registered of class: %s and name %s",
                    clazz.toString(), name));
        }
        namedInstancesOfType.put(name, toRegister);
    }

    @Override
    public boolean isNamedInstanceLoaded(Class clazz, String name) {
        return namedInstances.containsKey(clazz) && namedInstances.get(clazz).containsKey(name);
    }

}
