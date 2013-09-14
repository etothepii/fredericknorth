package uk.co.epii.conservatives.fredericknorth.utilities;

/**
 * User: James Robinson
 * Date: 12/08/2013
 * Time: 23:34
 */
public class NullProgressTracker implements ProgressTracker {

    public static final NullProgressTracker NULL = new NullProgressTracker();

    private final Object sync = new Object();

    @Override
    public void startSubsection(int steps) {}

    @Override
    public void increment(String message, int n) {}

    @Override
    public void increment(String message) {}

    @Override
    public void increment(int n) {}

    @Override
    public void increment() {}

    @Override
    public void setMessage(String message) {}

    @Override
    public Object getSync() {
        return sync;
    }

    @Override
    public void finish() {}

    @Override
    public boolean isAtEnd() {
        return false;
    }

    @Override
    public void endSubsection() {}

    @Override
    public int getMaximum() {return 1;}

    @Override
    public int getValue() {
        return 0;
    }

    @Override
    public boolean isIndeterminate() {
        return false;
    }
}
