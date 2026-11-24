package video;

import java.awt.image.BufferedImage;

/**
 * Represents a frame in a video, storing a value representing it's time, and the representation of the image
 * in the format of a BufferedImage.
 * 
 * Data structures that store this value compare if it is greater or lesser by the "seconds" variable.
 * 
 * @param seconds The timestamp of the frame.
 * @param image Frame of the video at the timestamp in the form of a BufferedImage.
 */
public record TimedFrame(double seconds, BufferedImage image) {}
