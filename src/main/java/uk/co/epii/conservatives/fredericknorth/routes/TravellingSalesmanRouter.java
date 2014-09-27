package uk.co.epii.conservatives.fredericknorth.routes;

import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.SchrimpfFactory;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.spencerperceval.tuple.Duple;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;
import jsprit.core.problem.vehicle.*;

/**
 * User: James Robinson
 * Date: 17/09/2014
 * Time: 20:53
 */
public class TravellingSalesmanRouter extends AbstractRouter {

  private static final Logger LOG = LoggerFactory.getLogger(TravellingSalesmanRouter.class);

  private int WEIGHT_INDEX = 0;
  
  private Map<Point, Duple<List<IndivisbleChunk>, Double>> colocatedChunks;
  private Map<Point, String> pointsToNodes;
  private Map<String, Point> nodesToPoints;
  private Rectangle bounds;
  private int targetSize;

  @Override
  public List<Route> createRoutes(RoutableArea routing, Collection<IndivisbleChunk> chunks, int targetSize) {
    reset(routing);
    this.targetSize = targetSize;
    groupByLocation(chunks);
    removeOverLarge();
    List<Service> services = createServices();
    List<Vehicle> vehicles = buildVehicles();
    VehicleRoutingProblem problem = createProblem(vehicles, services);
    VehicleRoutingProblemSolution solution = solve(problem, 2048);
    extractRoutes(solution);
    LOG.debug("Cars Used: {}/{}", solution.getRoutes().size(), vehicles.size());
    removeCommonEndings();
    return routes;
  }

  private List<Vehicle> buildVehicles() {
    double totalDwellings = 0;
    for (Duple<List<IndivisbleChunk>, Double> duple : colocatedChunks.values()) {
      totalDwellings += duple.getSecond();
    }
    int vehicles = (int)Math.ceil(totalDwellings / targetSize);
    List<Vehicle> vehiclesList = new ArrayList<Vehicle>(vehicles);
    for (int i = 0; i < vehicles; i++) {
      vehiclesList.add(buildVehicle(targetSize * 3 / 2, false, getRandomPointWithinBounds()));
    }
    return vehiclesList;
  }

  private void extractRoutes(VehicleRoutingProblemSolution solution) {
    for (VehicleRoute vehicleRoute : solution.getRoutes()) {
      List<IndivisbleChunk> chunksDelivered = new ArrayList<IndivisbleChunk>();
      for (Job job : vehicleRoute.getTourActivities().getJobs()) {
        String name = job.getId();
        LOG.debug(name);
        Point point = nodesToPoints.get(name);
        chunksDelivered.addAll(colocatedChunks.get(point).getFirst());
      }
      createRoute(chunksDelivered);
    }
  }

  private VehicleRoutingProblemSolution solve(VehicleRoutingProblem problem, int iterations) {
    VehicleRoutingAlgorithm algorithm = new SchrimpfFactory().createAlgorithm(problem);
    algorithm.setMaxIterations(iterations);
    Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
    return Solutions.bestOf(solutions);
  }

