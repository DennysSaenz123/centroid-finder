import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class DfsBinaryGroupFinderTest {

    private final DfsBinaryGroupFinder finder = new DfsBinaryGroupFinder();

    // ---------- Basic cases ----------

    @Test
    public void testSinglePixel() {
        int[][] image = {
            {1}
        };

        List<Group> expected = List.of(new Group(1, new Coordinate(0, 0)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    @Test
    public void testAllZeros() {
        int[][] image = {
            {0, 0},
            {0, 0}
        };

        List<Group> actual = finder.findConnectedGroups(image);

        assertTrue(actual.isEmpty());
    }

    @Test
    public void testAllOnes() {
        int[][] image = {
            {1, 1, 1},
            {1, 1, 1}
        };

        // x sum = 6, y sum = 3, size 6 -> (1, 0)
        List<Group> expected = List.of(new Group(6, new Coordinate(1, 0)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }
}   