package uk.co.epii.conservatives.fredericknorth.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundaryLineController;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.maps.*;
import uk.co.epii.conservatives.fredericknorth.maps.extentions.WeightedPointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.Dwelling;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;
import uk.co.epii.conservatives.williampittjr.LogoGenerator;

import java.awt.*;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 26/06/2013
 * Time: 00:16
 */
class PDFRendererImpl implements PDFRenderer {

    private static final Logger LOG = Logger.getLogger(PDFRendererImpl.class);

    private static final int WARD_TITLE_SIZE = 24;
    private static final int ROUTE_TITLE_SIZE = 18;
    private static final int NORMAL_FONT_SIZE = 12;
    private static final int SMALL_FONT_SIZE = 6;

    private static final BaseColor CONSERVATIVE_GREEN = new BaseColor(110, 215, 0);
    private static final BaseColor CONSERVATIVE_BLUE = new BaseColor(0, 135, 220);
    private static final BaseColor CONSERVATIVE_TINT = new BaseColor(235, 235, 225);

    private File file;
    private Document document;
    private final BaseFont conservativeBaseFont;
    private final MapLabelFactory mapLabelFactory;
    private final LocationFactory locationFactory;
    private final MapViewGenerator mapViewGenerator;
    private final Comparator<Route> routeNameComparator;
    private final Comparator<RouteMapGrouping> routeMapGroupingComparator;
    private final Comparator<String> dwellingIdentifierComparator;
    private final Map<String, Image> cachedLogos = new HashMap<String, Image>();
    private final BoundaryLineController boundaryLineController;
    private List<? extends Location> meetingPoints;
    private PdfWriter writer = null;
    private LogoGenerator logoGenerator;

