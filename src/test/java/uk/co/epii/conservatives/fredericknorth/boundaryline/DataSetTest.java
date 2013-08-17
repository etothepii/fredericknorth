package uk.co.epii.conservatives.fredericknorth.boundaryline;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xBaseJ.DBF;
import org.xBaseJ.fields.CharField;
import org.xBaseJ.fields.Field;
import org.xBaseJ.fields.NumField;
import org.xBaseJ.xBaseJException;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.TestApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * User: James Robinson
 * Date: 20/07/2013
 * Time: 19:59
 */
public class DataSetTest {

    private static final Logger LOG = LoggerFactory.getLogger(DataSetTest.class);
    private DataSet dataSet;

    private String fileToLoad = "district_borough_unitary_region";
    private ApplicationContext applicationContext = new TestApplicationContext();

    @Before
    public void setUp() {
        applicationContext = new TestApplicationContext();
        String pathToFile = applicationContext.getNamedInstance(File.class, Keys.DATA_FOLDER).toString() +
                File.separator + applicationContext.getProperty("BoundaryLineDataDirectory");
        dataSet = DataSet.createFromFile(new File(pathToFile + fileToLoad + ".shp"));
    }

    @Test
    public void createFileTest() {
        for (DataSet.FileType fileType : DataSet.FileType.values()) {
            File file = dataSet.getFile(fileType);
            LOG.debug("{}", file);
            assertTrue(file.exists());
            assertTrue(file.getAbsolutePath().endsWith(DataSet.EXTENSIONS.get(fileType)));
        }
    }

    @Test
    public void readDatabase() {
        DBF dbf = dataSet.getDatabase();
        LOG.debug("{}", dbf.getRecordCount());
        LOG.debug("{}", dbf.getFieldCount());
        for (int i = 0; i < dbf.getFieldCount(); i++) {
            try {
                Field field = dbf.getField(i + 1);
                LOG.debug("{}: {}", field.getName(), field.getType());
            }
            catch (xBaseJException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            CharField nameField = (CharField)dbf.getField("NAME");
            NumField numberField = (NumField)dbf.getField("NUMBER");
            for (int i = 1; i <= dbf.getRecordCount(); i++) {
                dbf.read();
                LOG.debug("{}: {}", nameField.get(), numberField.get());
            }
        }
        catch (xBaseJException xbe) {
            throw new RuntimeException(xbe);
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

}
