package video;

import java.util.ArrayList;

public class VideoSummarizer {
    private final LargestCentroidLocator locator;

    public VideoSummarizer(LargestCentroidLocator locator) {
        this.locator = locator;
    }

    /** Locates the centroid in every frame; no video is written. */
    public ArrayList<TimedGroup> process(FrameSource source) throws Exception {
        return process(source, null);
    }

    /**
     * Locates the centroid in every frame and, if writer is not null,
     * also appends each binarized frame to the output video.
     */
    public ArrayList<TimedGroup> process(FrameSource source, BinarizedVideoWriter writer) throws Exception {
        ArrayList<TimedGroup> results = new ArrayList<>();
        TimedFrame frame;
        while ((frame = source.nextFrame()) != null) {
            results.add(locator.locate(frame));
            if (writer != null) {
                writer.write(frame);
            }
        }
        return results;
    }
}
