import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class DfsBinaryGroupFinder implements BinaryGroupFinder {
   /**
    * Finds connected pixel groups of 1s in an integer array representing a binary image.
    * 
    * The input is a non-empty rectangular 2D array containing only 1s and 0s.
    * If the array or any of its subarrays are null, a NullPointerException
    * is thrown. If the array is otherwise invalid, an IllegalArgumentException
    * is thrown.
    *
    * Pixels are considered connected vertically and horizontally, NOT diagonally.
    * The top-left cell of the array (row:0, column:0) is considered to be coordinate
    * (x:0, y:0). Y increases downward and X increases to the right. For example,
    * (row:4, column:7) corresponds to (x:7, y:4).
    *
    * The method returns a list of sorted groups. The group's size is the number 
    * of pixels in the group. The centroid of the group
    * is computed as the average of each of the pixel locations across each dimension.
    * For example, the x coordinate of the centroid is the sum of all the x
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * Similarly, the y coordinate of the centroid is the sum of all the y
    * coordinates of the pixels in the group divided by the number of pixels in that group.
    * The division should be done as INTEGER DIVISION.
    *
    * The groups are sorted in DESCENDING order according to Group's compareTo method.
    * 
    * @param image a rectangular 2D array containing only 1s and 0s
    * @return the found groups of connected pixels in descending order
    */
    @Override
    public List<Group> findConnectedGroups(int[][] image) throws NullPointerException, IllegalArgumentException {
        if(image == null) throw new NullPointerException("Input image cannot be null.");

        for(int[] row : image) {
            if(row == null) throw new NullPointerException("Input image cannot contain null rows.");
        }

        if(image.length == 0 || image[0].length == 0) {
            throw new IllegalArgumentException("Input image cannot be empty.");
        }

        List<Group> groups = new ArrayList<>();
        Set<Coordinate> visited = new HashSet<>();

        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (image[i][j] == 1 && !visited.contains(new Coordinate(i, j))) {
                    groups.add(findConnectedGroup(image, new Coordinate(i, j), visited));
                }
            }
        }

        return groups;
    }


    public Group findConnectedGroup(int[][] image, Coordinate start, Set<Coordinate> visited) {
        visited.add(start);
        List<Coordinate> coordinates = explore(image, start, visited);

        // calculate centroid
        int x = 0, y = 0;
        int pixels = coordinates.size();
        for (Coordinate coordinate : coordinates) {
            x += coordinate.x();
            y += coordinate.y();
        }
        x = x / pixels;
        y = y / pixels;
        return new Group(pixels, new Coordinate(x, y));
    }

    public List<Coordinate> explore(int[][] image, Coordinate current, Set<Coordinate> visited) {
        List<Coordinate> groupCords = new ArrayList<>();
        if (visited.contains(current)) return groupCords;
        visited.add(current);

        List<Coordinate> neighbors = possiblePixels(image, current);

        for(Coordinate neighbor : neighbors) {
            groupCords.addAll(explore(image, neighbor, visited));
        }

        return groupCords;
    }

    
    public List<Coordinate> possiblePixels(int[][] image, Coordinate current) {
        List<Coordinate> possible = new ArrayList<>();
        int row = current.y(), col = current.x();
        int nRow, nCol;

        // UP
        nRow = row - 1;
        nCol = col; 
        if (nRow >= 0 && image[nRow][nCol] == 1) {
            possible.add(new Coordinate(nRow, nCol));
        }

        // DOWN
        nRow = row + 1;
        nCol = col; 
        if (nRow < image.length && image[nRow][nCol] == 1) {
            possible.add(new Coordinate(nRow, nCol));
        }

        // LEFT
        nRow = row;
        nCol = col - 1; 
        if (nCol >= 0 && image[nRow][nCol] == 1) {
            possible.add(new Coordinate(nRow, nCol));
        }

        // RIGHT
        nRow = row;
        nCol = col + 1;
        if (nCol < image[row].length && image[nRow][nCol] == 1) {
            possible.add(new Coordinate(nRow, nCol));
        }

        return possible;
    }
    
}
