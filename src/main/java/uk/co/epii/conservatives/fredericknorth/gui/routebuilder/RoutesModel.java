package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.opendata.DwellingGroup;
import uk.co.epii.conservatives.fredericknorth.routes.Route;
import uk.co.epii.conservatives.fredericknorth.routes.Ward;

import javax.swing.*;
import java.util.*;

/**
 * User: James Robinson
 * Date: 02/07/2013
 * Time: 20:24
 */
class RoutesModel extends AbstractListModel implements ComboBoxModel {
    private final RouteBuilderMapFrameModel routeBuilderMapFrameModel;
    private final List<Route> routes;
    private final Set<String> routeNames;
    private final HashMap<Route, Integer> routesIndexMap;
    private Ward selectedWard;
    private int currentIndex;

    public RoutesModel(RouteBuilderMapFrameModel routeBuilderMapFrameModel) {
        this.routeBuilderMapFrameModel = routeBuilderMapFrameModel;
        routesIndexMap = new HashMap<Route, Integer>();
        routes = new ArrayList<Route>();
        routeNames = new HashSet<String>();
        currentIndex = -1;
        selectedWard = null;
    }

    @Override
    public void setSelectedItem(Object o) {
        if (!(o instanceof Route)) {
            throw new IllegalArgumentException("The item so provided must be of type Route");
        }
        setSelectedRoute((Route) o);
    }

    @Override
    public Route getSelectedItem() {
        if (currentIndex >= 0) {
            return routes.get(currentIndex);
        }
        return null;
    }

    @Override
    public int getSize() {
        if (selectedWard == null) {
            return 0;
        }
        return selectedWard.getRoutes().size();
    }

    @Override
    public Route getElementAt(int i) {
        return routes.get(i);
    }

    public void setSelectedWard(Ward selectedWard) {
        int oldSize = getSize();
        this.selectedWard = selectedWard;
        routes.clear();
        routeBuilderMapFrameModel.getUnroutedDwellingGroups().clear();
        if (selectedWard != null) {
            routeBuilderMapFrameModel.getUnroutedDwellingGroups().setToContentsOf(selectedWard.getUnroutedDwellingGroups());
            routes.addAll(this.selectedWard.getRoutes());
        }
        updateRoutesIndexMap();
        setSelectedRoute(getSize() == 0 ? null : routes.get(0));
        fireDataChanged(oldSize);
    }

    private void fireDataChanged(int oldSize) {
        int lastChangedIndex = Math.min(oldSize, getSize()) - 1;
        fireContentsChanged(this, 0, lastChangedIndex);
        if (oldSize > getSize()) {
            fireIntervalRemoved(this, getSize(), oldSize - 1);
        }
        if (oldSize < getSize()) {
            fireIntervalRemoved(this, oldSize, getSize() - 1);
        }
    }

    private void updateRoutesIndexMap() {
        Collections.sort(this.routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        routesIndexMap.clear();
        routeNames.clear();
        int index = 0;
        for (Route route : this.routes) {
            routesIndexMap.put(route, index++);
            routeNames.add(route.getName());
        }
    }

    public void add(String routeName) {
        Route route = selectedWard.createRoute(routeName);
        routes.add(route);
        updateRoutesIndexMap();
        setSelectedItem(route);
        int insertedAt = routesIndexMap.get(route);
        fireIntervalAdded(this, insertedAt, insertedAt);
    }

    public void rename(String routeName) {
        if (getSelectedItem() != null) {
            getSelectedItem().setName(routeName);
            fireContentsChanged(this, currentIndex, currentIndex);
        }
    }

    public void delete() {
        routes.remove(currentIndex);
        updateRoutesIndexMap();
        currentIndex = Math.min(currentIndex, routes.size() - 1);
        fireIntervalRemoved(this, currentIndex, currentIndex);
    }

    public void setSelectedRoute(Route selectedRoute) {
        if (selectedRoute == null) {
            currentIndex = -1;
            routeBuilderMapFrameModel.getRoutedDwellingGroups().clear();
        }
        else if (routesIndexMap.containsKey(selectedRoute)) {
            currentIndex = routesIndexMap.get(selectedRoute);
            routeBuilderMapFrameModel.getRoutedDwellingGroups().setToContentsOf(getSelectedItem().getDwellingGroups());
        }
        else {
            throw new IllegalArgumentException("Unknown Route: " + selectedRoute.getName());
        }
    }

    public String getNextSuggestedRouteName() {
        for (int i = 1; i <= routes.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder(32);
            stringBuilder.append("Route ");
            stringBuilder.append(i);
            String suggestedRouteName = stringBuilder.toString();
            if (!routeNames.contains(suggestedRouteName))
                return suggestedRouteName;
        }
        StringBuilder stringBuilder = new StringBuilder(32);
        stringBuilder.append("Route ");
        stringBuilder.append(routes.size() + 1);
        return stringBuilder.toString();
    }

    public void moveInToRoute(List<? extends DwellingGroup> dwellingGroups) {
        Set<DwellingGroup> toMove = routeBuilderMapFrameModel.getUnroutedDwellingGroups().intersect(dwellingGroups);
        routeBuilderMapFrameModel.getUnroutedDwellingGroups().removeAll(toMove);
        routeBuilderMapFrameModel.getRoutedDwellingGroups().addAll(toMove);
        getSelectedItem().addDwellingGroups(toMove);
    }

    public void moveOutOfRoute(List<? extends DwellingGroup> dwellingGroups) {
        Set<DwellingGroup> toMove = routeBuilderMapFrameModel.getRoutedDwellingGroups().intersect(dwellingGroups);
        routeBuilderMapFrameModel.getRoutedDwellingGroups().removeAll(toMove);
        routeBuilderMapFrameModel.getUnroutedDwellingGroups().addAll(toMove);
        getSelectedItem().removeDwellingGroups(toMove);
    }

    public void updateSelected() {
        fireContentsChanged(this, currentIndex, currentIndex);
    }

    public List<Route> getUnselectedRoutes() {
        List<Route> routes = new ArrayList<Route>(this.routes.size());
        for (int i = 0; i < routes.size(); i++) {
            if (i != currentIndex) {
                routes.add(this.routes.get(i));
            }
        }
        return routes;
    }
}
