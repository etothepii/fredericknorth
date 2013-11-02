package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.apache.commons.io.FileUtils;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.opengis.filter.FilterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumMap;

/**
 * User: JamesRobinson
 * Date: 20/07/2013
 * Time: 19:20
 */
public class DataSet {

    public enum FileType {DBF, PRJ, SHP, SHX}

    private static final Logger LOG = LoggerFactory.getLogger(DataSet.class);
    private static final Logger LOG_SYNC = LoggerFactory.getLogger(DataSet.class.getName().concat("_sync"));

    public final static EnumMap<FileType, String> EXTENSIONS;

    static {
        EXTENSIONS = new EnumMap<FileType, String>(FileType.class);
        EXTENSIONS.put(FileType.DBF, "dbf");
        EXTENSIONS.put(FileType.PRJ, "prj");
        EXTENSIONS.put(FileType.SHP, "shp");
        EXTENSIONS.put(FileType.SHX, "shx");
    }

    private final FilterFactory filterFactory;
    private EnumMap<FileType, File> files;
    private SimpleFeatureSource featureSource;
    private DBF dbf;

    public DataSet() {
        filterFactory = CommonFactoryFinder.getFilterFactory(null);
    }

    private DataSet(EnumMap<FileType, File> files) {
        this.files = files;
        filterFactory = CommonFactoryFinder.getFilterFactory(null);
    }

    public File getFile(FileType fileType) {
        return files.get(fileType);
    }

    public void setShpFile(String shpFile) {
        LOG.debug("{}", shpFile);
        EnumMap<FileType, File> files = new EnumMap<FileType, File>(FileType.class);
        for (FileType fileType : FileType.values()) {
            LOG.debug("{}", EXTENSIONS.get(fileType));
            File file = new File(shpFile.substring(0, shpFile.length() - 3) + EXTENSIONS.get(fileType));
            files.put(fileType, file);
        }
        this.files = files;
    }

    public void setShpURL(URL shpURL) {
        LOG.debug("{}", shpURL);
        EnumMap<FileType, File> files = new EnumMap<FileType, File>(FileType.class);
        for (FileType fileType : FileType.values()) {
            LOG.debug("{}", EXTENSIONS.get(fileType));
            String fileString = shpURL.toString();
            try {
                URL copyFrom = new URL(fileString.substring(0, fileString.length() - 3) + EXTENSIONS.get(fileType));
                File file = new File(copyFrom.toURI());
                files.put(fileType, file);
            }
            catch (MalformedURLException mue) {
                throw new RuntimeException(mue);
            }
            catch (URISyntaxException uriSE) {
                throw new RuntimeException(uriSE);
            }
        }
        this.files = files;
    }

    public DBF getDatabase() {
        if (dbf == null) {
            LOG_SYNC.debug("Awaiting this");
            try {
                synchronized (this) {
                    LOG_SYNC.debug("Received this");
                    if (dbf == null) {
                        try {
                            dbf = new DBF(getFile(FileType.DBF).toString());
                        }
                        catch (xBaseJException e) {
                            throw new RuntimeException(e);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            finally {
                LOG_SYNC.debug("Released this");
            }
        }
        return dbf;
    }

    public SimpleFeatureSource getFeatureSource() {
        if (featureSource == null) {
            LOG_SYNC.debug("Awaiting this");
            try {
                synchronized (this) {
                    LOG_SYNC.debug("Received this");
                    if (featureSource == null) {
                        try {
                            FileDataStore store = FileDataStoreFinder.getDataStore(getFile(FileType.SHP));
                            featureSource = store.getFeatureSource();
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            finally {
                LOG_SYNC.debug("Released this");
            }
        }
        return featureSource;
    }

    public SimpleFeatureCollection getEqualFilterTo(String column, String value) {
        try {
            return getFeatureSource().getFeatures(filterFactory.equal(filterFactory.property(column),
                    filterFactory.literal(value), true));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSet createFromResource(URL shpFile) {
        return create(shpFile);
    }

    public static DataSet createFromFile(File shpFile) {
        return create(shpFile);
    }

    private static DataSet create(Object shpFile) {
        LOG.debug("{}", shpFile);
        EnumMap<FileType, File> files = new EnumMap<FileType, File>(FileType.class);
        for (FileType fileType : FileType.values()) {
            LOG.debug("{}", EXTENSIONS.get(fileType));
            try {
                String fileString = shpFile.toString();
                File file;
                if (shpFile instanceof URL) {
                    URL copyFrom = new URL(fileString.substring(0, fileString.length() - 3) + EXTENSIONS.get(fileType));
                    file = new File(copyFrom.toURI());
                }
                else if (shpFile instanceof File) {
                    file = new File(fileString.substring(0, fileString.length() - 3) + EXTENSIONS.get(fileType));
                }
                else {
                    throw new IllegalArgumentException("Unsupported shpFile type " + shpFile.getClass());
                }
                files.put(fileType, file);
            }
            catch (MalformedURLException mue) {
                throw new RuntimeException(mue);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        return new DataSet(files);
    }

}
