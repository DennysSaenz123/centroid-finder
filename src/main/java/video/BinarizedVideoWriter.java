package video;

import java.awt.image.BufferedImage;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;

import image.ImageBinarizer;

/**
 * Binarizes each frame it is given and encodes the result into a video file.
 * The container format (mp4, avi, ...) is inferred from the output file's extension.
 */
public class BinarizedVideoWriter implements AutoCloseable {

    // Encodes frames into the output video file
    private final FFmpegFrameRecorder recorder;

    // Turns each binarized BufferedImage back into a javacv Frame for the recorder
    private final Java2DFrameConverter converter = new Java2DFrameConverter();

    // Same binarizer the locator pipeline uses, so the video matches what is being measured
    private final ImageBinarizer binarizer;

    public BinarizedVideoWriter(String path, int width, int height, double frameRate, ImageBinarizer binarizer)
            throws Exception {
        if (frameRate <= 0) {
            throw new IllegalArgumentException("Source frame rate is unknown (" + frameRate + "); cannot write video.");
        }
        this.binarizer = binarizer;

        recorder = new FFmpegFrameRecorder(path, width, height);
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        // yuv420p is the pixel format virtually every player supports
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setFrameRate(frameRate);
        // x264 quality setting (lower = better); keeps the sharp black/white edges clean
        recorder.setVideoOption("crf", "18");
        recorder.start();
    }

    /** Binarizes the frame's image and appends it to the output video. */
    public void write(TimedFrame frame) throws Exception {
        int[][] binary = binarizer.toBinaryArray(frame.image());
        BufferedImage binaryImage = binarizer.toBufferedImage(binary);
        recorder.record(converter.convert(binaryImage));
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
