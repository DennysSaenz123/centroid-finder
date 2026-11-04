package video;

import java.util.List;
import java.util.ArrayList;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import image.Group;
import image.Coordinate;
import image.ImageBinarizer;
import image.ImageGroupFinder;
import image.BinarizingImageGroupFinder;
import image.DfsBinaryGroupFinder;

public class JavaCvFrameSource implements FrameSource {

    // Reads frames from the video file; final because it's set once in the constructor
    private final FFmpegFrameGrabber grabber;

    // Turns each Frame into a BufferedImage that the image/centroid code understands
    private final Java2DFrameConverter converter = new Java2DFrameConverter();

    // Image binarizer to process incoming frames and to output Lists of Groups.
    private final ImageBinarizer binarizer;

    // Finds Groups within a binarized image. Instantiated on construction.
    private final ImageGroupFinder groupFinder;

    public JavaCvFrameSource(FFmpegFrameGrabber grabber, ImageBinarizer binarizer) {
        this.grabber = grabber;
        this.binarizer = binarizer;
        groupFinder = new BinarizingImageGroupFinder(binarizer, new DfsBinaryGroupFinder());
    }
    
    @Override
    public TimedFrame nextFrame() throws Exception {
        // Grab the next video frame only (grab() would also return audio frames)
        Frame frame = grabber.grabImage();

        // No more frames means the video is finished
        if (frame == null) return null;

        // getTimestamp() is in microseconds, so divide by 1,000,000 to get seconds
        double seconds = grabber.getTimestamp() / 1_000_000.0;

        // Convert the frame to a BufferedImage and pair it with its time
        return new TimedFrame(seconds, converter.convert(frame));
    }
    
    // Required by AutoCloseable: runs automatically at the end of a try-with-resources block
    @Override
    public void close() throws Exception {
        // Stop decoding the video
        grabber.stop();
        // Free the native FFmpeg memory and the file handle
        grabber.release();
        // Free the converter's internal buffers
        converter.close();
    }

    // 
    public List<TimedGroup> process() throws Exception {
        TimedFrame currentFrame = nextFrame();
        List<TimedGroup> orderedGroups = new ArrayList<>();

        // keep looping while there's still frames left;
        while (currentFrame != null) {
            List<Group> currentGroup = groupFinder.findConnectedGroups(currentFrame.image());

            // if no centroid, create a nullified value
            if (currentGroup.size() == 0) {
                orderedGroups.add(
                    new TimedGroup(currentFrame.seconds(), new Group(
                            0, new Coordinate(-1, -1)
                        )
                    )
                );
                continue;
            }

            // add largest (first) Group to output list
            // groups are automatically sorted in findConnectedGroups
            orderedGroups.add(new TimedGroup(currentFrame.seconds(), currentGroup.getFirst()));
            // current = current.next;
            currentFrame = nextFrame();
        }

        this.close();
        return orderedGroups;
    }
}