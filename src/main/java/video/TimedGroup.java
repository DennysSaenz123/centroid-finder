package video;

import image.Group;

public record TimedGroup(double time, Group group) {
    public String toCsvRow() {
        return String.format("%d%d%d", this.time(), this.group.centroid().x(), this.group.centroid().y());
    }
}
