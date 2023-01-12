package git.doomshade.aoc._81;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <h2>--- Day 8: Treetop Tree House ---</h2><p>The expedition comes across a peculiar patch of tall trees all planted carefully in a grid. The Elves explain that a
 * previous expedition planted these trees as a reforestation effort. Now, they're curious if this would be a good location for a
 * <a href="https://en.wikipedia.org/wiki/Tree_house" target="_blank">tree house</a>.</p>
 * <p>First, determine whether there is enough tree cover here to keep a tree house <em>hidden</em>. To do this, you need to count the number of trees that are <em>visible
 * from outside the grid</em> when looking directly along a row or column.</p>
 * <p>The Elves have already launched a <a href="https://en.wikipedia.org/wiki/Quadcopter" target="_blank">quadcopter</a>
 * to generate a map with the height of each tree (<span title="The Elves have already launched a quadcopter (your puzzle input).">your puzzle input</span>). For example:</p>
 * <pre><code>
 * 30373
 * 25512
 * 65332
 * 33549
 * 35390
 * </code></pre>
 * <p>Each tree is represented as a single digit whose value is its height, where <code>0</code> is the shortest and <code>9</code> is the tallest.</p>
 * <p>A tree is <em>visible</em> if all of the other trees between it and an edge of the grid are <em>shorter</em> than it. Only consider trees in the same row or column;
 * that is, only look up, down, left, or right from any given tree.</p>
 * <p>All of the trees around the edge of the grid are <em>visible</em> - since they are already on the edge, there are no trees to block the view. In this example, that
 * only leaves the <em>interior nine trees</em> to consider:</p>
 * <ul>
 * <li>The top-left <code>5</code> is <em>visible</em> from the left and top. (It isn't visible from the right or bottom since other trees of height <code>5</code> are in
 * the way.)</li>
 * <li>The top-middle <code>5</code> is <em>visible</em> from the top and right.</li>
 * <li>The top-right <code>1</code> is not visible from any direction; for it to be visible, there would need to only be trees of height <em>0</em> between it and an edge
 * .</li>
 * <li>The left-middle <code>5</code> is <em>visible</em>, but only from the right.</li>
 * <li>The center <code>3</code> is not visible from any direction; for it to be visible, there would need to be only trees of at most height <code>2</code> between it and
 * an edge.</li>
 * <li>The right-middle <code>3</code> is <em>visible</em> from the right.</li>
 * <li>In the bottom row, the middle <code>5</code> is <em>visible</em>, but the <code>3</code> and <code>4</code> are not.</li>
 * </ul>
 * <p>With 16 trees visible on the edge and another 5 visible in the interior, a total of <code><em>21</em></code> trees are visible in this arrangement.</p>
 * <p>Consider your map; <em>how many trees are visible from outside the grid?</em></p>
 */
public class Main implements Runnable {
    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            int origRows = input.size();
            int origCols = input.get(0)
                                .length();
            // [y][x]
            final int[][] grid = new int[origRows + 2][origCols + 2];
            for (int row = 1; row < origRows + 1; row++) {
                final String strRow = input.get(row - 1);
                for (int col = 1; col < origCols + 1; col++) {
                    grid[row][col] = strRow.charAt(col - 1) - '0';
                }
            }
            for (int row = 0; row < grid.length; row++) {
                grid[row][0] = -1;
                grid[row][grid[0].length - 1] = -1;
            }
            for (int col = 0; col < grid[0].length; col++) {
                grid[0][col] = -1;
                grid[grid.length - 1][col] = -1;
            }

            for (int[] row : grid) {
                System.out.println(Arrays.toString(row));
            }
            int result = 0;

            for (int row = 1; row < grid.length - 1; row++) {
                for (int col = 1; col < grid[0].length - 1; col++) {
//                    System.out.printf("[%d][%d]%n", row, col);
                    int minSize = grid[row][col];
                    // left right top bottom
                    byte visibility = 0b1111;

                    // go left
                    for (int left = col - 1; left >= 0; left--) {
                        final int lCell = grid[row][left];
//                        System.out.printf("Comparing %d >= %d%n", lCell, minSize);
                        if (lCell >= minSize) {
//                            System.out.println("Left not visible");
                            visibility ^= 0b1000;
                            break;
                        }
                    }
//                    System.out.println("Left done\n");

                    for (int right = col + 1; right < grid.length; right++) {
                        final int rCell = grid[row][right];
//                        System.out.printf("Comparing %d >= %d%n", rCell, minSize);
                        if (rCell >= minSize) {
//                            System.out.println("Right not visible");
                            visibility ^= 0b0100;
                            break;
                        }
                    }
//                    System.out.println("Right done\n");

                    for (int top = row - 1; top >= 0; top--) {
                        final int tCell = grid[top][col];
//                        System.out.printf("Comparing %d >= %d%n", tCell, minSize);
                        if (tCell >= minSize) {
//                            System.out.println("Top not visible");
                            visibility ^= 0b0010;
                            break;
                        }
                    }
//                    System.out.println("Top done\n");

                    for (int bottom = row + 1; bottom < grid.length; bottom++) {
                        final int bCell = grid[bottom][col];
//                        System.out.printf("Comparing %d >= %d%n", bCell, minSize);
                        if (bCell >= minSize) {
//                            System.out.println("Bottom not visible");
                            visibility ^= 0b0001;
                            break;
                        }
                    }
//                    System.out.println("Bottom done\n");
//                    System.out.println(Integer.toBinaryString(visibility));
//                    System.out.println("\n");
                    if (visibility != 0) {
//                        System.out.printf("[%d][%d] is visible%n", row, col);
                        result++;
                    }
                }
            }
            System.out.println(result);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
