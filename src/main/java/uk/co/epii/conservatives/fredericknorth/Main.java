package uk.co.epii.conservatives.fredericknorth;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import uk.co.epii.conservatives.fredericknorth.boundaryline.*;
import uk.co.epii.conservatives.fredericknorth.gui.MainWindow;
import uk.co.epii.conservatives.fredericknorth.gui.MainWindowModel;
import uk.co.epii.conservatives.fredericknorth.maps.gui.DotFactory;
import uk.co.epii.conservatives.fredericknorth.opendata.*;
import uk.co.epii.conservatives.fredericknorth.pdf.PDFRenderer;
import uk.co.epii.conservatives.fredericknorth.reports.DwellingCountReportBuilder;
import uk.co.epii.conservatives.fredericknorth.reports.DwellingCountReportBuilderRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.gui.DotFactoryRegistrar;
import uk.co.epii.conservatives.fredericknorth.maps.*;
import uk.co.epii.conservatives.fredericknorth.pdf.PDFRendererRegistrar;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializerRegistrar;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.DefaultApplicationContext;
import uk.co.epii.conservatives.fredericknorth.utilities.gui.ProgressTrackerFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

/**
 * An awesome fredericknorth
 * 
 */
public class Main
{
	private static final Logger LOG = Logger.getLogger(Main.class);

    private ProgressTrackerFrame progressTracker;
    private BoundaryLineController boundaryLineController;
    private PostcodeDatumFactory postcodeDatumFactory;
    private DwellingProcessor dwellingProcessor;
    private OSMapLocator osMapLocator;
    private PostcodeProcessor postcodeProcessor;
    private LocationFactory locationFactory;
    private MapLabelFactory mapLabelFactory;
    private DotFactory dotFactory;
    private XMLSerializer xmlSerializer;
    private OSMapLoader osMapLoader;
    private MapViewGenerator screenMapGenerator;
    private MapViewGenerator pdfMapGenerator;
    private MapImageFactory mapImageFactory;
    private PDFRenderer pdfRenderer;
    private BoundedAreaFactory boundedAreaFactory;
    private DwellingCountReportBuilder dwellingCountReportBuilder;

    public void setConfigPropertiesResourcesLocation(String configPropertiesResourcesLocation) {
        Properties config = new Properties();
        try {
            config.load(Main.class.getClassLoader().getResourceAsStream(configPropertiesResourcesLocation));
            Enumeration<?> names = config.propertyNames();
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                System.setProperty(name, config.getProperty(name));
            }
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public void setProgressTracker(ProgressTrackerFrame progressTracker) {
        this.progressTracker = progressTracker;
    }

    public void setBoundaryLineController(BoundaryLineController boundaryLineController) {
        this.boundaryLineController = boundaryLineController;
    }

    public void setPostcodeDatumFactory(PostcodeDatumFactory postcodeDatumFactory) {
        this.postcodeDatumFactory = postcodeDatumFactory;
    }

    public void setDwellingProcessor(DwellingProcessor dwellingProcessor) {
        this.dwellingProcessor = dwellingProcessor;
    }

    public void setOsMapLocator(OSMapLocator osMapLocator) {
        this.osMapLocator = osMapLocator;
    }

    public void setPostcodeProcessor(PostcodeProcessor postcodeProcessor) {
        this.postcodeProcessor = postcodeProcessor;
    }

    public void setLocationFactory(LocationFactory locationFactory) {
        this.locationFactory = locationFactory;
    }

    public void setMapLabelFactory(MapLabelFactory mapLabelFactory) {
        this.mapLabelFactory = mapLabelFactory;
    }

    public void setDotFactory(DotFactory dotFactory) {
        this.dotFactory = dotFactory;
    }

    public void setXmlSerializer(XMLSerializer xmlSerializer) {
        this.xmlSerializer = xmlSerializer;
    }

    public void setOsMapLoader(OSMapLoader osMapLoader) {
        this.osMapLoader = osMapLoader;
    }

    public void setScreenMapGenerator(MapViewGenerator screenMapGenerator) {
        this.screenMapGenerator = screenMapGenerator;
    }

    public void setPdfMapGenerator(MapViewGenerator pdfMapGenerator) {
        this.pdfMapGenerator = pdfMapGenerator;
    }

    public void setMapImageFactory(MapImageFactory mapImageFactory) {
        this.mapImageFactory = mapImageFactory;
    }

    public void setPdfRenderer(PDFRenderer pdfRenderer) {
        this.pdfRenderer = pdfRenderer;
    }

    public void setBoundedAreaFactory(BoundedAreaFactory boundedAreaFactory) {
        this.boundedAreaFactory = boundedAreaFactory;
    }

    public void setDwellingCountReportBuilder(DwellingCountReportBuilder dwellingCountReportBuilder) {
        this.dwellingCountReportBuilder = dwellingCountReportBuilder;
    }

    public static void main(final String[] args) throws Exception
	{
        ApplicationContext context = new ClassPathXmlApplicationContext("/applicationContext.xml");
        Main main = (Main)context.getBean("main");
        try {
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
