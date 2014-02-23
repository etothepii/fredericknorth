package uk.co.epii.conservatives.fredericknorth;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.*;
import uk.co.epii.conservatives.fredericknorth.gui.MainWindow;
import uk.co.epii.conservatives.fredericknorth.gui.MainWindowModel;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingProcessor;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactory;
import uk.co.epii.conservatives.fredericknorth.opendata.db.DwellingProcessorDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.opendata.db.PostcodeDatumFactoryDatabaseImpl;
import uk.co.epii.conservatives.fredericknorth.reports.DwellingCountReportBuilderRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.gui.DotFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.*;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingProcessorRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.pdf.PDFRendererRegistrar;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerRegistrar;
import uk.co.epii.conservatives.fredericknorth.utilities.DefaultApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerFrame;
import uk.co.epii.conservatives.williamcavendishbentinck.DatabaseSession;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.util.Arrays;

/**
 * An awesome fredericknorth
 * 
 */
public class Main
{
	private static final Logger LOG = Logger.getLogger(Main.class);

    private static ProgressTrackerFrame progressTracker;

	public static void main(final String[] args) throws Exception
	{
        progressTracker = new ProgressTrackerFrame(
                ImageIO.read(Main.class.getResourceAsStream("/letterbox.jpg")), 19);
        progressTracker.setVisible(true);
        progress("Loading Config");
        ApplicationContext springContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        DefaultApplicationContext applicationContext =
                new DefaultApplicationContext(DefaultApplicationContext.DEFAULT_CONFIG_LOCATION);
        try {
            applicationContext.registerDefaultInstance(DatabaseSession.class,
                    (DatabaseSession)springContext.getBean("databaseSession"));
            progress("Finding Data Folder");
            applicationContext.registerNamedInstance(File.class, Keys.DATA_FOLDER, findDataFolder());
            progress("Loading Boundary Line Controller");
            BoundaryLineControllerRegistrar.registerToContext(applicationContext);
            progress("Loading Postcode Data");
            applicationContext.registerDefaultInstance(PostcodeDatumFactory.class,
                    (PostcodeDatumFactory)springContext.getBean("postcodeDatumFactory"));
            progress("Loading Dwelling Processor");
            applicationContext.registerDefaultInstance(DwellingProcessor.class,
                    (DwellingProcessor)springContext.getBean("dwellingProcessor"));
            progress("Loading OS Map Locator");
            OSMapLocatorRegistrar.registerToContext(applicationContext);
            progress("Loading Location Factory");
            LocationFactoryRegistrar.registerToContext(applicationContext);
            progress("Loading Map Label Factory");
            MapLabelFactoryRegistrar.registerToContext(applicationContext);
            progress("Loading Dot Factory");
            DotFactoryRegistrar.registerToContext(applicationContext);
            progress("Loading XML Serializer");
            XMLSerializerRegistrar.registerToContext(applicationContext);
            progress("Loading OS Map Loader");
            OSMapLoaderRegistrar.registerToContext(applicationContext);
            progress("Loading Map View Generators");
            MapViewGeneratorRegistrar.registerToContext(applicationContext, progressTracker);
            MapViewGeneratorRegistrar.registerNamedToContext(applicationContext, Keys.PDF_GENERATOR, progressTracker);
            progress("Loading Map Image Factory");
            MapImageFactoryRegistrar.registerToContext(applicationContext);
            progress("Loading PDF Renderer");
            PDFRendererRegistrar.registerToContext(applicationContext);
            progress("Loading Bounded Area Factory");
            BoundedAreaFactoryRegistrar.registerToContext(applicationContext);
            progress("Loading DwellingCountReportBuilder");
            DwellingCountReportBuilderRegistrar.registerToContext(applicationContext);
        }
        catch (Exception e) {
            LOG.error("Exception thrown during startup", e);
            System.exit(1);
        }
        progress("Starting ...");
        progressTracker.isIndeterminate();
        MainWindowModel mainWindowModel = new MainWindowModel(applicationContext);
        MainWindow mainWindow = new MainWindow(applicationContext, mainWindowModel);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainWindow.setVisible(true);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressTracker.setVisible(false);
        progressTracker.dispose();
    }

    static File findDataFolder() {
        File file = new File(System.getProperty("user.home") + "/frederickNorth/Data");
        if (file.exists()) return file;
        throw new RuntimeException("Can not find data folder");
    }

    private static void progress(String message) {
        LOG.info(message);
        progressTracker.increment(message);
    }
}
