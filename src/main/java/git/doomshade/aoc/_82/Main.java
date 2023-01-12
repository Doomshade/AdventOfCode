package git.doomshade.aoc._82;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>Content with the amount of tree cover available, the Elves just need to know the best spot to build their tree house: they would
 * like to be able to see a lot of <em>trees</em>.</p>
 * <p>To measure the viewing distance from a given tree, look up, down, left, and right from that tree; stop if you reach an edge or at the first tree that is the same
 * height or taller than the tree under consideration. (If a tree is right on the edge, at least one of its viewing distances will be zero.)</p>
 * <p>The Elves don't care about distant trees taller than those found by the rules above; the proposed tree house has large
 * <a href="https://en.wikipedia.org/wiki/Eaves" target="_blank">eaves</a> to keep it dry, so they wouldn't be able to see higher than the tree house anyway.</p>
 * <p>In the example above, consider the middle <code>5</code> in the second row:</p>
 * <pre><code>
 * 30373
 * 25<em>5</em>12
 * 65332
 * 33549
 * 35390
 * </code></pre>
 * <ul>
 * <li>Looking up, its view is not blocked; it can see <code><em>1</em></code> tree (of height <code>3</code>).</li>
 * <li>Looking left, its view is blocked immediately; it can see only <code><em>1</em></code> tree (of height <code>5</code>, right next to it).</li>
 * <li>Looking right, its view is not blocked; it can see <code><em>2</em></code> trees.</li>
 * <li>Looking down, its view is blocked eventually; it can see <code><em>2</em></code> trees (one of height <code>3</code>, then the tree of height <code>5</code> that
 * blocks its view).</li>
 * </ul>
 * <p>A tree's <em>scenic score</em> is found by <em>multiplying together</em> its viewing distance in each of the four directions. For this tree, this is
 * <code><em>4</em></code> (found by multiplying <code>1 * 1 * 2 * 2</code>).</p>
 * <p>However, you can do even better: consider the tree of height <code>5</code> in the middle of the fourth row:</p>
 * <pre><code>
 * 30373
 * 25512
 * 65332
 * 33<em>5</em>49
 * 35390
 * </code></pre>
 * <ul>
 * <li>Looking up, its view is blocked at <code><em>2</em></code> trees (by another tree with a height of <code>5</code>).</li>
 * <li>Looking left, its view is not blocked; it can see <code><em>2</em></code> trees.</li>
 * <li>Looking down, its view is also not blocked; it can see <code><em>1</em></code> tree.</li>
 * <li>Looking right, its view is blocked at <code><em>2</em></code> trees (by a massive tree of height <code>9</code>).</li>
 * </ul>
 * <p>This tree's scenic score is <code><em>8</em></code> (<code>2 * 2 * 1 * 2</code>); this is the ideal spot for the tree house.</p>
 * <p>Consider each tree on your map. <em>What is the highest scenic score possible for any tree?</em></p>
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
            final int[][] grid = new int[origRows][origCols];
            for (int row = 0; row < origRows; row++) {
                final String strRow = input.get(row);
                for (int col = 0; col < origCols; col++) {
                    grid[row][col] = strRow.charAt(col) - '0';
                }
            }

            for (int[] row : grid) {
                System.out.println(Arrays.toString(row));
            }
            int result = Integer.MIN_VALUE;

            for (int row = 0; row < grid.length - 1; row++) {
                for (int col = 0; col < grid[0].length - 1; col++) {
                    int minSize = grid[row][col];
                    int[] multipliers = new int[4];

                    int count = 0;
                    for (int left = col - 1; left >= 0; left--) {
                        count++;
                        final int lCell = grid[row][left];
                        if (lCell >= minSize) {
                            break;
                        }
                    }
                    multipliers[0] = count;
                    count = 0;

                    for (int right = col + 1; right < grid.length; right++) {
                        count++;
                        final int rCell = grid[row][right];
                        if (rCell >= minSize) {
                            break;
                        }
                    }
                    multipliers[1] = count;
                    count = 0;

                    for (int top = row - 1; top >= 0; top--) {
                        count++;
                        final int tCell = grid[top][col];
                        if (tCell >= minSize) {
                            break;
                        }
                    }
                    multipliers[2] = count;
                    count = 0;

                    for (int bottom = row + 1; bottom < grid.length; bottom++) {
                        count++;
                        final int bCell = grid[bottom][col];
                        if (bCell >= minSize) {
                            break;
                        }
                    }
                    multipliers[3] = count;
                    final int scenicScore = Arrays.stream(multipliers)
                                                  .reduce(1, (left, right) -> left * right);
                    System.out.printf("Scenic score for [%d, %d]: %d (%s)%n", row, col, scenicScore, Arrays.toString(multipliers));
                    if (result < scenicScore) {
                        result = scenicScore;
                    }
                }
            }
            System.out.println(result);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
