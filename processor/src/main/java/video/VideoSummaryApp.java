package video;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import image.EuclideanColorDistance;
import image.DistanceImageBinarizer;
import image.ImageBinarizer;
import image.ColorDistanceFinder;
import image.DfsBinaryGroupFinder;
import image.BinarizingImageGroupFinder;
import image.ImageGroupFinder;

/**
 * The Video Summary Application
 * 
 * A re-implementation of the Image Summary Application, designed specifically for videos.
 * 
 * Takes in four command-line arguments:
 * 1. The path to the video file (example: "directory/folder/video.mp4")
 * 2. An output path for a .csv file containing your video's data. (example: "directory/folder/output.csv")
 * 3. A target hex color in RRGGBB format. (example: FFA500 for orange)
 * 4. A threshold in integer format for binarization. (maximum is 441)
 * 
 * The application performs as follows:
 * 1. Parses your input color into a 24-bit integer.
 * 2. Initializes the threshold for binarization by calculating the Euclidean color distance between your
 *    desired color and any colors within the threshold.
 * 3. Begins slicing through your input video, binarizing each frame's pixel. A pixel is marked white (1) if it is
 *    within Euclidean color distance of your desired color, determined by your threshold. Otherwise, it is marked
 *    black (0).
 * 4. Combs through your binarized frames, grabbing the largest group of color and its centroid. Groups are connected
 *    horizontally and vertical, and can be used to represent an object that is the same color across it's entire body.
 * 5. Writes out a .csv file containing the timestamp of the frame, and the x and y coordinates of the largest group per
 *    frame. x grows from top to bottom, and y grows from left to right.
 */
public class VideoSummaryApp {
    public static void main(String[] args) {

        // check for valid args
        if (args.length != 4) {
            System.out.println("Improper usage!\nCorrect usage: java VideoSummaryApp <input_video_path> <output_csv> <target_color_hexcode> <threshold>");
            return;
        }

        String inputPath = args[0];
        String outputPath = args[1];
        String targetColorHex = args[2];

        // check if the input path actually exists
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.err.println("Input file at " + inputFile.getAbsolutePath() + " does not exist.");
            return;
        }
        if (!inputFile.isFile()) {
            System.err.println("Input path at " + inputFile.getAbsolutePath() + " is not a file. (Is it a directory?)");
            return;
        }
        if (!inputFile.canRead()) {
            System.err.println("Input file at " + inputFile.getAbsolutePath() + " could not be read.");
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

        // check if threshold is an integer
        int threshold = 0;
        try {
            threshold = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("Threshold must be an integer!");
            return;
        }

        // args are valid! move onto calling proper classes and functions.

        // creating our pipelines for frame processing...
        ColorDistanceFinder distFinder = new EuclideanColorDistance();
        
        // turns images into 2D binary arrays
        ImageBinarizer binarizer = new DistanceImageBinarizer(distFinder, targetColor, threshold);

        // finds groups of identified pixels
        ImageGroupFinder groupFinder = new BinarizingImageGroupFinder(binarizer, new DfsBinaryGroupFinder());

        // finds the largest Group
        LargestCentroidLocator locator = new LargestCentroidLocator(groupFinder);

        // loops through every frame in the video and returns its largest Group per frame
        VideoSummarizer summarizer = new VideoSummarizer(locator);

        // output list which will have its values put into the .csv file.
        List<TimedGroup> groups = null;
        
        System.out.println("\n\nStarting video processing...\n\n");

        // process the video!
        // kills if any exceptions are thrown (likely from the frame grabber breaking)
        try (FrameSource source = new JavaCvFrameSource(inputPath)) {
            groups = summarizer.process(source);
        } catch (Exception e) {
            System.out.println("Failed to process video at " + inputPath);
            e.printStackTrace();
            return;
        }

        System.out.println("\n\nVideo processed! Outputting .csv file...\n\n");

        // output as a .csv
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