    PDFRendererImpl(LogoGenerator logoGenerator, BaseFont conservativeBaseFont, MapLabelFactory mapLabelFactory, LocationFactory locationFactory,
                    MapViewGenerator mapViewGenerator, BoundaryLineController boundaryLineController, List<Location> meetingPoints) {
        this.boundaryLineController = boundaryLineController;
        this.logoGenerator = logoGenerator;
        this.conservativeBaseFont = conservativeBaseFont;
        this.mapLabelFactory = mapLabelFactory;
        this.locationFactory = locationFactory;
        this.mapViewGenerator = mapViewGenerator;
        this.meetingPoints = meetingPoints;
        this.routeMapGroupingComparator = new Comparator<RouteMapGrouping>() {
            @Override
            public int compare(RouteMapGrouping o1, RouteMapGrouping o2) {
                int comparison = o1.getGeoLocation().x - o2.getGeoLocation().x;
                if (comparison != 0) return  comparison;
                return o1.getGeoLocation().y - o2.getGeoLocation().y;
            }
        };
        routeNameComparator = new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                int compare;
                if ((compare = o1.getFullyQualifiedName().compareTo(o2.getFullyQualifiedName())) != 0) return compare;
                return o1.getName().compareTo(o2.getName());
            }
        };
        dwellingIdentifierComparator = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Integer o1Int, o2Int;
                try {
                    o1Int = Integer.parseInt(o1);
                }
                catch (NumberFormatException nfe) {
                    o1Int = null;
                }
                try {
                    o2Int = Integer.parseInt(o2);
                }
                catch (NumberFormatException nfe) {
                    o2Int = null;
                }
                if (o1Int == null && o2Int == null) {
                    return o1.compareTo(o2);
                }
                if (o1Int == null) {
                    return -1;
                }
                if (o2Int == null) {
                    return 1;
                }
                return o1Int - o2Int;
            }
        };
    }

    @Override
    public void buildRouteGuide(Route route, File file)  {
        this.file = file;
        try {
            createDocument();
            addRouteContent(route);
            closeDocument();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void buildRoutesGuide(Collection<? extends Route> routesCollection, File file, ProgressTracker progressTracker)  {
        ArrayList<Route> routes = new ArrayList<Route>(routesCollection);
        progressTracker.startSubsection(routes.size());
        Collections.sort(routes, routeNameComparator);
        this.file = file;
        try {
            createDocument();
            boolean first = true;
            for (Route route : routes) {
                progressTracker.setMessage(route.getName());
                try {
                    if (route.getDwellingGroups().isEmpty()) {
                        continue;
                    }
                    if (first) {
                        first = false;
                    }
                    else {
                        document.newPage();
                    }
                    addRouteContent(route);
                }
                finally {
                    progressTracker.increment();
                }
            }
            closeDocument();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void buildRoutesGuide(RoutableArea routableArea, File file, ProgressTracker progressTracker) {
        buildRoutesGuide(routableArea.getRoutes(), file, progressTracker);
    }

    @Override
    public void setMeetingPoints(List<? extends Location> meetingPoints) {
        this.meetingPoints = meetingPoints;
    }

    private void addRouteContent(Route route) throws DocumentException, IOException {
        String wardName = route.getRoutableArea().getName();
        String routeName = route.getName();
        String association = route.getAssociation();
        if (association == null) {
            association = getConstituency(route);
            route.setAssociation(association);
        }
        document.add(createTitle(association, wardName, routeName));
        List<RouteMapGrouping> routeMapGroupings = getGroupings(route);
        document.add(createRouteMap(routeMapGroupings));
        document.add(createTable(routeMapGroupings));
        LOG.debug("PageNumber: " + writer.getPageNumber());
        if (writer.getPageNumber() % 2 == 1) {
            document.newPage();
        }
        document.add(createDwellingList(routeMapGroupings));

    }

    private String getConstituency(Route route) {
        Set<DwellingGroup> dwellingGroups = new HashSet<DwellingGroup>(route.getDwellingGroups());
        Collection<WeightedPoint> weightedPoints = new ArrayList<WeightedPoint>(dwellingGroups.size());
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            weightedPoints.add(new DefaultWeightedPoint(dwellingGroup.getPoint(), dwellingGroup.size()));
        }
        Point2D.Double median = WeightedPointExtensions.getMedian(weightedPoints);
        BoundedArea mainConstituency =
                boundaryLineController.getContainingFeature(
                        BoundedAreaType.PARLIAMENTARY_CONSTITUENCY, median.getX(), median.getY());
        return mainConstituency == null ? "An Association" : mainConstituency.getName();
    }

    private Element createDwellingList(List<RouteMapGrouping> routeMapGroupings) {
        PdfPTable table = new PdfPTable(new float[] {0.05f, 0.95f});
        for (int i = 0; i < routeMapGroupings.size(); i++) {
            RouteMapGrouping routeMapGrouping = routeMapGroupings.get(i);
            PdfPCell cell = new PdfPCell(getChunk((i + 1) + "", SMALL_FONT_SIZE, true, BaseColor.BLACK));
            cell.setRowspan(routeMapGrouping.hasCommonName() ? 2 : routeMapGrouping.getDwellingGroupList().size() * 2);
            table.addCell(cell);
            if (routeMapGrouping.hasCommonName()) {
                cell = new PdfPCell(getChunk(routeMapGrouping.getCommonName(), SMALL_FONT_SIZE, true, BaseColor.BLACK));
                table.addCell(cell);
                String orderedIdentifiers = toCommaSeperatedString(getOrderedIdentifiers(
                        routeMapGrouping.getDwellingGroupList(), routeMapGrouping.size()));
                cell = new PdfPCell(getChunk(orderedIdentifiers, SMALL_FONT_SIZE, true, BaseColor.BLACK));
                table.addCell(cell);
            }
            else {
                for (DwellingGroup dwellingGroup : routeMapGrouping.getDwellingGroupList()) {
                    cell = new PdfPCell(getChunk(dwellingGroup.getDisplayName(), SMALL_FONT_SIZE, true, BaseColor.BLACK));
                    table.addCell(cell);
                    String orderedIdentifiers = toCommaSeperatedString(getOrderedIdentifiers(dwellingGroup));
                    cell = new PdfPCell(getChunk(orderedIdentifiers, SMALL_FONT_SIZE, true, BaseColor.BLACK));
                    table.addCell(cell);
                }
            }
        }
        table.setWidthPercentage(100f);
        return table;
    }

    private List<String> getOrderedIdentifiers(DwellingGroup dwellingGroup) {
        ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
        dwellingGroups.add(dwellingGroup);
        return getOrderedIdentifiers(dwellingGroups, dwellingGroup.size());

    }

    private List<String> getOrderedIdentifiers(Collection<? extends DwellingGroup> dwellingGroups, int size) {
        List<String> dwellings = new ArrayList<String>(size);
        for (DwellingGroup dwellingGroup : dwellingGroups) {
            for (Dwelling dwelling : dwellingGroup.getDwellings()) {
                dwellings.add(dwelling.getIdentifier());
            }
        }
        Collections.sort(dwellings, dwellingIdentifierComparator);
        return dwellings;
    }

    private String toCommaSeperatedString(Collection<String> strings) {
        boolean first = true;
        StringBuilder stringBuilder = new StringBuilder(strings.size() * 32);
        for (String string : strings) {
            if (first) {
                first = false;
            }
            else {
                stringBuilder.append(", ");
            }
            stringBuilder.append(string);
        }
        return stringBuilder.toString();
    }

    private Element createTitle(String constituency, String wardName, String routeName) {
        PdfPTable table = new PdfPTable(new float[] {0.5f, 0.5f});
        table.setWidthPercentage(100f);
        PdfPCell wardCell = new PdfPCell(new Paragraph(getChunk(wardName, WARD_TITLE_SIZE, true, CONSERVATIVE_BLUE)));
        wardCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(wardCell);
        PdfPCell logoCell = new PdfPCell(getLogo(constituency));
        logoCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        logoCell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        logoCell.setRowspan(2);
        logoCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(logoCell);
        PdfPCell routeCell = new PdfPCell(new Paragraph(getChunk(routeName, ROUTE_TITLE_SIZE, true, CONSERVATIVE_GREEN)));
        routeCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(routeCell);
        PdfPCell spacer = new PdfPCell();
        spacer.setFixedHeight(36f);
        spacer.setColspan(2);
        spacer.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(spacer);
        return table;
    }

    private Image getLogo(String constituency) {
        Image logo = cachedLogos.get(constituency);
        if (logo == null) {
            try {
                BufferedImage conLogo = logoGenerator.getLogo(constituency);
                logo = Image.getInstance(Toolkit.getDefaultToolkit().createImage(conLogo.getSource()), null);
                float width = 216f;
                float height = (width * conLogo.getHeight()) / conLogo.getWidth();
                logo.scaleAbsolute(width, height);
                cachedLogos.put(constituency, logo);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            catch (BadElementException e) {
                throw new RuntimeException(e);
            }
        }
        return logo;
    }

    private List<RouteMapGrouping> getGroupings(Route route) {
        HashMap<Point, RouteMapGrouping> groupings = new HashMap<Point, RouteMapGrouping>();
        for (DwellingGroup dwellingGroup : route.getDwellingGroups()) {
            if (!groupings.containsKey(dwellingGroup.getPoint())) {
                groupings.put(dwellingGroup.getPoint(), new RouteMapGrouping(dwellingGroup.getPoint()));
            }
            groupings.get(dwellingGroup.getPoint()).addDwellingGroup(dwellingGroup);
        }
        List<RouteMapGrouping> listOfGroupings = new ArrayList<RouteMapGrouping>(groupings.values());
        Collections.sort(listOfGroupings, routeMapGroupingComparator);
        return listOfGroupings;
    }

    private void closeDocument() {
        document.close();
    }

    private void createDocument() throws FileNotFoundException, DocumentException {
        document = new Document();
        document.setPageSize(PageSize.A4);
        document.setMargins(72f, 72f, 72f, 72f);
        writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
    }

    private PdfPTable createRouteMap(List<RouteMapGrouping> routeMapGroupings) throws IOException, BadElementException {
        PdfPTable table = new PdfPTable(new float[] {1f});
        table.setWidthPercentage(100f);
        List<Location> geoLocations = new ArrayList<Location>();
        int index = 0;
        for (RouteMapGrouping routeMapGrouping : routeMapGroupings) {
            geoLocations.add(locationFactory.getInstance((++index) + "", routeMapGrouping.getGeoLocation()));
        }
        Location meetingPoint = getNearestMeetingPoint(routeMapGroupings);
        if (meetingPoint != null) {
            geoLocations.add(meetingPoint);
        }
        mapViewGenerator.setViewPortSize(new Dimension(640, 480), new NullProgressTracker(), null);
        Rectangle mapRectangle = locationFactory.calculatePaddedRectangle(geoLocations);
        mapViewGenerator.scaleToFitRectangle(mapRectangle, new NullProgressTracker(), null);
        MapView mapView = mapViewGenerator.getView();
        List<Location> imageLocations = new ArrayList<Location>();
        for (Location location : geoLocations) {
            imageLocations.add(locationFactory.getInstance(location.getName(), mapView.getImageLocation(location.getPoint())));
        }
        Graphics2D g = (Graphics2D)mapView.getMap().getGraphics();
        g.setTransform(AffineTransform.getScaleInstance(1d, 1d));
        for (MapLabel mapLabel : mapLabelFactory.getMapLabels(
                new Rectangle(mapView.getSize()), imageLocations, mapView.getMap().getGraphics())) {
            mapLabel.paint(g);
        }
        Image image = Image.getInstance(Toolkit.getDefaultToolkit().createImage(mapView.getMap().getSource()), null);
        table.addCell(image);
        return table;
    }

    private Location getNearestMeetingPoint(List<RouteMapGrouping> routeMapGroupings) {
        Point2D.Double median = WeightedPointExtensions.getMedian(routeMapGroupings);
        Location nearest = null;
        double distance = Double.MAX_VALUE;
        for (Location meetingPoint : meetingPoints) {
            double d = Math.sqrt(Math.pow(median.x - meetingPoint.getPoint().x, 2) +
                    Math.pow(median.y - meetingPoint.getPoint().y, 2));
            if (d < distance) {
                distance = d;
                nearest = meetingPoint;
            }
        }
        if (distance > 1610) return null;
        return nearest;
    }

    private PdfPTable createTable(List<RouteMapGrouping> routeMapGroupings) {
        PdfPTable table = new PdfPTable(new float[] {.05f,.875f,.075f});
        table.setWidthPercentage(100f);
        int dwellingGroupIndex = 0;
        int total = 0;
        for (RouteMapGrouping routeMapGrouping : routeMapGroupings) {
            dwellingGroupIndex++;
            if (routeMapGrouping.hasCommonName()) {
                table.addCell(getChunk(dwellingGroupIndex + ""));
                table.addCell(getChunk(routeMapGrouping.getCommonName()));
                PdfPCell dwellings = new PdfPCell(getChunk(routeMapGrouping.size() + ""));
                dwellings.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                table.addCell(dwellings);
            }
            else {
                PdfPCell cell = new PdfPCell(getChunk(dwellingGroupIndex + ""));
                cell.setRowspan(routeMapGrouping.getDwellingGroupList().size());
                table.addCell(cell);
                for (DwellingGroup dwellingGroup : routeMapGrouping.getDwellingGroupList()) {
                    table.addCell(getChunk(dwellingGroup.getDisplayName()));
                    PdfPCell dwellings = new PdfPCell(getChunk(dwellingGroup.size() + ""));
                    dwellings.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                    table.addCell(dwellings);
                }
            }
            total += routeMapGrouping.size();
        }
        PdfPCell cell = new PdfPCell(new Phrase(""));
        cell.setColspan(2);
        cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(cell);
        PdfPCell dwellings = new PdfPCell(getChunk(total + "", NORMAL_FONT_SIZE, true, BaseColor.BLACK));
        dwellings.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        dwellings.setBackgroundColor(CONSERVATIVE_TINT);
        table.addCell(dwellings);
        return table;
    }

    private Phrase getChunk(String text) {
        return getChunk(text, NORMAL_FONT_SIZE);
    }

    private Phrase getChunk(String text, int size) {
        return getChunk(text, size, false, BaseColor.BLACK);
    }

    private Paragraph getChunk(String text, int size, boolean bold, BaseColor baseColor) {
        Font font = new Font(conservativeBaseFont, size, bold ? Font.BOLD : Font.NORMAL);
        font.setColor(baseColor);
        return new Paragraph(new Chunk(text, font));
    }
}
