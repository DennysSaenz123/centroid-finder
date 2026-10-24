import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class EuclideanColorDistanceTest {
    EuclideanColorDistance ecd = new EuclideanColorDistance();
    @Test 
    public void testConvertHexToR() {
        int color = 0xc84912;
        
        int actual = ecd.hexToR(color);
        int expected = 200;
        
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertHexToG() {
        int color = 0xc84912;
        
        int actual = ecd.hexToG(color);
        int expected = 73;
        
        assertEquals(expected, actual);
    }

    @Test 
    public void testConvertHexToB() {
        int color = 0xc84912;
        
        int actual = ecd.hexToB(color);
        int expected = 18;
        
        assertEquals(expected, actual);
    }

    @Test
    public void testAllWhite() {
        int color = 0xffffff;

        int[] expected = new int[]{255, 255, 255};
        int[] actual = new int[]{ecd.hexToR(color), ecd.hexToG(color), ecd.hexToB(color)};

        assertArrayEquals(expected, actual);
    }

    @Test
    public void testAllBlack() {
        int color = 0x000000;

        int[] expected = new int[]{0, 0, 0};
        int[] actual = new int[]{ecd.hexToR(color), ecd.hexToG(color), ecd.hexToB(color)};

        assertArrayEquals(expected, actual);
    }
}
