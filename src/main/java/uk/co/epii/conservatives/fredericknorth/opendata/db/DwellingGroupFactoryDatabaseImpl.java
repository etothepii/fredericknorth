package uk.co.epii.conservatives.fredericknorth.opendata.db;

import org.apache.log4j.Logger;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PointExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroupFactory;
import uk.co.epii.politics.williamcavendishbentinck.DatabaseSession;
import uk.co.epii.politics.williamcavendishbentinck.stubs.StubDeliveryPointAddress;
import uk.co.epii.politics.williamcavendishbentinck.tables.BLPU;
import uk.co.epii.politics.williamcavendishbentinck.tables.DeliveryPointAddress;
import uk.co.epii.spencerperceval.tuple.Duple;
import uk.co.epii.spencerperceval.util.Group;
import uk.co.epii.spencerperceval.util.Grouper;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/**
 * User: James Robinson
 * Date: 03/11/2013
 * Time: 11:36
 */
public class DwellingGroupFactoryDatabaseImpl implements DwellingGroupFactory {

    private static final Logger LOG = Logger.getLogger(DwellingGroupFactory.class);

    private final Map<Point, Collection<DwellingGroupDatabaseImpl>> loaded =
            new HashMap<Point, Collection<DwellingGroupDatabaseImpl>>();
    private final Set<Rectangle> loadedAreas = new HashSet<Rectangle>();
    private Grouper<StubDeliveryPointAddress> deliveryPointAddressGrouper = new Grouper<StubDeliveryPointAddress>();
    private DatabaseSession databaseSession;

    public void setDatabaseSession(DatabaseSession databaseSession) {
        this.databaseSession = databaseSession;
    }

    @Override
    public Collection<? extends DwellingGroup> getDwellingGroups(Rectangle bounds) {
        if (previouslyLoaded(bounds)) {
          return fromCache(bounds);
        }
        Map<Point, List<DeliveryPointAddress>> dwellings = new HashMap<Point, List<DeliveryPointAddress>>();
        for(Duple<BLPU, DeliveryPointAddress> dwelling :
                databaseSession.containedWithin(bounds, BLPU.class, DeliveryPointAddress.class, "UPRN", "UPRN")) {
          Point point = PointExtensions.fromFloat(
                  new Point2D.Float(dwelling.getFirst().getXCoordinate(), dwelling.getFirst().getYCoordinate()));
          List<DeliveryPointAddress> colocated = dwellings.get(point);
          if (colocated == null) {
            colocated = new ArrayList<DeliveryPointAddress>();
            dwellings.put(point, colocated);
          }
          if (dwelling.getSecond() == null) {
            throw new NullPointerException();
          }
          colocated.add(dwelling.getSecond());
        }
        loadedAreas.add(bounds);
        return extractAndCache(dwellings);
    }

  @Override
  public DwellingGroup load(Point point, String dwellingGroupKey) {
    getDwellingGroups(new Rectangle(point, new Dimension(1, 1)));
    Collection<DwellingGroupDatabaseImpl> dwellingGroups = loaded.get(point);
    if (dwellingGroups == null) {
      return null;
    }
    for (DwellingGroup dwellingGroup : dwellingGroups) {
      if (dwellingGroup.getKey().equals(dwellingGroupKey)) {
        return dwellingGroup;
      }
    }
    return null;
  }

  private Collection<? extends DwellingGroup> fromCache(Rectangle bounds) {
    ArrayList<DwellingGroup> dwellingGroups = new ArrayList<DwellingGroup>();
    for (Map.Entry<Point, Collection<DwellingGroupDatabaseImpl>> entry : loaded.entrySet()) {
      if (bounds.contains(entry.getKey())) {
        dwellingGroups.addAll(entry.getValue());
      }
    }
    return dwellingGroups;
  }

  private boolean previouslyLoaded(Rectangle bounds) {
    for (Rectangle loaded : loadedAreas) {
      if (loaded.contains(bounds)) {
        return true;
      }
    }
    return false;
  }

  private Collection<? extends DwellingGroup> extractAndCache(Map<Point,List<DeliveryPointAddress>> dwellings) {
    List<DwellingGroupDatabaseImpl> dwellingGroups = new ArrayList<DwellingGroupDatabaseImpl>();
    for (Map.Entry<Point, List<DeliveryPointAddress>> colocated : dwellings.entrySet()) {
      dwellingGroups.addAll(extractAndCache(colocated.getKey(), colocated.getValue()));
    }
    return dwellingGroups;
  }

  private Collection<? extends DwellingGroupDatabaseImpl> extractAndCache(Point point, List<DeliveryPointAddress> deliveryPointAddresses) {
    List<DwellingGroupDatabaseImpl> dwellingGroups = new ArrayList<DwellingGroupDatabaseImpl>();
    Map<StubDeliveryPointAddress, DeliveryPointAddress> stubs = new HashMap<StubDeliveryPointAddress, DeliveryPointAddress>(deliveryPointAddresses.size());
    for (DeliveryPointAddress deliveryPointAddress : deliveryPointAddresses) {
      stubs.put(new StubDeliveryPointAddress(deliveryPointAddress), deliveryPointAddress);
    }
    for (Group<StubDeliveryPointAddress> groupedByName : deliveryPointAddressGrouper.group(stubs.keySet())) {
      List<DwellingDatabaseImpl> dwellings = new ArrayList<DwellingDatabaseImpl>();
      StubDeliveryPointAddress common = groupedByName.getCommon();
      for (StubDeliveryPointAddress stub : groupedByName) {
        dwellings.add(new DwellingDatabaseImpl(stub.getDifference(common), point, stubs.get(stub)));
      }
      DwellingGroupDatabaseImpl dwellingGroupDatabase = new DwellingGroupDatabaseImpl(dwellings, common.toString(), point);
      dwellingGroups.add(dwellingGroupDatabase);
    }
    loaded.put(point, dwellingGroups);
    return dwellingGroups;
  }


}
