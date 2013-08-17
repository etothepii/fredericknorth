package uk.co.epii.conservatives.fredericknorth.utilities;

/**
 * User: James Robinson
 * Date: 12/08/2013
 * Time: 23:34
 */
public class NullProgressTracker implements ProgressTracker {

    private final Object sync = new Object();

    @Override
    public void setSteps(int n) {}

    @Override
    public void setStep(int n) {}

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
}
