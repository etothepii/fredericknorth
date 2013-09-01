package uk.co.epii.conservatives.fredericknorth.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.utilities.ProgressTracker;

import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;

/**
 * User: James Robinson
 * Date: 31/08/2013
 * Time: 20:31
 */
public class LoadSingleImageProgressTracker implements IIOReadProgressListener {

    private static final Logger LOG = LoggerFactory.getLogger(LoadSingleImageProgressTracker.class);

    private final int increments;
    private int remaining;
    private float complete;
    private ProgressTracker progressTracker;

    public LoadSingleImageProgressTracker(ProgressTracker progressTracker, int increments) {
        this.increments = increments;
        this.progressTracker = progressTracker;
        remaining = increments;
        complete = 0f;
    }

    @Override
    public void sequenceStarted(ImageReader source, int minIndex) {}

    @Override
    public void sequenceComplete(ImageReader source) {}

    @Override
    public void imageStarted(ImageReader source, int imageIndex) {}

    @Override
    public void imageProgress(ImageReader source, float percentageDone) {
        LOG.debug("Image progress: {}", percentageDone);
        complete = percentageDone;
        int targetRemaining = (int)((1 - complete) * increments);
        int delta = remaining - targetRemaining;
        if (delta > 0) {
            progressTracker.increment(delta);
            remaining -= delta;
        }
    }

    @Override
    public void imageComplete(ImageReader source) {
        progressTracker.increment(remaining);
        remaining = 0;
    }

    @Override
    public void thumbnailStarted(ImageReader source, int imageIndex, int thumbnailIndex) {}

    @Override
    public void thumbnailProgress(ImageReader source, float percentageDone) {}

    @Override
    public void thumbnailComplete(ImageReader source) {}

    @Override
    public void readAborted(ImageReader source) {
        progressTracker.increment(remaining);
        remaining = 0;
    }

    public int getRemaining() {
        return remaining;
    }
}
