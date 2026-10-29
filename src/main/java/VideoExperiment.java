package main.java;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class VideoExperiment {
    public static void main(String[] args) {
        
        String sampleImageDir = "sampleInput/squares.jpg";
        String sampleVideoDir = "sampleInput/kitten.mp4";
        BufferedImage inputImage = null;
        BufferedImage inputVideo = null;

        // catch load failure for image
        try {
            inputImage = ImageIO.read(new File(sampleImageDir));
        } catch (Exception e) {
            System.err.println("Error loading image: " + sampleImageDir);
            e.printStackTrace();
            return;
        }
        // catch load failure for video
        try {
            inputVideo = ImageIO.read(new File(sampleVideoDir));
        } catch (Exception e) {
            System.err.println("Error loading video: " + sampleVideoDir);
            e.printStackTrace();
            return;
        }
        
    }
}
