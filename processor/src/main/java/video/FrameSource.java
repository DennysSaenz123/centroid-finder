package video;

/**
 * An interface used for iterating through a video, frame by frame.
 * Use an FFmpegFrameGrabber object when implementing this interface.
 */
public interface FrameSource extends AutoCloseable {
    TimedFrame nextFrame() throws Exception; // null when the video ends
}
