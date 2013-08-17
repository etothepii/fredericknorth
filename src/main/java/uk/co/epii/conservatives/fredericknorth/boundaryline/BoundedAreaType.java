package uk.co.epii.conservatives.fredericknorth.boundaryline;

import java.util.*;

/**
 * User: James Robinson
 * Date: 21/07/2013
 * Time: 16:21
 */
public enum BoundedAreaType {

    NEIGHBOURHOOD("Neighbourhood", null, null),
    POLLING_DISTRICT("Polling District", NEIGHBOURHOOD, null),
    UNITARY_DISTRICT_WARD("Unitary District Ward", POLLING_DISTRICT, "district_borough_unitary_ward_region.shp"),
    PARLIAMENTARY_CONSTITUENCY("Parliamentary Constituency", POLLING_DISTRICT, "westminster_const_region.shp"),
    UNITARY_DISTRICT("Unitary District", UNITARY_DISTRICT_WARD, "district_borough_unitary_region.shp"),
    COUNTY_WARD("County Ward", POLLING_DISTRICT, "county_electoral_division_region.shp"),
    COUNTY("County", COUNTY_WARD, "county_region.shp");

    public static final BoundedAreaType[] orphans;

    static {
        Set<BoundedAreaType> all = new HashSet<BoundedAreaType>(Arrays.asList(values()));
        for (BoundedAreaType boundedAreaType : values()) {
            BoundedAreaType child = boundedAreaType.getChildType();
            if (child != null)
                all.remove(child);
        }
        orphans = all.toArray(new BoundedAreaType[all.size()]);
    }

    private final String name;
    private final BoundedAreaType childType;
    private final String fileName;

    private BoundedAreaType(String name, BoundedAreaType childType, String fileName) {
        this.name = name;
        this.childType = childType;
        this.fileName = fileName;
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

    public int getPriority() {
        return 0;
    }

    public BoundedAreaType[] getChildTypes() {
        List<BoundedAreaType> boundedAreaTypes = new ArrayList<BoundedAreaType>();
        BoundedAreaType type = this;
        do {
            boundedAreaTypes.add(type);
        } while ((type = type.getChildType()) != null);
        return boundedAreaTypes.toArray(new BoundedAreaType[boundedAreaTypes.size()]);
    }
}
