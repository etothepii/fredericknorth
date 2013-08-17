package uk.co.epii.conservatives.fredericknorth.routebuildergui;

import uk.co.epii.conservatives.fredericknorth.routes.Route;

/**
 * User: James Robinson
 * Date: 06/07/2013
 * Time: 10:20
 */
class RouteRenderer extends LeftRightRenderer {

    @Override
    protected String[] getLeftAndRight(Object value) {
        if (value == null) {
            return new String[] {"", ""};
        }
        if (!(value instanceof Route)) {
            throw new IllegalArgumentException("Route Renderer can only render objects of tyoe Route");
        }
        Route route = (Route)value;
        return new String[] {
                route.getName(),
                integerNumberFormat.format(route.getDwellingCount())
        };
    }
}
