package video;

import image.Group;

/**
 * Represents the Group of a frame and its timestamp.
 * 
 * Comparisons between TimedGroups will be made using its "time" value.
 * 
 * @param time Timestamp of the frame.
 * @param group Group of the frame, containing it's size and centroid.
 */
public record TimedGroup(double time, Group group) {
    /**
     * Returns a String representing a TimedGroup instance as a CSV row. (.csv)
     * Outputs a String of the timestamp, x-coordinate, and y-coordinate of the largest group of the frame.
     * 
     * @return a CSV row string representing the timestamp and coordinates of the TimedGroup.
     */
    public String toCsvRow() {
        return String.format("%.3f,%d,%d", this.time(), this.group.centroid().x(), this.group.centroid().y());
    }
}
