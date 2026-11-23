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

public class VideoSummaryApp {

    private static final int EXIT_CODE_SUCCESS = 0;
    private static final int EXIT_CODE_FAILURE = 1;
    private static final int EXIT_CODE_INVALID_ARGS = 2;
    private static final int EXIT_CODE_FILE_NOT_FOUND = 3;
    private static final int EXIT_CODE_FILE_NOT_READABLE = 4;
    private static final int EXIT_CODE_INVALID_HEX_COLOR = 5;

    public static int main(String[] args) {


        // check for valid args
        if (args.length != 4) {
            System.out.println("Improper usage!\nCorrect usage: java VideoSummaryApp <input_video_path> <output_csv> <target_color_hexcode> <threshold>");
            return EXIT_CODE_INVALID_ARGS;
        }

        String inputPath = args[0];
        String outputPath = args[1];
        String targetColorHex = args[2];

        // check if the input path actually exists
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            System.err.println("Input file at " + inputFile.getAbsolutePath() + " does not exist.");
            return EXIT_CODE_FILE_NOT_FOUND;
        }
        if (!inputFile.isFile()) {
            System.err.println("Input path at " + inputFile.getAbsolutePath() + " is not a file. (Is it a directory?)");
            return EXIT_CODE_FILE_NOT_FOUND;
        }
        if (!inputFile.canRead()) {
            System.err.println("Input file at " + inputFile.getAbsolutePath() + " could not be read.");
            return EXIT_CODE_FILE_NOT_FOUND;
        }

        // check if targetColorHex is actually hexidecimal in the proper format.
        int targetColor = 0;
        try {
            targetColor = Integer.parseInt(targetColorHex, 16);
        } catch (NumberFormatException e) {
            System.err.println("Invalid hex target color. Please provide a color in RRGGBB format.");
            return EXIT_CODE_INVALID_HEX_COLOR;
        }

        // check if threshold is an integer
        int threshold = 0;
        try {
            threshold = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("Threshold must be an integer!");
            return EXIT_CODE_FAILURE;
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
            return EXIT_CODE_FAILURE;
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
        return EXIT_CODE_SUCCESS;
    }   
}
