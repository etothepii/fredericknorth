package uk.co.epii.conservatives.fredericknorth.maps;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * User: James Robinson
 * Date: 30/06/2013
 * Time: 14:11
 */
public class CornerTest {

    @Test
    public void checkAllCornerCombinationsCreatedTest() {
        HashSet<CornerGroup> cornerGroups = new HashSet<CornerGroup>();
        for (Corner[] corners : Corner.allOrders) {
            cornerGroups.add(new CornerGroup(corners));
        }
        assertEquals(24, cornerGroups.size());
    }

    private class CornerGroup {

        public final Corner a, b, c, d;

        public CornerGroup(Corner[] corners) {
            a = corners[0];
            b = corners[1];
            c = corners[2];
            d = corners[3];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CornerGroup that = (CornerGroup) o;

            if (a != that.a) return false;
            if (b != that.b) return false;
            if (c != that.c) return false;
            if (d != that.d) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = a != null ? a.hashCode() : 0;
            result = 31 * result + (b != null ? b.hashCode() : 0);
            result = 31 * result + (c != null ? c.hashCode() : 0);
            result = 31 * result + (d != null ? d.hashCode() : 0);
            return result;
        }
    }
}
