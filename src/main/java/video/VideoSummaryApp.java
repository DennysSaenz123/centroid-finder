package video;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;

import image.BinarizingImageGroupFinder;
import image.ColorDistanceFinder;
import image.DfsBinaryGroupFinder;
import image.DistanceImageBinarizer;
import image.EuclideanColorDistance;
import image.ImageBinarizer;
import image.ImageGroupFinder;

public class VideoSummaryApp {
    public static void main(String[] args) {

        if (args.length != 4 && args.length != 5) {
            System.err.println("Improper usage!\nCorrect usage: java VideoSummaryApp <input_video_path> <output_csv> <target_color_hexcode> <threshold> [output_video_path]");
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];
        String targetColorHex = args[2];
        // optional: where to write the binarized video (null means don't write one)
        String outputVideoPath = args.length == 5 ? args[4] : null;

        // validate the input file before doing any work
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.err.println("Input file does not exist: " + inputFile.getAbsolutePath());
            return;
        }
        if (!inputFile.isFile()) {
            System.err.println("Input path is not a file (is it a directory?): " + inputFile.getAbsolutePath());
            return;
        }
        if (!inputFile.canRead()) {
            System.err.println("Input file is not readable: " + inputFile.getAbsolutePath());
            return;
        }

        // validate the output video's folder up front, not after processing the whole video
        if (outputVideoPath != null) {
            File parent = new File(outputVideoPath).getAbsoluteFile().getParentFile();
            if (parent != null && !parent.isDirectory()) {
                System.err.println("Output video directory does not exist: " + parent.getAbsolutePath());
                return;
            }
        }

        int targetColor;
        try {
            targetColor = Integer.parseInt(targetColorHex, 16);
        } catch (NumberFormatException e) {
            System.err.println("Invalid hex target color. Please provide a color in RRGGBB format.");
            return;
        }

        int threshold;
        try {
            threshold = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Threshold must be an integer!");
            return;
        }

        // build the image pipeline
        ColorDistanceFinder distFinder = new EuclideanColorDistance();
        ImageBinarizer binarizer = new DistanceImageBinarizer(distFinder, targetColor, threshold);
        ImageGroupFinder groupFinder = new BinarizingImageGroupFinder(binarizer, new DfsBinaryGroupFinder());

        LargestCentroidLocator locator = new LargestCentroidLocator(groupFinder);
        VideoSummarizer summarizer = new VideoSummarizer(locator);

        // process the video; try-with-resources closes both resources (writer first, then source).
        // A null resource is simply skipped, so no video is written when outputVideoPath is null.
        ArrayList<TimedGroup> groups;
        try (FrameSource source = new JavaCvFrameSource(inputPath);
             BinarizedVideoWriter writer = outputVideoPath == null ? null
                     : new BinarizedVideoWriter(outputVideoPath, source.getWidth(), source.getHeight(),
                             source.getFrameRate(), binarizer)) {
            groups = summarizer.process(source, writer);
        } catch (Exception e) {
            System.err.println("Failed to process video at: " + inputPath);
            e.printStackTrace();
            return;
        }

        if (outputVideoPath != null) {
            System.out.println("Binarized video saved as " + outputVideoPath + ".");
        }

        // write the CSV
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
