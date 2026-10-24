import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
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

    @Test
    public void testHorizontalNeighborsConnect() {
        int[][] image = {
            {1, 1}
        };

        List<Group> expected = List.of(new Group(2, new Coordinate(0, 0)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    @Test
    public void testVerticalNeighborsConnect() {
        int[][] image = {
            {1},
            {1}
        };

        List<Group> expected = List.of(new Group(2, new Coordinate(0, 0)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    @Test
    public void testDiagonalDoesNotConnect() {
        int[][] image = {
            {1, 0},
            {0, 1}
        };

        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(2, actual.size());
        assertTrue(actual.contains(new Group(1, new Coordinate(0, 0))));
        assertTrue(actual.contains(new Group(1, new Coordinate(1, 1))));
    }

    @Test
    public void testLShape() {
        int[][] image = {
            {1, 0, 0},
            {1, 0, 0},
            {1, 1, 1}
        };

        // x sum = 3, y sum = 7, size 5 -> (0, 1)
        List<Group> expected = List.of(new Group(5, new Coordinate(0, 1)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    @Test
    public void testUShape() {
        int[][] image = {
            {1, 0, 1},
            {1, 0, 1},
            {1, 1, 1}
        };

        // x sum = 7, y sum = 8, size 7 -> (1, 1)
        List<Group> expected = List.of(new Group(7, new Coordinate(1, 1)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    // ---------- Centroid ----------

    @Test
    public void testCentroidXAndYNotSwapped() {
        int[][] image = {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 1, 1, 1}
        };

        // Pixels at x = 2,3,4 and y = 2 -> (3, 2)
        List<Group> expected = List.of(new Group(3, new Coordinate(3, 2)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    @Test
    public void testCentroidUsesIntegerDivision() {
        int[][] image = {
            {0, 0, 1, 1}
        };

        // x = (2 + 3) / 2 = 2.5 -> 2 with integer division
        List<Group> expected = List.of(new Group(2, new Coordinate(2, 0)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    // ---------- Sorting ----------

    @Test
    public void testGroupsSortedBySizeDescending() {
        int[][] image = {
            {1, 0, 1, 1},
            {0, 0, 0, 0},
            {1, 1, 1, 0}
        };

        List<Group> expected = List.of(
            new Group(3, new Coordinate(1, 2)),
            new Group(2, new Coordinate(2, 0)),
            new Group(1, new Coordinate(0, 0))
        );
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    @Test
    public void testSameSizeGroupsFollowCompareTo() {
        int[][] image = {
            {1, 0, 1}
        };

        List<Group> actual = finder.findConnectedGroups(image);

        // Whatever the tie-break is, the result should match a descending sort
        List<Group> sorted = new ArrayList<>(actual);
        sorted.sort(Collections.reverseOrder());

        assertEquals(2, actual.size());
        assertEquals(sorted, actual);
    }

    // ---------- Shapes ----------

    @Test
    public void testWideGrid() {
        int[][] image = {
            {1, 1, 1, 1, 1}
        };

        // x sum = 10, size 5 -> (2, 0)
        List<Group> expected = List.of(new Group(5, new Coordinate(2, 0)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    @Test
    public void testTallGrid() {
        int[][] image = {
            {1},
            {1},
            {1},
            {1},
            {1}
        };

        // y sum = 10, size 5 -> (0, 2)
        List<Group> expected = List.of(new Group(5, new Coordinate(0, 2)));
        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(expected, actual);
    }

    @Test
    public void testGroupsInAllCorners() {
        int[][] image = {
            {1, 0, 1},
            {0, 0, 0},
            {1, 0, 1}
        };

        List<Group> actual = finder.findConnectedGroups(image);

        assertEquals(4, actual.size());
        assertTrue(actual.contains(new Group(1, new Coordinate(0, 0))));
        assertTrue(actual.contains(new Group(1, new Coordinate(2, 0))));
        assertTrue(actual.contains(new Group(1, new Coordinate(0, 2))));
        assertTrue(actual.contains(new Group(1, new Coordinate(2, 2))));
    }

    // ---------- Errors ----------

    @Test
    public void testNullArrayThrows() {
        assertThrows(NullPointerException.class, () -> finder.findConnectedGroups(null));
    }

    @Test
    public void testNullRowThrows() {
        int[][] image = {
            {1, 0},
            null
        };

        assertThrows(NullPointerException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testEmptyArrayThrows() {
        int[][] image = new int[0][0];

        assertThrows(IllegalArgumentException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testJaggedArrayThrows() {
        int[][] image = {
            {1, 0, 1},
            {1, 0}
        };

        assertThrows(IllegalArgumentException.class, () -> finder.findConnectedGroups(image));
    }

    @Test
    public void testInvalidValueThrows() {
        int[][] image = {
            {1, 2},
            {0, 1}
        };

        assertThrows(IllegalArgumentException.class, () -> finder.findConnectedGroups(image));
    }
}
