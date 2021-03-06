package uk.co.epii.conservatives.fredericknorth.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundaryLineController;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.maps.*;
import uk.co.epii.conservatives.fredericknorth.maps.extentions.WeightedPointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.routes.RoutableArea;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.utilities.NullProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;
import uk.co.epii.conservatives.fredericknorth.utilities.StringExtentions;
import uk.co.epii.conservatives.williampittjr.LogoGenerator;
import uk.co.epii.spencerperceval.tuple.Duple;

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
    private static final int NORMAL_FONT_SIZE = 10;
    private static final int SMALL_FONT_SIZE = 6;

    private static final BaseColor DEFAULT_PRIMARY = new BaseColor(110, 215, 0);
    private static final BaseColor DEFAULT_SECONDARY = new BaseColor(0, 135, 220);
    private static final BaseColor CONSERVATIVE_TINT = new BaseColor(235, 235, 225);
    private final BaseColor primary;
    private final BaseColor secondary;

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
    private Comparator<String> addressComparator = new AddressComparator();
    private PdfWriter writer = null;
    private LogoGenerator logoGenerator;
    private List<Duple<String, List<Location>>> dwellingGroups;
    private RenderedRouteFactory renderedRouteFactory;
    private String thankYouTitle = "Thank you";
    private String thankYouBody = "Please scan the QR code on the right on your phone";

    PDFRendererImpl(LogoGenerator logoGenerator, BaseFont conservativeBaseFont, MapLabelFactory mapLabelFactory,
                    LocationFactory locationFactory, MapViewGenerator mapViewGenerator,
                    BoundaryLineController boundaryLineController, List<Location> meetingPoints,
                    RenderedRouteFactory renderedRouteFactory) {
        this.boundaryLineController = boundaryLineController;
        this.renderedRouteFactory = renderedRouteFactory;
        this.logoGenerator = logoGenerator;
        this.conservativeBaseFont = conservativeBaseFont;
        this.mapLabelFactory = mapLabelFactory;
        this.locationFactory = locationFactory;
        this.mapViewGenerator = mapViewGenerator;
        this.primary = logoGenerator == null ? DEFAULT_PRIMARY : new BaseColor(
                logoGenerator.getPrimaryColor().getRed(),
                logoGenerator.getPrimaryColor().getGreen(),
                logoGenerator.getPrimaryColor().getBlue());
        this.secondary = logoGenerator == null ? DEFAULT_SECONDARY : new BaseColor(
                logoGenerator.getSecondaryColor().getRed(),
                logoGenerator.getSecondaryColor().getGreen(),
                logoGenerator.getSecondaryColor().getBlue());
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
                    return 0;
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
    public RenderedRoute buildRouteGuide(Route route, File file)  {
        RenderedRoute renderedRoute;
        this.file = file;
        try {
            createDocument();
            renderedRoute = addRouteContent(route);
            closeDocument();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return renderedRoute;
    }

    @Override
    public Collection<RenderedRoute> buildRoutesGuide(Collection<? extends Route> routesCollection, File file, ProgressTracker progressTracker)  {
        ArrayList<Route> routes = new ArrayList<Route>(routesCollection);
        ArrayList<RenderedRoute> renderedRoutes = new ArrayList<RenderedRoute>(routes.size());
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
                    renderedRoutes.add(addRouteContent(route));
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
        return renderedRoutes;
    }

  public void setThankYouTitle(String thankYouTitle) {
    this.thankYouTitle = thankYouTitle;
  }

  public void setThankYouBody(String thankYouBody) {
    this.thankYouBody = thankYouBody;
  }

  @Override
    public Collection<RenderedRoute> buildRoutesGuide(RoutableArea routableArea, File file, ProgressTracker progressTracker) {
        return buildRoutesGuide(routableArea.getRoutes(), file, progressTracker);
    }

    @Override
    public void setMeetingPoints(List<? extends Location> meetingPoints) {
        this.meetingPoints = meetingPoints;
    }

    private RenderedRoute addRouteContent(Route route) throws DocumentException, IOException {
        RenderedRoute renderedRoute = renderedRouteFactory.getRenderedRoute(route);
        int startPage = writer.getPageNumber();
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
        document.add(Chunk.NEWLINE);
        document.add(createTable(routeMapGroupings));
        LOG.debug("PageNumber: " + writer.getPageNumber());
        if (writer.getPageNumber() == startPage) {
            document.newPage();
        }
        else {
            document.add(Chunk.NEWLINE);
        }
        document.add(createDwellingList());
        document.add(createQRCode(renderedRoute));
        return renderedRoute;
    }

  private Element createQRCode(RenderedRoute renderedRoute) throws DocumentException, IOException {
    LOG.debug(renderedRoute.getUniqueUrl());
    BarcodeQRCode qrCode = new BarcodeQRCode(renderedRoute.getUniqueUrl(), 1, 1, null);
    Image qrCodeImage = qrCode.getImage();
    qrCodeImage.setAlignment(Element.ALIGN_RIGHT | Element.ALIGN_BOTTOM);
    qrCodeImage.scaleAbsolute(108f, 108f);
    qrCodeImage.setBorder(0);
    qrCodeImage.setAbsolutePosition(PageSize.A4.getWidth() - qrCodeImage.getScaledWidth() - 90f, 90f);
    PdfContentByte under = writer.getDirectContentUnder();
    under.saveState();
    under.setRGBColorFill(0xE0, 0xE0, 0xE0);
    under.setRGBColorStroke(0x00, 0x00, 0x00);
    under.setLineWidth(3);
    under.rectangle(72f, 72f, PageSize.A4.getWidth() - 144f, 144f);
    under.fillStroke();
    under.restoreState();
    PdfContentByte over = writer.getDirectContent();
    over.saveState();
    ColumnText columnText = new ColumnText(over);
    columnText.setSimpleColumn(90f, 90f, PageSize.A4.getWidth() - 216f, 198f);
    Paragraph paragraph = new Paragraph();
    paragraph.add(getChunk(thankYouTitle, ROUTE_TITLE_SIZE));
    paragraph.setLeading(ROUTE_TITLE_SIZE);
    columnText.addElement(paragraph);
    paragraph = new Paragraph();
    paragraph.add(Chunk.NEWLINE);
    paragraph.add(getChunk(thankYouBody, NORMAL_FONT_SIZE));
    paragraph.setLeading(NORMAL_FONT_SIZE);
    columnText.addElement(paragraph);
    columnText.go();
    over.restoreState();
    return qrCodeImage;
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
        if (mainConstituency == null) {
            return "An Association";
        }
        String name = mainConstituency.getName();
        if (name.endsWith(" Co Const")) {
            return name.substring(0, name.length() - 9);
        }
        if (name.endsWith(" Boro Const")) {
            return name.substring(0, name.length() - 11);
        }
        if (name.endsWith(" Burgh Const")) {
            return name.substring(0, name.length() - 12);
        }
        return mainConstituency.getName();
    }

    private Element createDwellingList() {
        PdfPTable table = new PdfPTable(new float[] {1f});
        for (Duple<String, List<Location>> dwellingGrouping : dwellingGroups) {
            PdfPCell cell = new PdfPCell(getParagraph(dwellingGrouping.getFirst(), SMALL_FONT_SIZE, true, BaseColor.BLACK));
            table.addCell(cell);
            String orderedIdentifiers = toCommaSeperatedString(getOrderedIdentifiers(
                    dwellingGrouping.getSecond(), dwellingGrouping.getSecond().size()));
            cell = new PdfPCell(getParagraph(orderedIdentifiers, SMALL_FONT_SIZE, true, BaseColor.BLACK));
            table.addCell(cell);
        }
        table.setWidthPercentage(100f);
        return table;
    }

    private List<String> getOrderedIdentifiers(Collection<? extends Location> dwellingGroup, int size) {
        List<String> dwellings = new ArrayList<String>(size);
        for (Location dwelling : dwellingGroup) {
            dwellings.add(dwelling.getName());
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
        PdfPCell wardCell = new PdfPCell(new Paragraph(getParagraph(wardName, WARD_TITLE_SIZE, true, primary)));
        wardCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(wardCell);
        PdfPCell logoCell = new PdfPCell(getLogo(constituency));
        logoCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        logoCell.setVerticalAlignment(PdfPCell.ALIGN_TOP);
        logoCell.setRowspan(2);
        logoCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(logoCell);
        PdfPCell routeCell = new PdfPCell(new Paragraph(getParagraph(routeName, ROUTE_TITLE_SIZE, true, secondary)));
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
        List<Duple<Point, Integer>> deliveryPoints = getDeliveryPoints(routeMapGroupings);
        Location meetingPoint = getNearestMeetingPoint(routeMapGroupings);
        Rectangle mapRectangle = deriveMapRectangle(deliveryPoints, meetingPoint);
        mapViewGenerator.setViewPortSize(new Dimension(640, 480), new NullProgressTracker(), null);
        mapViewGenerator.scaleToFitRectangle(mapRectangle, new NullProgressTracker(), null);
        MapView mapView = mapViewGenerator.getView();
        for (Duple<Point, Integer> deliveryPoint : deliveryPoints) {
            deliveryPoint.setFirst(mapView.getImageLocation(deliveryPoint.getFirst()));
        }
        Graphics2D g = (Graphics2D)mapView.getMap().getGraphics();
        g.setTransform(AffineTransform.getScaleInstance(1d, 1d));
        if (meetingPoint != null) {
            for (MapLabel mapLabel : mapLabelFactory.getMapLabels(
                    new Rectangle(mapView.getSize()), Arrays.asList(meetingPoint), mapView.getMap().getGraphics(), mapView)) {
                mapLabel.paint(g);
            }
        }
        for (Duple<Point, Integer> deliveryPoint : deliveryPoints) {
            drawDot(g, deliveryPoint.getFirst(), deliveryPoint.getSecond());
        }
        Image image = Image.getInstance(Toolkit.getDefaultToolkit().createImage(mapView.getMap().getSource()), null);
        table.addCell(image);
        return table;
    }

    private void drawDot(Graphics g, Point p, int dwellings) {
        int dotRadius = (int)Math.ceil(Math.sqrt(dwellings * 5));
        g.setColor(Color.YELLOW);
        g.fillOval(p.x - dotRadius, p.y - dotRadius, dotRadius * 2, dotRadius * 2);
        int smallerRadius = dotRadius * 4 / 5;
        g.setColor(Color.RED);
        g.fillOval(p.x - smallerRadius, p.y - smallerRadius, smallerRadius * 2, smallerRadius * 2);
    }

    private Rectangle deriveMapRectangle(List<Duple<Point, Integer>> deliveryPoints, Location meetingPoint) {
        List<Location> locations = new ArrayList<Location>(deliveryPoints.size() + 1);
        if (meetingPoint != null) {
            locations.add(meetingPoint);
        }
        for (Duple<Point, Integer> duple : deliveryPoints) {
            locations.add(new LocationImpl(null, duple.getFirst()));
        }
        return locationFactory.calculatePaddedRectangle(locations);
    }

    private List<Duple<Point, Integer>> getDeliveryPoints(List<RouteMapGrouping> routeMapGroupings) {
        List<Duple<Point, Integer>> deliveryPoints = new ArrayList<Duple<Point, Integer>>();
        for (RouteMapGrouping routeMapGrouping : routeMapGroupings) {
            deliveryPoints.add(new Duple<Point, Integer>(routeMapGrouping.getGeoLocation(), routeMapGrouping.size()));
        }
        Collections.sort(deliveryPoints, new Comparator<Duple<Point, Integer>>() {
            @Override
            public int compare(Duple<Point, Integer> a, Duple<Point, Integer> b) {
                return b.getSecond() - a.getSecond();
            }
        });
        return deliveryPoints;
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
        PdfPTable table = new PdfPTable(new float[] {.925f,.075f});
        table.setWidthPercentage(100f);
        int total = 0;
        dwellingGroups = getNameGroupedGroupings(routeMapGroupings);
        for (Duple<String, List<Location>> dwellingGroup : dwellingGroups) {
            total += dwellingGroup.getSecond().size();
        }
        PdfPCell cell = new PdfPCell(new Phrase(""));
        cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(cell);
        PdfPCell totalDwellings = new PdfPCell(getParagraph(total + "", NORMAL_FONT_SIZE, true, BaseColor.BLACK));
        totalDwellings.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        totalDwellings.setBackgroundColor(CONSERVATIVE_TINT);
        table.addCell(totalDwellings);
        for (Duple<String, List<Location>> dwellingGroup : dwellingGroups) {
            table.addCell(getPhrase(dwellingGroup.getFirst()));
            PdfPCell dwellings = new PdfPCell(getPhrase(dwellingGroup.getSecond().size() + ""));
            dwellings.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            table.addCell(dwellings);
        }
        return table;
    }

    private List<Duple<String, List<Location>>> getNameGroupedGroupings(List<RouteMapGrouping> routeMapGroupings) {
        Map<String, List<Location>> map = new HashMap<String, List<Location>>();
        for (RouteMapGrouping routeMapGrouping : routeMapGroupings) {
            for (DwellingGroup dwellingGroup : routeMapGrouping.getDwellingGroupList()) {
                List<Location> superDwellingGroup = map.get(dwellingGroup.getCommonName());
                if (superDwellingGroup == null) {
                    superDwellingGroup = new ArrayList<Location>();
                    map.put(dwellingGroup.getCommonName(), superDwellingGroup);
                }
                for (Location dwelling : dwellingGroup.getDwellings()) {
                    superDwellingGroup.add(dwelling);
                }
            }
        }
        List<Duple<String, List<Location>>> strings = new ArrayList<Duple<String, List<Location>>>(map.size());
        List<Map.Entry<String, List<Location>>> entries = new ArrayList<Map.Entry<String, List<Location>>>(map.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, List<Location>>>() {
            @Override
            public int compare(Map.Entry<String, List<Location>> a, Map.Entry<String, List<Location>> b) {
                return a.getKey().compareToIgnoreCase(b.getKey());
            }
        });
        String commonEnding = getCommonEnding(map.keySet());
        for (Map.Entry<String, List<Location>> entry : entries) {
            List<Location> dwellings = new ArrayList<Location>(entry.getValue());
            strings.add(new Duple<String, List<Location>>(entry.getKey(), dwellings));
        }
        truncate(strings, commonEnding);
        Collections.sort(strings, new Comparator<Duple<String, List<Location>>>() {
            @Override
            public int compare(Duple<String, List<Location>> o1, Duple<String, List<Location>> o2) {
                return addressComparator.compare(o1.getFirst(), o2.getFirst());
            }
        });
        return strings;
    }

    static String getCommonEnding(Collection<String> strings) {
    String commonEnding = StringExtentions.getCommonEnding(strings);
        int comma = commonEnding.indexOf(',');
        if (comma == -1) {
            return "";
        }
        return commonEnding.substring(comma);
    }

    static void truncate(List<Duple<String, List<Location>>> strings, String commonEnding) {
        for (Duple<String, List<Location>> duple : strings) {
            duple.setFirst(duple.getFirst().substring(0, duple.getFirst().length() - commonEnding.length()));
        }
    }

    private Chunk getChunk(String text, int size) {
      return getChunk(text, size, false, BaseColor.BLACK);
    }

    private Chunk getChunk(String text, int size, boolean bold, BaseColor baseColor) {
      Font font = new Font(conservativeBaseFont, size, bold ? Font.BOLD : Font.NORMAL);
      font.setColor(baseColor);
      return new Chunk(text, font);
    }

    private Phrase getPhrase(String text) {
        return getPhrase(text, NORMAL_FONT_SIZE);
    }

    private Phrase getPhrase(String text, int size) {
        return getParagraph(text, size, false, BaseColor.BLACK);
    }

    private Paragraph getParagraph(String text, int size, boolean bold, BaseColor baseColor) {
        Font font = new Font(conservativeBaseFont, size, bold ? Font.BOLD : Font.NORMAL);
        font.setColor(baseColor);
        return new Paragraph(new Chunk(text, font));
    }
}
