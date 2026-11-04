package video;

import java.util.ArrayList;

public class VideoSummarizer {
    private final LargestCentroidLocator locator;

    public VideoSummarizer(LargestCentroidLocator locator) {
        this.locator = locator;
    }

    public ArrayList<TimedGroup> process(FrameSource source) throws Exception {
        ArrayList<TimedGroup> results = new ArrayList<>();
        TimedFrame frame;
        while ((frame = source.nextFrame()) != null) {
            results.add(locator.locate(frame));
        }
        return results;
    }
}
