package git.doomshade.aoc._121;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <h2>--- Day 12: Hill Climbing Algorithm ---</h2><p>You try contacting the Elves using your <span title="When you look up the specs for your handheld device, every field
 * just says &quot;plot&quot;.">handheld device</span>, but the river you're following must be too low to get a decent signal.</p>
 * <p>You ask the device for a heightmap of the surrounding area (your puzzle input). The heightmap shows the local area from above broken into a grid; the elevation of
 * each square of the grid is given by a single lowercase letter, where <code>a</code> is the lowest elevation, <code>b</code> is the next-lowest, and so on up to the highest
 * elevation, <code>z</code>.</p>
 * <p>Also included on the heightmap are marks for your current position (<code>S</code>) and the location that should get the best signal (<code>E</code>). Your current
 * position (<code>S</code>) has elevation <code>a</code>, and the location that should get the best signal (<code>E</code>) has elevation <code>z</code>.</p>
 * <p>You'd like to reach <code>E</code>, but to save energy, you should do it in <em>as few steps as possible</em>. During each step, you can move exactly one square up,
 * down, left, or right. To avoid needing to get out your climbing gear, the elevation of the destination square can be <em>at most one higher</em> than the elevation of your
 * current square; that is, if your current elevation is <code>m</code>, you could step to elevation <code>n</code>, but not to elevation <code>o</code>. (This also means that
 * the elevation of the destination square can be much lower than the elevation of your current square.)</p>
 * <p>For example:</p>
 * <pre><code>
 * <em>S</em>abqponm
 * abcryxxl
 * accsz<em>E</em>xk
 * acctuvwj
 * abdefghi
 * </code></pre>
 * <p>Here, you start in the top-left corner; your goal is near the middle. You could start by moving down or right, but eventually you'll need to head toward the
 * <code>e</code> at the bottom. From there, you can spiral around to the goal:</p>
 * <pre><code>
 * v..v&lt;&lt;&lt;&lt;
 * &gt;v.vv&lt;&lt;^
 * .&gt;vv&gt;E^^
 * ..v&gt;&gt;&gt;^^
 * ..&gt;&gt;&gt;&gt;&gt;^
 * </code></pre>
 * <p>In the above diagram, the symbols indicate whether the path exits each square moving up (<code>^</code>), down (<code>v</code>), left (<code>&lt;</code>), or right
 * (<code>&gt;</code>). The location that should get the best signal is still <code>E</code>, and <code>.</code> marks unvisited squares.</p>
 * <p>This path reaches the goal in <code><em>31</em></code> steps, the fewest possible.</p>
 * <p><em>What is the fewest steps required to move from your current position to the location that should get the best signal?</em></p>
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
            int startRow, startCol;
            int endRow, endCol;
            startRow = startCol = endRow = endCol = -1;
            for (int row = 0; row < rows; row++) {
                final String s = input.get(row);
                final char[] chars = s.toCharArray();
                for (int col = 0; col < cols; col++) {
                    char c = chars[col];
                    if (c == 'S') {
                        startRow = row;
                        startCol = col;
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
            this.start = nodes[startRow][startCol];
            this.start.distance = 0;
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

    private Node start = null;
    private Node end = null;

    private void dfs() {
        dfs(start, 1);
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
