package video;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.bytedeco.javacv.FFmpegFrameGrabber;

import image.EuclideanColorDistance;
import image.DistanceImageBinarizer;
import image.ImageBinarizer;
import image.ColorDistanceFinder;
import image.Group;

public class VideoSummaryApp {
    public static void main(String[] args) {

        // check for valid args
        if (args.length != 4) {
            System.out.println("Improper usage!\nCorrect usage: java VideoSummaryApp <input_video_path> <output_csv> <target_color_hexcode> <threshold>");
        }

        String inputPath = args[0];
        String outputPath = args[1];
        String targetColorHex = args[2];

        // check if threshold is an integer
        int threshold = 0;
        try {
            threshold = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("Threshold must be an integer!");
            return;
        }

        // check if the input path is valid.
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = new FFmpegFrameGrabber(inputPath);
        } catch (Exception e) {
            System.err.println("Failed to load mp4 file at: " + inputPath);
            e.printStackTrace();
            return;
        }

        // check if targetColorHex is actually hexidecimal in the proper format.
        int targetColor = 0;
        try {
            targetColor = Integer.parseInt(targetColorHex, 16);
        } catch (NumberFormatException e) {
            System.err.println("Invalid hex target color. Please provide a color in RRGGBB format.");
            return;
        }

        // args are valid! move onto calling proper classes and functions.

        ColorDistanceFinder distFinder = new EuclideanColorDistance();
        ImageBinarizer binarizer = new DistanceImageBinarizer(distFinder, targetColor, threshold);

        JavaCvFrameSource frameProcessor = new JavaCvFrameSource(grabber, binarizer);

        List<TimedGroup> groups = null;
        
        try {
            frameProcessor.process();
        } catch (Exception e) {
            System.out.println("Failed to process image.");
            e.printStackTrace();
            return;
        }
        
        try (PrintWriter writer = new PrintWriter(outputPath)) {
            for (TimedGroup group : groups) {
                writer.println(group.toCsvRow());
            }
            System.out.println("Groups summary saved as " + outputPath + ".");
        } catch (Exception e) {
            System.err.println("Error writing " + outputPath + ".");
            e.printStackTrace();
        }
    }   
}
