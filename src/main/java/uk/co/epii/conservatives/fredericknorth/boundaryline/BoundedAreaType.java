package uk.co.epii.conservatives.fredericknorth.boundaryline;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 16:21
 */
public enum BoundedAreaType {
    NEIGHBOURHOOD("Neighbourhood", null, null, 40, Color.YELLOW),
    POLLING_DISTRICT("Polling District", NEIGHBOURHOOD, null, 30, Color.GREEN),
    UNITARY_DISTRICT_WARD("Unitary District Ward", POLLING_DISTRICT, "district_borough_unitary_ward_region.shp", 20, Color.BLUE),
    PARLIAMENTARY_CONSTITUENCY("Parliamentary Constituency", POLLING_DISTRICT, "westminster_const_region.shp", 10, Color.RED),
    UNITARY_DISTRICT("Unitary District", UNITARY_DISTRICT_WARD, "district_borough_unitary_region.shp", 10, Color.RED),
    COUNTY_WARD("County Ward", POLLING_DISTRICT, "county_electoral_division_region.shp", 20, Color.BLUE),
    COUNTY("County", COUNTY_WARD, "county_region.shp", 10, Color.RED);

    public static final BoundedAreaType[] orphans;

    static {
        Set<BoundedAreaType> all = new HashSet<BoundedAreaType>(Arrays.asList(values()));
        for (BoundedAreaType boundedAreaType : values()) {
            BoundedAreaType child = boundedAreaType.getChildType();
            if (child != null)
                all.remove(child);
        }
        orphans = all.toArray(new BoundedAreaType[all.size()]);
        Arrays.sort(orphans, new Comparator<BoundedAreaType>() {
            @Override
            public int compare(BoundedAreaType a, BoundedAreaType b) {
                return a.getName().compareTo(b.getName());
            }
        });
    }

    private final String name;
    private final BoundedAreaType childType;
    private final String fileName;
    private final int priority;
    private final Color defaultColour;

    private BoundedAreaType(String name, BoundedAreaType childType, String fileName, int prioirty, Color defaultColour) {
        this.name = name;
        this.childType = childType;
        this.fileName = fileName;
        this.priority = prioirty;
        this.defaultColour = defaultColour;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public BoundedAreaType getChildType() {
        return childType;
    }

    public int getMaximumGenerations() {
        if (childType == null) {
            return 1;
        }
        return childType.getMaximumGenerations() + 1;
    }

    public Color getDefaultColour() {
        return defaultColour;
    }

    public int getPriority() {
        return priority;
    }

    public BoundedAreaType[] getAllPossibleDecendentTypes() {
        List<BoundedAreaType> boundedAreaTypes = new ArrayList<BoundedAreaType>();
        BoundedAreaType type = this;
        do {
            boundedAreaTypes.add(type);
        } while ((type = type.getChildType()) != null);
        return boundedAreaTypes.toArray(new BoundedAreaType[boundedAreaTypes.size()]);
    }
}
