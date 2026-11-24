package video;

import java.util.List;

import image.Coordinate;
import image.Group;
import image.ImageGroupFinder;

/**
 * An Object used to locate the largest Group in a frame of a video.
 * 
 * The largest group represents the largest detected object on-screen within the color threshold.
 * This can be used to obtain the centroid of the object. 
 */
public class LargestCentroidLocator {
    private final ImageGroupFinder groupFinder;

    public LargestCentroidLocator(ImageGroupFinder groupFinder) {
        this.groupFinder = groupFinder;
    }

    /**
     * Locates the largest centroid in a TimedFrame object.
     * 
     * @param frame The TimedFrame containing the time of the frame and the frame itself as a BufferedImage.
     * @return A TimedGroup, containing the time of the Group's frame and the Group object itself.
     */
    public TimedGroup locate(TimedFrame frame) {
        List<Group> groups = groupFinder.findConnectedGroups(frame.image());
        if (groups == null || groups.isEmpty()) {
            // no centroid found
            return new TimedGroup(frame.seconds(), new Group(0, new Coordinate(-1, -1)));
        }
        // groups are sorted largest-first
        return new TimedGroup(frame.seconds(), groups.get(0));
    }
}
