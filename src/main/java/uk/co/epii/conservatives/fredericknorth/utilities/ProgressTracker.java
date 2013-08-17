package uk.co.epii.conservatives.fredericknorth.utilities;

/**
 * User: James Robinson
 * Date: 09/08/2013
 * Time: 19:26
 */
public interface ProgressTracker {

    public void setSteps(int n);
    public void setStep(int n);
    public void startSubsection(int steps);
    public void increment(String message, int n);
    public void increment(String message);
    public void increment(int n);
    public void increment();
    public void setMessage(String message);
    public Object getSync();
    public void finish();
}
