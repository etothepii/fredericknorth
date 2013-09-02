package uk.co.epii.conservatives.fredericknorth.utilities;

import java.util.ArrayList;

/**
 * User: James Robinson
 * Date: 02/09/2013
 * Time: 21:39
 */
public class CancellationToken {

    private boolean cancelled = true;
    private final ArrayList<Runnable> toRun = new ArrayList<Runnable>();

    public boolean isCancelled() {
        synchronized (this) {
            return cancelled;
        }
    }

    public synchronized void cancel() {
        synchronized (this) {
            if (isCancelled()) {
                return;
            }
            cancelled = true;
        }
        synchronized (toRun) {
            for (Runnable runnable : toRun) {
                runnable.run();
            }
        }
    }

    public void register(Runnable runnable) {
        synchronized (toRun) {
            toRun.add(runnable);
        }
    }

    public void unregister(Runnable runnable) {
        synchronized (toRun) {
            toRun.remove(runnable);
        }
    }

}
