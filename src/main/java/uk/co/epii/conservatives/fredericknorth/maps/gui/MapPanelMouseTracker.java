package uk.co.epii.conservatives.fredericknorth.maps.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: James Robinson
 * Date: 10/07/2013
 * Time: 17:26
 */
public class MapPanelMouseTracker {

    private static final Logger LOG_SYNC =
            LoggerFactory.getLogger(MapPanelMouseTracker.class.getName().concat("_sync"));

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
                    LOG_SYNC.debug("Awaiting sync");
                    try {
                        synchronized (sync) {
                            LOG_SYNC.debug("Received sync");
                            sleepFor = consideredStableAt - System.currentTimeMillis();
                            stablePoint = lastSeenAt;
                        }
                    }
                    finally {
                        LOG_SYNC.debug("Released sync");
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
                        LOG_SYNC.debug("Awaiting sync");
                        try {
                            synchronized (sync) {
                                LOG_SYNC.debug("Received sync");
                                try {
                                    sync.wait();
                                }
                                catch (InterruptedException ie) {
                                    break;
                                }
                            }
                        }
                        finally {
                            LOG_SYNC.debug("Released sync");
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
        LOG_SYNC.debug("Awaiting listeners");
        try {
            synchronized (listeners) {
                LOG_SYNC.debug("Received listeners");
                for (MouseStableListener listener : listeners) {
                    listener.mouseStable(e);
                }
            }
        }
        finally {
            LOG_SYNC.debug("Released listeners");
        }
    }

    public void addMouseStableListener(MouseStableListener l) {
        LOG_SYNC.debug("Awaiting listeners");
        try {
            synchronized (listeners) {
                LOG_SYNC.debug("Received listeners");
                listeners.add(l);
            }
        }
        finally {
            LOG_SYNC.debug("Released listeners");
        }
    }

    public void removeMouseStableListener(MouseStableListener l) {
        LOG_SYNC.debug("Awaiting listeners");
        try {
            synchronized (listeners) {
                LOG_SYNC.debug("Received listeners");
                listeners.remove(l);
            }
        }
        finally {
            LOG_SYNC.debug("Released listeners");
        }
    }

    public void setMouseLocation(Point point) {
        LOG_SYNC.debug("Awaiting sync");
        try {
            synchronized (sync) {
                LOG_SYNC.debug("Received sync");
                if (lastSeenAt != null && lastSeenAt.equals(point)) {
                    return;
                }
                lastSeenAt = point;
                consideredStableAt = System.currentTimeMillis() + stationaryMouseRequirement;
                sync.notify();
            }
        }
        finally {
            LOG_SYNC.debug("Released sync");
        }
    }

    public long getConsideredStableAt() {
        LOG_SYNC.debug("Awaiting sync");
        try {
            synchronized (sync) {
                LOG_SYNC.debug("Received sync");
                return consideredStableAt;
            }
        }
        finally {
            LOG_SYNC.debug("Released sync");
        }
    }
}
