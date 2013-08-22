package uk.co.epii.conservatives.fredericknorth.gui.routebuilder;

import uk.co.epii.conservatives.fredericknorth.routes.Ward;

import java.text.NumberFormat;

/**
 * User: James Robinson
 * Date: 06/07/2013
 * Time: 10:20
 */
class WardRenderer extends LeftRightRenderer {

    private NumberFormat integerNumberFormat = NumberFormat.getInstance();

    private String getFormattedRoutedProportion(int dwellingCount, int unroutedDwellingCount) {
        int assigned = dwellingCount - unroutedDwellingCount;
        StringBuilder stringBuilder = new StringBuilder(16);
        stringBuilder.append(integerNumberFormat.format(assigned));
        stringBuilder.append(" / ");
        stringBuilder.append(integerNumberFormat.format(dwellingCount));
        return stringBuilder.toString();
    }

    @Override
    protected String[] getLeftAndRight(Object value) {
        if (value == null) {
            return new String[2];
        }
        if (!(value instanceof Ward)) {
            throw new IllegalArgumentException("WardRenderer can only Render wards");
        }
        Ward ward = (Ward)value;
        return new String[] {
            ward.getName(),
            getFormattedRoutedProportion(ward.getDwellingCount(), ward.getUnroutedDwellingCount())
        };
    }
}
