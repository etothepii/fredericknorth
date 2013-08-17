package uk.co.epii.conservatives.fredericknorth.maps.gui;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 17:26
 */
public class MapPanelMouseTracker {


    private final Object sync;
    private Point lastSeenAt;
    private long consideredStableAt;
    private final Thread trackMouseThread;
    private final long stationaryMouseRequirement;
    private final ArrayList<MouseStableListener> listeners;

    public MapPanelMouseTracker(long stationaryMouseRequirement) {
        listeners = new ArrayList<MouseStableListener>();
        this.stationaryMouseRequirement = stationaryMouseRequirement;
        sync = new Object();
        trackMouseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    long sleepFor;
                    Point stablePoint;
                    synchronized (sync) {
                        sleepFor = consideredStableAt - System.currentTimeMillis();
                        stablePoint = lastSeenAt;
                    }
                    if (sleepFor > 0) {
                        try {
                            Thread.sleep(sleepFor);
                        }
                        catch (InterruptedException ie) {
                            break;
                        }
                    }
                    else {
                        if (stablePoint != null) {
                            mouseStable(stablePoint);
                        }
                        synchronized (sync) {
                            try {
                                sync.wait();
                            }
                            catch (InterruptedException ie) {
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

    public void start() {
        trackMouseThread.start();
    }

    public void interrupted() {
        trackMouseThread.interrupt();
    }

    private void mouseStable(Point stablePoint) {
        MouseStableEvent e = new MouseStableEvent(this, stablePoint);
        synchronized (listeners) {
            for (MouseStableListener listener : listeners) {
                listener.mouseStable(e);
            }
        }
    }

    public void addMouseStableListener(MouseStableListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public void removeMouseStableListener(MouseStableListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    public void setMouseLocation(Point point) {
        synchronized (sync) {
            if (lastSeenAt != null && lastSeenAt.equals(point)) {
                return;
            }
            lastSeenAt = point;
            consideredStableAt = System.currentTimeMillis() + stationaryMouseRequirement;
            sync.notify();
        }
    }

    public long getConsideredStableAt() {
        synchronized (sync) {
            return consideredStableAt;
        }
    }
}
