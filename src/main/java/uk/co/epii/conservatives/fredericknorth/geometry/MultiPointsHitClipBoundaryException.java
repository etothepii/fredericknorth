package uk.co.epii.conservatives.fredericknorth.geometry;

/**
 * User: James Robinson
 * Date: 28/02/2014
 * Time: 00:22
 */
public class MultiPointsHitClipBoundaryException extends RuntimeException {

    public MultiPointsHitClipBoundaryException() {
        super();
    }
    public MultiPointsHitClipBoundaryException(String message) {
        super(message);
    }
    public MultiPointsHitClipBoundaryException(String message, Throwable t) {
        super(message, t);
    }
    public MultiPointsHitClipBoundaryException(Throwable t) {
        super(t);
    }
}
