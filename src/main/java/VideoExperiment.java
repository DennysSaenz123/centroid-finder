package main.java;

import java.awt.image.BufferedImage;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

public class VideoExperiment {
    public static void main(String[] args) {
        
        String sampleVideoDir = "sampleInput/kitten.mp4";
        FFmpegFrameGrabber grabber = null;

        // catch load failure for video
        try {
            grabber = new FFmpegFrameGrabber(sampleVideoDir);
            grabber.start();
        } catch (Exception e) {
            System.err.println("FFmpegFrameGrabber failed to start. File path: " + sampleVideoDir);
            e.printStackTrace();
            return;
        }

        // observe as Herbert becomes green!!!!!

        Frame frame = null;
        try {
            frame = grabber.grabImage();
            if (frame == null) throw new Exception("Frame is null.");
        } catch (Exception e) {
            System.out.println("Failed to grab frame. Aborting...");
            e.printStackTrace();
            return;
        }

        Java2DFrameConverter frameConverter = new Java2DFrameConverter();
        BufferedImage image = frameConverter.convert(frame);

        // now for the greenification
    }
}
