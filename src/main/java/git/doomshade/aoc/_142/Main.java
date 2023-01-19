package git.doomshade.aoc._142;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>You realize you misread the scan. There isn't an <span title="Endless Void is my C cover band.">endless void</span> at the bottom
 * of the scan - there's floor, and you're standing on it!</p>
 * <p>You don't have time to scan the floor, so assume the floor is an infinite horizontal line with a <code>y</code> coordinate equal to <em>two plus the highest
 * <code>y</code> coordinate</em> of any point in your scan.</p>
 * <p>In the example above, the highest <code>y</code> coordinate of any point is <code>9</code>, and so the floor is at <code>y=11</code>. (This is as if your scan
 * contained one extra rock path like <code>-infinity,11 -&gt; infinity,11</code>.) With the added floor, the example above now looks like this:</p>
 * <pre><code>
 *         ...........+........
 *         ....................
 *         ....................
 *         ....................
 *         .........#...##.....
 *         .........#...#......
 *         .......###...#......
 *         .............#......
 *         .............#......
 *         .....#########......
 *         ....................
 * &lt;-- etc #################### etc --&gt;
 * </code></pre>
 * <p>To find somewhere safe to stand, you'll need to simulate falling sand until a unit of sand comes to rest at <code>500,0</code>, blocking the source entirely and
 * stopping the flow of sand into the cave. In the example above, the situation finally looks like this after <code><em>93</em></code> units of sand come to rest:</p>
 * <pre><code>
 * ............o............
 * ...........ooo...........
 * ..........ooooo..........
 * .........ooooooo.........
 * ........oo#ooo##o........
 * .......ooo#ooo#ooo.......
 * ......oo###ooo#oooo......
 * .....oooo.oooo#ooooo.....
 * ....oooooooooo#oooooo....
 * ...ooo#########ooooooo...
 * ..ooooo.......ooooooooo..
 * #########################
 * </code></pre>
 * <p>Using your scan, simulate the falling sand until the source of the sand becomes blocked. <em>How many units of sand come to rest?</em></p>
 */
public class Main implements Runnable {
    private static final Point SAND_SPAWN_POINT = new Point(500, 0);
    private static final byte AIR = 0;
    private static final byte ROCK = 1;
    private static final byte SAND = 2;

    private record Point(int x, int y) {
        public Point add(Point other) {
            return new Point(x + other.x, y + other.y);
        }

        public Point subtract(Point other) {
            return new Point(x - other.x, y - other.y);
        }
    }

    private static final Pattern POINT_PATTERN = Pattern.compile("(?<x>\\d+),(?<y>\\d+)");

    private static List<Point> parseLines(final String input) {
        final Matcher matcher = POINT_PATTERN.matcher(input);
        final List<Point> points = new LinkedList<>();

        while (matcher.find()) {
            final int x = Integer.parseInt(matcher.group("x"));
            final int y = Integer.parseInt(matcher.group("y"));
            points.add(new Point(x, y));
        }

        return points;
    }

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            final List<List<Point>> verticesList = new ArrayList<>();
            int maxX, maxY;
            maxX = maxY = Integer.MIN_VALUE;
            for (final String s : input) {
                final List<Point> points = parseLines(s);
                maxX = updateMax(maxX, points, x -> x.x);
                maxY = updateMax(maxY, points, x -> x.y);
                verticesList.add(points);
            }

            // change from part 1: set maxY to 3 for the extra floor
            final byte[][] tiles = new byte[maxY + 3][maxX + 1 + maxY + 3];
            for (final List<Point> vertices : verticesList) {
                mapToTiles(tiles, vertices);
            }
            for (int x = 0; x < tiles[0].length; x++) {
                tiles[maxY + 2][x] = ROCK;
            }
            int result = simulateSandFall(tiles);
            if (result < 0) {
                System.out.println("Last sand could not fit. F");
            }
            printTiles(tiles, System.out);
            System.out.println("Result: " + result);
//            printTiles(tiles, System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int simulateSandFall(final byte[][] tiles) {
        int result = 0;

        // main sand fall loop
        mainLoop:
        while (true) {

            // a single unit of sand falling
            final SandState sandState = simulateSingleUnit(tiles);
            switch (sandState) {
                case COULD_NOT_FIT -> {
                    break mainLoop;
                }
                case RESTING -> result++;
                default -> throw new IllegalStateException("Unexpected value: " + sandState);
            }
        }
        return result;
    }

    private SandState simulateSingleUnit(final byte[][] tiles) {
        int x = SAND_SPAWN_POINT.x;
        int y = SAND_SPAWN_POINT.y;

        if (tiles[y][x] == SAND) {
            return SandState.COULD_NOT_FIT;
        }

        while (true) {
            if (y + 1 == tiles.length) {
                return SandState.FELL_INTO_ABYSS;
            }
            if (tiles[y + 1][x] == AIR) {
                y++;
            } else if (x > 0 && tiles[y + 1][x - 1] == AIR) {
                y++;
                x--;
            } else if (x < tiles[0].length - 1 && tiles[y + 1][x + 1] == AIR) {
                y++;
                x++;
            } else {
                tiles[y][x] = SAND;
                return SandState.RESTING;
            }
        }
    }

    private enum SandState {
        COULD_NOT_FIT,
        RESTING,
        FELL_INTO_ABYSS
    }

    private void printTiles(final byte[][] tiles, final OutputStream out) throws IOException {
        for (final byte[] tile : tiles) {
            for (final byte b : tile) {
                switch (b) {
                    case AIR -> out.write('.');
                    case ROCK -> out.write('#');
                    case SAND -> out.write('o');
                }
            }
            out.write('\n');
        }
    }

    private void mapToTiles(final byte[][] tiles, final List<Point> vertices) {
        Point prevVertex = null;
        for (final Point vertex : vertices) {
            if (prevVertex != null) {
                final Point delta = vertex.subtract(prevVertex);

                final boolean changeHorizontal = delta.x != 0;
                final boolean changeVertical = delta.y != 0;
                if (changeHorizontal) {
                    final int start = Math.min(prevVertex.x, vertex.x);
                    final int end = Math.max(prevVertex.x, vertex.x);
                    final int y = prevVertex.y;
                    for (int x = start; x <= end; x++) {
                        tiles[y][x] = ROCK;
                    }
                } else if (changeVertical) {
                    final int start = Math.min(prevVertex.y, vertex.y);
                    final int end = Math.max(prevVertex.y, vertex.y);
                    final int x = prevVertex.x;
                    for (int y = start; y <= end; y++) {
                        tiles[y][x] = ROCK;
                    }
                } else {
                    throw new IllegalStateException();
                }
            }
            prevVertex = vertex;
        }
    }

    private static int updateMax(final int currMax, final Collection<Point> points, final ToIntFunction<Point> mapFunction) {
        return Math.max(points.stream()
                              .mapToInt(mapFunction)
                              .max()
                              .orElse(currMax), currMax);
    }
}
