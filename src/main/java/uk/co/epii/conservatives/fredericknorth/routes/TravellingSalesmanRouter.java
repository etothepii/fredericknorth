package uk.co.epii.conservatives.fredericknorth.routes;

import jsprit.core.algorithm.VehicleRoutingAlgorithm;
import jsprit.core.algorithm.box.SchrimpfFactory;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.job.Job;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.solution.route.activity.TourActivity;
import jsprit.core.util.Coordinate;
import jsprit.core.util.Solutions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
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

  @Override
  public List<Route> createRoutes(RoutableArea routing, Collection<IndivisbleChunk> chunks, int targetSize) {
    reset(routing);
    groupByLocation(chunks);
    removeOverLarge(targetSize);
    Vehicle vehicle = buildVehicle(targetSize);
    List<Service> services = createServices();
    VehicleRoutingProblem problem = createProblem(Arrays.asList(vehicle), services);
    VehicleRoutingProblemSolution solution = solve(problem);
    extractRoutes(solution);
    removeCommonEndings();
    return routes;
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

  private VehicleRoutingProblemSolution solve(VehicleRoutingProblem problem) {
    VehicleRoutingAlgorithm algorithm = new SchrimpfFactory().createAlgorithm(problem);
    Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();
    return Solutions.bestOf(solutions);
  }

  private VehicleRoutingProblem createProblem(Collection<Vehicle> vehicles, List<Service> services) {
    VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
    vrpBuilder.addAllVehicles(vehicles);
    vrpBuilder.addAllJobs(services);
    return vrpBuilder.build();
  }

  private List<Service> createServices() {
    List<Service> services = new ArrayList<Service>(colocatedChunks.size());
    for (Map.Entry<Point, Duple<List<IndivisbleChunk>, Double>> entry : colocatedChunks.entrySet()) {
      Point point = entry.getKey();
      Service.Builder builder = Service.Builder.newInstance(pointsToNodes.get(point));
      builder.addSizeDimension(WEIGHT_INDEX, entry.getValue().getSecond().intValue());
      builder.setCoord(Coordinate.newInstance(point.x, point.y));
      services.add(builder.build());
    }
    return services;
  }

  private Vehicle buildVehicle(int targetSize) {
    VehicleTypeImpl.Builder vehicleTypeBuilder =
            VehicleTypeImpl.Builder.newInstance("vehicleType");
    vehicleTypeBuilder.addCapacityDimension(WEIGHT_INDEX, targetSize);
    VehicleType vehicleType = vehicleTypeBuilder.build();
    VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
    Point2D cog = PolygonExtensions.getCentreOfGravity(routing.getBoundedArea().getAreas());
    vehicleBuilder.setStartLocationCoordinate(Coordinate.newInstance((int)cog.getX(), (int)cog.getY()));
    vehicleBuilder.setType(vehicleType);
    return vehicleBuilder.build();
  }

  private void removeOverLarge(int targetSize) {
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
