package git.doomshade.aoc._141;

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
 * <h2>--- Day 14: Regolith Reservoir ---</h2><p>The distress signal leads you to a giant waterfall! Actually, hang on - the signal seems like it's coming from the
 * waterfall itself, and that doesn't make any sense. However, you do notice a little path that leads <em>behind</em> the waterfall.</p>
 * <p>Correction: the distress signal leads you behind a giant waterfall! There seems to be a large cave system here, and the signal definitely leads further inside.</p>
 * <p>As you begin to make your way deeper underground, you feel the ground rumble for a moment. Sand begins pouring into the cave! If you don't quickly figure out where
 * the sand is going, you could quickly become trapped!</p>
 * <p>Fortunately, your <a href="/2018/day/17">familiarity</a>
 * with analyzing the path of falling material will come in handy here. You scan a two-dimensional vertical slice of the cave above you (your puzzle input) and discover that
 * it is mostly <em>air</em> with structures made of <em>rock</em>.</p>
 * <p>Your scan traces the path of each solid rock structure and reports the <code>x,y</code> coordinates that form the shape of the path, where <code>x</code> represents
 * distance to the right and <code>y</code> represents distance down. Each path appears as a single line of text in your scan. After the first point of each path, each point
 * indicates the end of a straight horizontal or vertical line to be drawn from the previous point. For example:</p>
 * <pre><code>498,4 -&gt; 498,6 -&gt; 496,6
 * 503,4 -&gt; 502,4 -&gt; 502,9 -&gt; 494,9
 * </code></pre>
 * <p>This scan means that there are two paths of rock; the first path consists of two straight lines, and the second path consists of three straight lines. (Specifically,
 * the first path consists of a line of rock from <code>498,4</code> through <code>498,6</code> and another line of rock from <code>498,6</code> through <code>496,6</code>
 * .)</p>
 * <p>The sand is pouring into the cave from point <code>500,0</code>.</p>
 * <p>Drawing rock as <code>#</code>, air as <code>.</code>, and the source of the sand as <code>+</code>, this becomes:</p>
 * <pre><code>
 *   4     5  5
 *   9     0  0
 *   4     0  3
 * 0 ......+...
 * 1 ..........
 * 2 ..........
 * 3 ..........
 * 4 ....#...##
 * 5 ....#...#.
 * 6 ..###...#.
 * 7 ........#.
 * 8 ........#.
 * 9 #########.
 * </code></pre>
 * <p>Sand is produced <em>one unit at a time</em>, and the next unit of sand is not produced until the previous unit of sand <em>comes to rest</em>. A unit of sand is
 * large enough to fill one tile of air in your scan.</p>
 * <p>A unit of sand always falls <em>down one step</em> if possible. If the tile immediately below is blocked (by rock or sand), the unit of sand attempts to instead move
 * diagonally <em>one step down and to the left</em>. If that tile is blocked, the unit of sand attempts to instead move diagonally <em>one step down and to the right</em>.
 * Sand keeps moving as long as it is able to do so, at each step trying to move down, then down-left, then down-right. If all three possible destinations are blocked, the
 * unit of sand <em>comes to rest</em> and no longer moves, at which point the next unit of sand is created back at the source.</p>
 * <p>So, drawing sand that has come to rest as <code>o</code>, the first unit of sand simply falls straight down and then stops:</p>
 * <pre><code>
 * ......+...
 * ..........
 * ..........
 * ..........
 * ....#...##
 * ....#...#.
 * ..###...#.
 * ........#.
 * ......<em>o</em>.#.
 * #########.
 * </code></pre>
 * <p>The second unit of sand then falls straight down, lands on the first one, and then comes to rest to its left:</p>
 * <pre><code>
 * ......+...
 * ..........
 * ..........
 * ..........
 * ....#...##
 * ....#...#.
 * ..###...#.
 * ........#.
 * .....oo.#.
 * #########.
 * </code></pre>
 * <p>After a total of five units of sand have come to rest, they form this pattern:</p>
 * <pre><code>
 * ......+...
 * ..........
 * ..........
 * ..........
 * ....#...##
 * ....#...#.
 * ..###...#.
 * ......o.#.
 * ....oooo#.
 * #########.
 * </code></pre>
 * <p>After a total of 22 units of sand:</p>
 * <pre><code>
 * ......+...
 * ..........
 * ......o...
 * .....ooo..
 * ....#ooo##
 * ....#ooo#.
 * ..###ooo#.
 * ....oooo#.
 * ...ooooo#.
 * #########.
 * </code></pre>
 * <p>Finally, only two more units of sand can possibly come to rest:</p>
 * <pre><code>
 * ......+...
 * ..........
 * ......o...
 * .....ooo..
 * ....#ooo##
 * ...<em>o</em>#ooo#.
 * ..###ooo#.
 * ....oooo#.
 * .<em>o</em>.ooooo#.
 * #########.
 * </code></pre>
 * <p>Once all <code><em>24</em></code> units of sand shown above have come to rest, all further sand flows out the bottom, falling into the endless void. Just for fun, the
 * path any new sand takes before falling forever is shown here with <code>~</code>:</p>
 * <pre><code>
 * .......+...
 * .......~...
 * ......~o...
 * .....~ooo..
 * ....~#ooo##
 * ...~o#ooo#.
 * ..~###ooo#.
 * ..~..oooo#.
 * .~o.ooooo#.
 * ~#########.
 * ~..........
 * ~..........
 * ~..........
 * </code></pre>
 * <p>Using your scan, simulate the falling sand. <em>How many units of sand come to rest before sand starts flowing into the abyss below?</em></p>
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
            final byte[][] tiles = new byte[maxY + 1][maxX + 1];
            for (final List<Point> vertices : verticesList) {
                mapToTiles(tiles, vertices);
            }
            int result = simulateSandFall(tiles);
            if (result < 0) {
                System.out.println("Last sand could not fit. F");
            } else {
                System.out.println("Result: " + result);
            }
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
                case FELL_INTO_ABYSS -> {
                    break mainLoop;
                }
                case COULD_NOT_FIT -> {
                    return -1;
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
            } else if (tiles[y + 1][x - 1] == AIR) {
                y++;
                x--;
            } else if (tiles[y + 1][x + 1] == AIR) {
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
