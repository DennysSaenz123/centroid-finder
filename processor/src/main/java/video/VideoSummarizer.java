package video;

import java.util.List;
import java.util.ArrayList;

public class VideoSummarizer {
    private final LargestCentroidLocator locator;

    public VideoSummarizer(LargestCentroidLocator locator) {
        this.locator = locator;
    }

    /**
     * Calls centroid location methods frame by frame to create a data representation of a full video.
     * 
     * @param source FrameSource object containing a path to the video and a nextFrame() method.
     * @return An ArrayList of TimedGroup objects, representing the full video.
     * @throws Exception Thrown when FrameSource fails, usually because of underlying issues with FFmpegFrameGrabber.
     */
    public List<TimedGroup> process(FrameSource source) throws Exception {
        List<TimedGroup> results = new ArrayList<>();
        TimedFrame frame;
        while ((frame = source.nextFrame()) != null) {
            results.add(locator.locate(frame));
            System.out.println("Processed frame at " + frame.seconds() + " seconds.");
        }
        return results;
    }
}
