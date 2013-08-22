package uk.co.epii.conservatives.fredericknorth;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.boundaryline.*;
import uk.co.epii.conservatives.fredericknorth.reports.DwellingCountReportBuilderRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.gui.DotFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.*;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingProcessorRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeDatumFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.opendata.PostcodeProcessorRegistrar;
import uk.co.epii.conservatives.fredericknorth.pdf.PDFRendererRegistrar;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerRegistrar;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.DefaultApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerFrame;

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
                ImageIO.read(Main.class.getResourceAsStream("/letterbox.jpg")), 18);
        progressTracker.setVisible(true);
        progress("Loading Config");
        ApplicationContext applicationContext =
                new DefaultApplicationContext(DefaultApplicationContext.DEFAULT_CONFIG_LOCATION);
        try {
            progress("Finding Data Folder");
            applicationContext.registerNamedInstance(File.class, Keys.DATA_FOLDER, findDataFolder());
            progress("Loading Boundary Line Controller");
            BoundaryLineControllerRegistrar.registerToContext(applicationContext);
            progress("Loading Postcode Data");
            PostcodeDatumFactoryRegistrar.registerToContext(applicationContext);
            progress("Loading Dwelling Processor");
            DwellingProcessorRegistrar.registerToContext(applicationContext, progressTracker);
            progress("Loading OS Map Locator");
            OSMapLocatorRegistrar.registerToContext(applicationContext);
            progress("Loading Postcode Processor");
            PostcodeProcessorRegistrar.registerToContext(applicationContext);
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
            MapViewGeneratorRegistrar.registerToContext(applicationContext);
            MapViewGeneratorRegistrar.registerNamedToContext(applicationContext, Keys.PDF_GENERATOR);
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
        MainMenu mainMenu = new MainMenu(applicationContext);
        mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenu.setLocationRelativeTo(null);
        mainMenu.setVisible(true);
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
