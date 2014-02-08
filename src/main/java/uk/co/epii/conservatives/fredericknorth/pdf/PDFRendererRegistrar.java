package uk.co.epii.conservatives.fredericknorth.pdf;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundaryLineController;
import uk.co.epii.conservatives.fredericknorth.maps.Location;
import uk.co.epii.conservatives.fredericknorth.utilities.ApplicationContext;
import uk.co.epii.conservatives.fredericknorth.Keys;
import uk.co.epii.conservatives.fredericknorth.maps.LocationFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapLabelFactory;
import uk.co.epii.conservatives.fredericknorth.maps.MapViewGenerator;
import uk.co.epii.conservatives.williampittjr.ConservativeLogoGeneratorImpl;
import uk.co.epii.conservatives.williampittjr.LogoGenerator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * User: James Robinson
 * Date: 26/06/2013
 * Time: 00:17
 */
public class PDFRendererRegistrar {

    private static final Logger LOG = Logger.getLogger(PDFRendererRegistrar.class);

    private static final String PDFFontKey = "PDFFont";

    public static void registerToContext(ApplicationContext applicationContext) {
        LOG.info("Loading PDF Font to temp dir");
        String pdfFont = applicationContext.getProperty(PDFFontKey);
        URL pdfFontLocation = PDFRendererImpl.class.getResource(pdfFont);
        String lastPart = null;
        for (String thisPart : pdfFont.split("[/\\\\]")) {
            lastPart = thisPart;
        }
        String tempFontLocation = System.getProperty("java.io.tmpdir") + lastPart;
        try {
            FileUtils.copyURLToFile(pdfFontLocation, new File(tempFontLocation));
            LOG.debug("Copied font from: " + pdfFontLocation.getFile());
            LOG.debug("Copied font to: " + tempFontLocation);
            BaseFont conservativeBaseFont = BaseFont.createFont(tempFontLocation, BaseFont.WINANSI, true);
            applicationContext.registerDefaultInstance(PDFRenderer.class, new PDFRendererImpl(
                    new ConservativeLogoGeneratorImpl(), conservativeBaseFont,
                    applicationContext.getDefaultInstance(MapLabelFactory.class),
                    applicationContext.getDefaultInstance(LocationFactory.class),
                    applicationContext.getNamedInstance(MapViewGenerator.class, Keys.PDF_GENERATOR),
                    applicationContext.getDefaultInstance(BoundaryLineController.class),
                    new ArrayList<Location>()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }
}