  private VehicleRoutingProblem createProblem(Collection<Vehicle> vehicles, List<Service> services) {
    VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
    vrpBuilder.addAllVehicles(vehicles);
    vrpBuilder.addAllJobs(services);
    vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.FINITE);
    return vrpBuilder.build();
  }

  private List<Service> createServices() {
    List<Service> services = new ArrayList<Service>(colocatedChunks.size());
    for (Map.Entry<Point, Duple<List<IndivisbleChunk>, Double>> entry : colocatedChunks.entrySet()) {
      Point point = entry.getKey();
      Service.Builder builder = Service.Builder.newInstance(pointsToNodes.get(point));
      builder.addSizeDimension(WEIGHT_INDEX, entry.getValue().getSecond().intValue());
      builder.setServiceTime(calculateServiceTime(entry.getValue().getFirst()));
      builder.setCoord(Coordinate.newInstance(point.x, point.y));
      services.add(builder.build());
    }
    return services;
  }

  private double calculateServiceTime(List<IndivisbleChunk> chunks) {
    StringBuilder postcodes = new StringBuilder();
    Set<String> postcodesSet = new HashSet<String>();
    Vehicle vehicle = buildVehicle(targetSize, true, chunks.get(0).getMedian());
    List<Service> services = new ArrayList<>();
    for (IndivisbleChunk chunk : chunks) {
      for (DwellingGroup dwellingGroup : chunk.getDwellingGroups()) {
        for (String postcode : dwellingGroup.getPostcodes()) {
          if (postcodesSet.add(postcode)) {
            postcodes.append(postcode);
            postcodes.append(" ");
          }
        }
        Service.Builder builder = Service.Builder.newInstance("Building " + (services.size() + 1));
        builder.addSizeDimension(WEIGHT_INDEX, dwellingGroup.size());
        builder.setServiceTime(dwellingGroup.size());
        builder.setCoord(Coordinate.newInstance(dwellingGroup.getPoint().x, dwellingGroup.getPoint().y));
        services.add(builder.build());
      }
    }
    VehicleRoutingProblem problem = createProblem(Arrays.asList(vehicle), services);
    VehicleRoutingProblemSolution solution = solve(problem, 16);
    if (solution.getRoutes().size() != 1) {
      throw new RuntimeException("Multiple routes created to deliver one chunk");
    }
    VehicleRoute vehicleRoute = solution.getRoutes().iterator().next();
    double time = vehicleRoute.getEnd().getArrTime() - vehicleRoute.getStart().getArrTime();
    LOG.debug("Delivered {} in {}", postcodes, time);
    return time;
  }

  private Point getRandomPointWithinBounds() {
    return new Point (
            (int)(Math.random() * bounds.width + bounds.x), (int)(Math.random() * bounds.height + bounds.y));
  }

  private Vehicle buildVehicle(int targetSize, boolean withinChunk, Point depot) {
    VehicleTypeImpl.Builder vehicleTypeBuilder =
            VehicleTypeImpl.Builder.newInstance("vehicleType");
    vehicleTypeBuilder.addCapacityDimension(WEIGHT_INDEX, targetSize);
    vehicleTypeBuilder.setFixedCost(0);
    vehicleTypeBuilder.setCostPerDistance(100);
    vehicleTypeBuilder.setCostPerTime(0);
    vehicleTypeBuilder.setMaxVelocity(withinChunk ? 1 : 2);
    VehicleType vehicleType = vehicleTypeBuilder.build();
    VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(UUID.randomUUID().toString());
    vehicleBuilder.setStartLocationCoordinate(Coordinate.newInstance(depot.x, depot.y));
    vehicleBuilder.setType(vehicleType);
    return vehicleBuilder.build();
  }

  private void removeOverLarge() {
    for (Point point : new ArrayList<Point>(colocatedChunks.keySet())) {
      Duple<List<IndivisbleChunk>, Double> duple = colocatedChunks.get(point);
      if (duple.getSecond() > targetSize) {
        colocatedChunks.remove(point);
        createRoute(duple.getFirst());
      }
    }
  }

  @Override
  protected void reset(RoutableArea routing) {
    super.reset(routing);
    bounds = PolygonExtensions.getBounds(routing.getBoundedArea().getAreas());
    colocatedChunks = new HashMap<Point, Duple<List<IndivisbleChunk>, Double>>();
    pointsToNodes = new HashMap<Point, String>();
    nodesToPoints = new HashMap<String, Point>();
  }

  private void groupByLocation(Collection<IndivisbleChunk> chunks) {
    for (IndivisbleChunk chunk : chunks) {
      Duple<List<IndivisbleChunk>, Double> duple = colocatedChunks.get(chunk.getMedian());
      if (duple == null) {
        duple = new Duple<List<IndivisbleChunk>, Double>(new ArrayList<IndivisbleChunk>(), 0d);
        colocatedChunks.put(chunk.getMedian(), duple);
        String node = "NODE" + colocatedChunks.size();
        pointsToNodes.put(chunk.getMedian(), node);
        nodesToPoints.put(node, chunk.getMedian());
      }
      duple.getFirst().add(chunk);
      duple.setSecond(duple.getSecond() + chunk.size());
    }
  }


}
