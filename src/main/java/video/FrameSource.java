package video;

public interface FrameSource extends AutoCloseable {
    TimedFrame nextFrame() throws Exception; // null when the video ends

    double getFrameRate(); // frames per second of the source video
    int getWidth();        // frame width in pixels
    int getHeight();       // frame height in pixels
}
