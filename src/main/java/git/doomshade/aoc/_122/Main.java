package git.doomshade.aoc._122;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>As you walk up the hill, you suspect that the Elves will want to turn this into a hiking trail. The beginning isn't very scenic,
 * though; perhaps you can find a better starting point.</p>
 * <p>To maximize exercise while hiking, the trail should start as low as possible: elevation <code>a</code>. The goal is still the square marked <code>E</code>. However,
 * the trail should still be direct, taking the fewest steps to reach its goal. So, you'll need to find the shortest path from <em>any square at elevation
 * <code>a</code></em> to the square marked <code>E</code>.</p>
 * <p>Again consider the example from above:</p>
 * <pre><code>
 * <em>S</em>abqponm
 * abcryxxl
 * accsz<em>E</em>xk
 * acctuvwj
 * abdefghi
 * </code></pre>
 * <p>Now, there are six choices for starting position (five marked <code>a</code>, plus the square marked <code>S</code> that counts as being at elevation <code>a</code>).
 * If you start at the bottom-left square, you can reach the goal most quickly:</p>
 * <pre><code>
 * ...v&lt;&lt;&lt;&lt;
 * ...vv&lt;&lt;^
 * ...v&gt;E^^
 * .&gt;v&gt;&gt;&gt;^^
 * &gt;^&gt;&gt;&gt;&gt;&gt;^
 * </code></pre>
 * <p>This path reaches the goal in only <code><em>29</em></code> steps, the fewest possible.</p>
 * <p><em>What is the fewest steps required to move starting from any square with elevation <code>a</code> to the location that should get the best signal?</em></p>
 */
public class Main implements Runnable {

    private class Node {
        private final int col, row;
        private final PriorityQueue<Node> neighbours = new PriorityQueue<>((x, y) -> {
            final double euclideanDistanceX = Math.sqrt((x.row - end.row) * (x.row - end.row) + (x.col - end.col) * (x.col - end.col));
            final double euclideanDistanceY = Math.sqrt((y.row - end.row) * (y.row - end.row) + (y.col - end.col) * (y.col - end.col));
            return Double.compare(euclideanDistanceX, euclideanDistanceY);
        });
        private Node previous = null;
        private int distance = Integer.MAX_VALUE;

        private Node(final int row, final int col) {
            this.row = row;
            this.col = col;
        }

        public void addNeighbour(Node node) {
            this.neighbours.add(node);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Node node = (Node) o;

            if (col != node.col) {
                return false;
            }
            return row == node.row;
        }

        @Override
        public int hashCode() {
            int result = col;
            result = 31 * result + row;
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Node{");
            sb.append("col=")
              .append(col);
            sb.append(", row=")
              .append(row);
            sb.append(", distance=")
              .append(distance);
            if (previous != null) {
                sb.append(", previous=")
                  .append(String.format("{%d, %d}", previous.row, previous.col));
            }
            sb.append(", neighbours=[")
              .append(neighbours.stream()
                                .map(x -> String.format("{%d, %d}", x.row, x.col))
                                .collect(Collectors.joining(", ")));
            sb.append(']');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            final int rows = input.size();
            final int cols = input.get(0)
                                  .length();
            final int[][] grid = new int[rows][cols];
            final List<Integer> startRows = new ArrayList<>();
            final List<Integer> startCols = new ArrayList<>();
            int endRow, endCol;
            endRow = endCol = -1;
            for (int row = 0; row < rows; row++) {
                final String s = input.get(row);
                final char[] chars = s.toCharArray();
                for (int col = 0; col < cols; col++) {
                    char c = chars[col];
                    if (c == 'S' || c == 'a') {
                        startRows.add(row);
                        startCols.add(col);
                        c = 'a';
                    } else if (c == 'E') {
                        endRow = row;
                        endCol = col;
                        c = 'z';
                    }

                    grid[row][col] = c;
                }
            }

            final Node[][] nodes = new Node[rows][cols];
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    nodes[row][col] = new Node(row, col);
                }
            }
            for (int i = 0; i < startRows.size(); i++) {
                final Node start = nodes[startRows.get(i)][startCols.get(i)];
                start.distance = 0;
                this.starts.add(start);
            }
            this.end = nodes[endRow][endCol];

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    final Node node = nodes[row][col];
                    final int curr = grid[row][col];
                    if (col > 0) {
                        int left = grid[row][col - 1];
                        int heightDifference = curr - left;
                        if (heightDifference >= -1) {
                            node.addNeighbour(nodes[row][col - 1]);
                        }
                    }
                    if (row > 0) {
                        int top = grid[row - 1][col];
                        int heightDifference = curr - top;
                        if (heightDifference >= -1) {
                            node.addNeighbour(nodes[row - 1][col]);
                        }
                    }
                    if (col < cols - 1) {
                        int right = grid[row][col + 1];
                        int heightDifference = curr - right;
                        if (heightDifference >= -1) {
                            node.addNeighbour(nodes[row][col + 1]);
                        }
                    }
                    if (row < rows - 1) {
                        int bottom = grid[row + 1][col];
                        int heightDifference = curr - bottom;
                        if (heightDifference >= -1) {
                            node.addNeighbour(nodes[row + 1][col]);
                        }
                    }
                }
            }

            System.out.println(nodes[1][0].neighbours);
            dfs();
            for (Node[] row : nodes) {
                System.out.print("[");
                for (Node cell : row) {
                    System.out.print(cell.distance);
                    System.out.print(", ");
                }
                System.out.println("]");
            }
            System.out.println(nodes[endRow][endCol].distance);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<Node> starts = new ArrayList<>();
    private Node end = null;

    private void dfs() {
        for (Node start : starts) {
            dfs(start, 1);
        }
    }

    private long millis = System.currentTimeMillis();

    private void dfs(Node node, int distance) {
        if (node == end) {
            System.out.println("POSSIBLE SOLUTION: " + distance);
        }
        final long curr = System.currentTimeMillis();
        if (curr - millis > 5000L) {
            millis = curr;
//            System.out.printf("Node: %s, End: %s (%d)%n", node, end, distance);
        }
        for (Node neighbour : node.neighbours) {
            if (neighbour.distance > distance) {
                neighbour.distance = distance;
                neighbour.previous = node;
                dfs(neighbour, distance + 1);
            }
        }
    }
}
