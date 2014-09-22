package uk.co.epii.conservatives.fredericknorth.reports;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedArea;
import uk.co.epii.conservatives.fredericknorth.boundaryline.BoundedAreaType;
import uk.co.epii.conservatives.fredericknorth.geometry.NearestPoint;
import uk.co.epii.conservatives.fredericknorth.serialization.XMLSerializer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 05/08/2013
 * Time: 22:53
 */
public class DwellingCountReportBuilderImplTest {

    @Test
    public void reverseTestEven() {
        Integer[] values = new Integer[] {1,2,3,4};
        DwellingCountReportBuilderImpl.reverse(values);
        assertEquals(4, (int)values[0]);
        assertEquals(3, (int)values[1]);
        assertEquals(2, (int)values[2]);
        assertEquals(1, (int)values[3]);
    }

    @Test
    public void reverseTestOdd() {
        Integer[] values = new Integer[] {1,2,3,4, 5};
        DwellingCountReportBuilderImpl.reverse(values);
        assertEquals(5, (int)values[0]);
        assertEquals(4, (int)values[1]);
        assertEquals(3, (int)values[2]);
        assertEquals(2, (int)values[3]);
        assertEquals(1, (int)values[4]);
    }

    @Test
    public void flattenTest() {
        DummyBoundedArea TH = new DummyBoundedArea("TH");
        DummyBoundedArea BCT = new DummyBoundedArea("BCT");
        DummyBoundedArea PD1 = new DummyBoundedArea("PD1");
        DummyBoundedArea PD2 = new DummyBoundedArea("PD2");
        DummyBoundedArea PD3 = new DummyBoundedArea("PD3");
        DummyBoundedArea PD2_N1 = new DummyBoundedArea("N1");
        DummyBoundedArea PD2_N2 = new DummyBoundedArea("N2");
        DummyBoundedArea PD2_N3 = new DummyBoundedArea("N3");
        DummyBoundedArea PD3_N1 = new DummyBoundedArea("N1");
        DummyBoundedArea PD3_N2 = new DummyBoundedArea("N2");
        DummyBoundedArea PD3_N3 = new DummyBoundedArea("N3");
        DummyBoundedArea PD3_N4 = new DummyBoundedArea("N4");
        DummyBoundedArea PD3_N5 = new DummyBoundedArea("N5");
        TH.setBoundedAreas(new DummyBoundedArea[] {BCT});
        BCT.setBoundedAreas(new DummyBoundedArea[] {PD1, PD2, PD3});;
        PD2.setBoundedAreas(new DummyBoundedArea[] {PD2_N1, PD2_N2, PD2_N3});
        PD3.setBoundedAreas(new DummyBoundedArea[] {PD3_N1, PD3_N2, PD3_N3, PD3_N4, PD3_N5});
        DwellingCountReportBuilder builder = new DwellingCountReportBuilderImpl(null, null);
        List<BoundedArea> boundedAreaList = builder.flatten(TH);
        StringBuilder stringBuilder = new StringBuilder(128);
        for (BoundedArea boundedArea : boundedAreaList) {
            stringBuilder.append(boundedArea.getName());
            stringBuilder.append(" ");
        }
        assertEquals("TH BCT PD1 PD2 N1 N2 N3 PD3 N1 N2 N3 N4 N5 ", stringBuilder.toString());

    }

    private class DummyBoundedArea implements BoundedArea {

        private String name;
        private BoundedArea[] boundedAreas;

        private DummyBoundedArea(String name) {
            this.name = name;
            boundedAreas = new BoundedArea[]{};
        }

        public DummyBoundedArea(String name, BoundedArea[] boundedAreas) {
            this.name = name;
            this.boundedAreas = boundedAreas;
        }

      @Override
      public UUID getUuid() {
        return UUID.fromString("0000000");
      }

      @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public BoundedArea[] getChildren() {
            return boundedAreas;
        }

        public void setBoundedAreas(BoundedArea[] boundedAreas) {
            this.boundedAreas = boundedAreas;
        }

        @Override
        public BoundedAreaType getBoundedAreaType() {
            throw new UnsupportedOperationException("This method is not supported in this Dummy");
        }

        @Override
        public Polygon[] getAreas() {
            throw new UnsupportedOperationException("This method is not supported in this Dummy");
        }

        @Override
        public Polygon[] getEnclaves() {
            throw new UnsupportedOperationException("This method is not supported in this Dummy");
        }

        @Override
        public Element toXml(Document document) {
            throw new UnsupportedOperationException("This method is not supported in this Dummy");
        }

        @Override
        public void addChild(BoundedArea boundedAreas) {
            throw new UnsupportedOperationException("This method is not supported in this Dummy");
        }

        @Override
        public void save(XMLSerializer xmlSerializer, File selectedFile) {
            throw new UnsupportedOperationException("This method is not supported in this Dummy");
        }

        @Override
        public NearestPoint getNearestGeoPoint(Point2D.Float point) {
            throw new UnsupportedOperationException("This method is not supported in this Dummy");
        }
    }

}
