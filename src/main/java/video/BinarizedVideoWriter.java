package video;
 
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
 
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
 
import image.ImageBinarizer;
 
/**
 * Binarizes each frame it is given, marks the largest group's centroid with a red dot,
 * and encodes the result into a video file.
 * The container format (mp4, avi, ...) is inferred from the output file's extension.
 */
public class BinarizedVideoWriter implements AutoCloseable {
 
    // Encodes frames into the output video file
    private final FFmpegFrameRecorder recorder;
 
    // Turns each binarized BufferedImage back into a javacv Frame for the recorder
    private final Java2DFrameConverter converter = new Java2DFrameConverter();
 
    // Same binarizer the locator pipeline uses, so the video matches what is being measured
    private final ImageBinarizer binarizer;
 
    // Radius of the red marker in pixels, scaled to the video size so it is visible at any resolution
    private final int dotRadius;
 
    public BinarizedVideoWriter(String path, int width, int height, double frameRate, ImageBinarizer binarizer)
            throws Exception {
        if (frameRate <= 0) {
            throw new IllegalArgumentException("Source frame rate is unknown (" + frameRate + "); cannot write video.");
        }
        this.binarizer = binarizer;
        this.dotRadius = Math.max(3, Math.min(width, height) / 60);
 
        recorder = new FFmpegFrameRecorder(path, width, height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        // yuv420p is the pixel format virtually every player supports
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setFrameRate(frameRate);
        // x264 quality setting (lower = better); keeps the sharp black/white edges clean
        recorder.setVideoOption("crf", "18");
        recorder.start();
    }
 
    /**
     * Binarizes the frame's image, draws a red dot at the located centroid,
     * and appends the result to the output video.
     *
     * @param frame   the original frame
     * @param located the largest group found in that same frame
     */
    public void write(TimedFrame frame, TimedGroup located) throws Exception {
        int[][] binary = binarizer.toBinaryArray(frame.image());
        BufferedImage binaryImage = binarizer.toBufferedImage(binary);
        drawDot(binaryImage, located);
        recorder.record(converter.convert(binaryImage));
    }
 
    // Draws a filled red circle centered on the group's centroid
    private void drawDot(BufferedImage image, TimedGroup located) {
        // size 0 means nothing matched (the centroid is the -1,-1 sentinel), so there is nothing to mark
        if (located.group().size() == 0) return;
 
        int cx = located.group().centroid().x();
        int cy = located.group().centroid().y();
 
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(Color.RED);
            // fillOval takes the top-left corner of the bounding box, so shift by the radius
            g.fillOval(cx - dotRadius, cy - dotRadius, dotRadius * 2, dotRadius * 2);
        } finally {
            g.dispose();
        }
    }
 
    // Required by AutoCloseable: finishes the file so it is playable
    @Override
    public void close() throws Exception {
        // Flush any buffered frames and write the file trailer
        recorder.stop();
        // Free the native FFmpeg memory and the file handle
        recorder.release();
        // Free the converter's internal buffers
        converter.close();
    }
}
