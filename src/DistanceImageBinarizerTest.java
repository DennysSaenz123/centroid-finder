import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

public class DistanceImageBinarizerTest {

    // Helper: makes an image filled with one color
    private BufferedImage makeImage(int width, int height, int color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, color);
            }
        }
        return image;
    }

    @Test
    public void testAllPixelsCloseBecomeWhite() {
        // Fake: every pixel is distance 0 from the target
        ColorDistanceFinder fake = (a, b) -> 0;
        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(fake, 0x000000, 10);
        BufferedImage image = makeImage(3, 2, 0x123456);

        int[][] expected = {
            {1, 1, 1},
            {1, 1, 1}
        };
        int[][] actual = binarizer.toBinaryArray(image);

        assertArrayEquals(expected, actual);
    }
    @Test
    public void testAllPixelsFarBecomeBlack() {
        // Fake: every pixel is distance 100 from the target
        ColorDistanceFinder fake = (a, b) -> 100;
        DistanceImageBinarizer binarizer = new DistanceImageBinarizer(fake, 0x000000, 50);
        BufferedImage image = makeImage(3, 2, 0x123456);

        int[][] expected = {
            {0, 0, 0},
            {0, 0, 0}
        };
        int[][] actual = binarizer.toBinaryArray(image);

        assertArrayEquals(expected, actual);
    }
}
