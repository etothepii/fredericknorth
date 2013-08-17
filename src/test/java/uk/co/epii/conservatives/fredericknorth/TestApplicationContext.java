package uk.co.epii.conservatives.fredericknorth;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * User: James Robinson
 * Date: 12/07/2013
 * Time: 04:03
 */
public class TestApplicationContext implements ApplicationContext {

    private static Logger LOG = Logger.getLogger(TestApplicationContext.class);

    public static final String DEFAULT_CONFIG_LOCATION = "/config.properties";

    private final HashMap<Class<?>, Object> registeredInstances;
    private final HashMap<Class<?>, HashMap<String, Object>> namedInstances;

    public TestApplicationContext() {
        this(DEFAULT_CONFIG_LOCATION);
        registerNamedInstance(File.class, Keys.DATA_FOLDER, Main.findDataFolder());
    }

    public TestApplicationContext(String propertiesResourceLocation) {
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
        }
        catch (IOException ioe) {
            LOG.error("The impossible has occurred and the strings.properties file has failed to load");
            throw new RuntimeException(ioe);
        }
        finally {
            closeOut(inputStream);
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
        return null;
    }

    @Override
    public <T> T getNamedInstance(Class<T> clazz, String name) {
        if (!namedInstances.containsKey(clazz) || !namedInstances.get(clazz).containsKey(name)) {
            return null;
        }
        return (T)namedInstances. get(clazz).get(name);
    }

    @Override
    public <T> void registerDefaultInstance(Class<T> clazz, T toRegister) {
        registeredInstances.put(clazz, toRegister);
    }

    @Override
    public boolean isDefaultInstanceLoaded(Class clazz) {
        return registeredInstances.containsKey(clazz);
    }

    @Override
    public <T> void registerNamedInstance(Class<T> clazz, String name, T toRegister) {
        if (!namedInstances.containsKey(clazz)) {
            namedInstances.put(clazz, new HashMap<String, Object>());
        }
        HashMap<String, Object> namedInstancesOfType = namedInstances.get(clazz);
        namedInstancesOfType.put(name, toRegister);
    }

    @Override
    public boolean isNamedInstanceLoaded(Class clazz, String name) {
        return namedInstances.containsKey(clazz) && namedInstances.get(clazz).containsKey(name);
    }
}
