package git.doomshade.aoc._151;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2>--- Day 15: Beacon Exclusion Zone ---</h2><p>You feel the ground rumble again as the distress signal leads you to a large network of
 * subterranean tunnels. You don't have time to search them all, but you don't need to: your pack contains a set of deployable
 * <em>sensors</em> that you imagine were originally built to locate lost Elves .</p>
 * <p>The sensors aren't very powerful, but that's okay; your handheld device indicates that you're close enough to the source of the
 * distress signal to use them. You pull the emergency sensor system out of your pack, hit the big button on top, and the sensors zoom off down the tunnels.</p>
 * <p>Once a sensor finds a spot it thinks will give it a good reading, it attaches itself to a hard surface and begins monitoring for the
 * nearest signal source
 * <em>beacon</em>. Sensors and beacons always exist at integer coordinates. Each sensor knows its own position and can <em>determine the
 * position of a beacon precisely</em>; however, sensors can only lock on to the one beacon <em>closest to the sensor</em> as measured by the
 * <a href="https://en.wikipedia.org/wiki/Taxicab_geometry" target="_blank">Manhattan distance</a>
 * . (There is never a tie where two beacons are the same distance to a sensor.)</p>
 * <p>It doesn't take long for the sensors to report back their positions and closest beacons (your puzzle input). For example:</p>
 * <pre><code>
 * Sensor at x=2, y=18: closest beacon is at x=-2, y=15
 * Sensor at x=9, y=16: closest beacon is at x=10, y=16
 * Sensor at x=13, y=2: closest beacon is at x=15, y=3
 * Sensor at x=12, y=14: closest beacon is at x=10, y=16
 * Sensor at x=10, y=20: closest beacon is at x=10, y=16
 * Sensor at x=14, y=17: closest beacon is at x=10, y=16
 * Sensor at x=8, y=7: closest beacon is at x=2, y=10
 * Sensor at x=2, y=0: closest beacon is at x=2, y=10
 * Sensor at x=0, y=11: closest beacon is at x=2, y=10
 * Sensor at x=20, y=14: closest beacon is at x=25, y=17
 * Sensor at x=17, y=20: closest beacon is at x=21, y=22
 * Sensor at x=16, y=7: closest beacon is at x=15, y=3
 * Sensor at x=14, y=3: closest beacon is at x=15, y=3
 * Sensor at x=20, y=1: closest beacon is at x=15, y=3
 * </code></pre>
 * <p>So, consider the sensor at <code>2,18</code>; the closest beacon to it is at <code>-2,15</code>. For the sensor at <code>9,16</code>,
 * the closest beacon to it is at
 * <code>10,16</code>.</p>
 * <p>Drawing sensors as <code>S</code> and beacons as <code>B</code>, the above arrangement of sensors and beacons looks like this:</p>
 * <pre><code>
 *                1    1    2    2
 *      0    5    0    5    0    5
 *  0 ....S.......................
 *  1 ......................S.....
 *  2 ...............S............
 *  3 ................SB..........
 *  4 ............................
 *  5 ............................
 *  6 ............................
 *  7 ..........S.......S.........
 *  8 ............................
 *  9 ............................
 * 10 ....B.......................
 * 11 ..S.........................
 * 12 ............................
 * 13 ............................
 * 14 ..............S.......S.....
 * 15 B...........................
 * 16 ...........SB...............
 * 17 ................S..........B
 * 18 ....S.......................
 * 19 ............................
 * 20 ............S......S........
 * 21 ............................
 * 22 .......................B....
 * </code></pre>
 * <p>This isn't necessarily a comprehensive map of all beacons in the area, though. Because each sensor only identifies its closest
 * beacon, if a sensor detects a beacon, you know there are no other beacons that close or closer to that sensor. There could still be beacons that just happen to not be the closest beacon to any
 * sensor. Consider the sensor at <code>8,7</code>:</p>
 * <pre><code>
 *                1    1    2    2
 *      0    5    0    5    0    5
 * -2 ..........#.................
 * -1 .........###................
 *  0 ....S...#####...............
 *  1 .......#######........S.....
 *  2 ......#########S............
 *  3 .....###########SB..........
 *  4 ....#############...........
 *  5 ...###############..........
 *  6 ..#################.........
 *  7 .#########<em>S</em>#######S#........
 *  8 ..#################.........
 *  9 ...###############..........
 * 10 ....<em>B</em>############...........
 * 11 ..S..###########............
 * 12 ......#########.............
 * 13 .......#######..............
 * 14 ........#####.S.......S.....
 * 15 B........###................
 * 16 ..........#SB...............
 * 17 ................S..........B
 * 18 ....S.......................
 * 19 ............................
 * 20 ............S......S........
 * 21 ............................
 * 22 .......................B....
 * </code></pre>
 * <p>This sensor's closest beacon is at <code>2,10</code>, and so you know there are no beacons that close or closer (in any positions
 * marked <code>#</code>).</p>
 * <p>None of the detected beacons seem to be producing the distress signal, so you'll need to <span title="&quot;When you have eliminated
 * all which is impossible, then whatever remains, however improbable, must be where the missing beacon is.&quot; - Sherlock Holmes">work out</span> where the distress beacon is by working out where
 * it
 * <em>isn't</em>. For now, keep things simple by counting the positions where a beacon cannot possibly be along just a single row.</p>
 * <p>So, suppose you have an arrangement of beacons and sensors like in the example above and, just in the row where <code>y=10</code>,
 * you'd like to count the number of positions a beacon cannot possibly exist. The coverage from all sensors near that row looks like this:</p>
 * <pre><code>
 *                  1    1    2    2
 *        0    5    0    5    0    5
 *  9 ...#########################...
 * <em>10 ..####B######################..</em>
 * 11 .###S#############.###########.
 * </code></pre>
 * <p>In this example, in the row where <code>y=10</code>, there are <code><em>26</em></code> positions where a beacon cannot be
 * present.</p>
 * <p>Consult the report from the sensors you just deployed. <em>In the row where <code>y=2000000</code>, how many positions cannot contain
 * a beacon?</em></p>
 */
public class Main implements Runnable {
    // Sensor at x=2, y=18: closest beacon is at x=-2, y=15
    private static final Pattern SENSOR_PATTERN = Pattern.compile(".+x=(?<sensorX>-?\\d+), y=(?<sensorY>-?\\d+).+x=(?<beaconX>-?\\d+), y=(?<beaconY>-?\\d+)");
    private static final byte COULD_BE_BEACON = 0;
    private static final byte COULD_NOT_BE_BEACON = 1;
    private static final byte SENSOR = 2;
    private static final byte BEACON = 3;
    public static final int EXTRA_SPACE = 10;
    private boolean continueExpanding = true;

    private int val(int curr) {
        return curr + EXTRA_SPACE;
    }

    private static class Point {
        final int x, y;

        private Point(final int x, final int y) {
            this.x = x;
            this.y = y;
        }
    }

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            final List<Point> sensors = new ArrayList<>();
            final List<Point> beacons = new ArrayList<>();

            for (String s : input) {
                final Matcher matcher = SENSOR_PATTERN.matcher(s);
                if (!matcher.find()) {
                    throw new IllegalStateException();
                }
                final int sensorX = Integer.parseInt(matcher.group("sensorX"));
                final int sensorY = Integer.parseInt(matcher.group("sensorY"));
                final int beaconX = Integer.parseInt(matcher.group("beaconX"));
                final int beaconY = Integer.parseInt(matcher.group("beaconY"));

                final Point sensor = new Point(sensorX, sensorY);
                final Point beacon = new Point(beaconX, beaconY);

                sensors.add(sensor);
                beacons.add(beacon);
            }
            final int minSensorX = getMinValue(sensors, pt -> pt.x);
            final int minSensorY = getMinValue(sensors, pt -> pt.y);
            final int minBeaconX = getMinValue(beacons, pt -> pt.x);
            final int minBeaconY = getMinValue(beacons, pt -> pt.y);

            final int maxSensorX = getMaxValue(sensors, pt -> pt.x);
            final int maxSensorY = getMaxValue(sensors, pt -> pt.y);
            final int maxBeaconX = getMaxValue(beacons, pt -> pt.x);
            final int maxBeaconY = getMaxValue(beacons, pt -> pt.y);

            final int minX = Math.min(minSensorX, minBeaconX);
            final int minY = Math.min(minSensorY, minBeaconY);
            final int maxX = Math.max(maxSensorX, maxBeaconX);
            final int maxY = Math.max(maxSensorY, maxBeaconY);

            System.out.printf("min: %d, %d; max: %d, %d%n", minX, minY, maxX, maxY);
            final long yy = val(maxY) + Math.abs(minY) + EXTRA_SPACE;
            final long xx = val(maxX) + Math.abs(minX) + EXTRA_SPACE;

            System.out.printf("ROWS: %d, COLS: %d, BYTES: %d%n", yy, xx, yy * xx);
            final byte[][] tiles = new byte[(int) yy][(int) xx];

            for (final Point p : beacons) {
                tiles[val(p.y)][val(p.x)] = BEACON;
            }
            for (final Point p : sensors) {
                tiles[val(p.y)][val(p.x)] = SENSOR;
            }

            for (int i = 0; i < sensors.size(); i++) {
                final Point sensor = sensors.get(i);
                final int actualSensorX = val(sensor.x);
                final int actualSensorY = val(sensor.y);

                final Point closestBeacon = beacons.get(i);
                final int actualBeaconX = val(closestBeacon.x);
                final int actualBeaconY = val(closestBeacon.y);

                continueExpanding = true;
                expand(tiles, new Point(actualSensorX, actualSensorY), new Point(actualBeaconX, actualBeaconY));

            }
            printTiles(tiles);
            final byte[] targetRow = tiles[val(9)];
            int res = 0;
            for (byte b : targetRow) {
                if (b == COULD_NOT_BE_BEACON) {
                    res++;
                }
            }
            System.out.println("RESULT: " + res);
            // TODO: markujeme, jake pozice jsme prohledali v matici. pote spocteme vsechny marknute pozice v radku 2000000
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printTiles(final byte[][] tiles) {
        for (int i = 0; i < tiles.length; i++) {
            final byte[] row = tiles[i];
            System.out.println(String.join("", mapToStr(row)));
        }
        System.out.println("-----------------------------");
    }

    private static String[] mapToStr(byte[] bytes) {
        final String[] arr = new String[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            final byte b = bytes[i];
            switch (b) {
                case COULD_BE_BEACON -> arr[i] = ".";
                case COULD_NOT_BE_BEACON -> arr[i] = "#";
                case SENSOR -> arr[i] = "S";
                case BEACON -> arr[i] = "B";
                default -> throw new IllegalArgumentException("Invalid byte: " + b);
            }
        }
        return arr;
    }


    private void expand(final byte[][] tiles, final Point signal, final Point beacon) {
        final int signalXMaxBounds = tiles[0].length;
        final int signalYMaxBounds = tiles.length;

        final Queue<Point> searchQueue = new LinkedList<>();
        searchQueue.add(signal);

        while (!searchQueue.isEmpty()) {
            final Point tile = searchQueue.poll();
            if (tile.x == beacon.x && tile.y == beacon.y) {
                return;
            }

            // left
            if (isInBounds(tile.y, 0) && isInBounds(tile.x, -1) && tiles[tile.y][tile.x - 1] == COULD_BE_BEACON) {
                tiles[tile.y][tile.x - 1] = COULD_NOT_BE_BEACON;
            }
            searchQueue.add(new Point(tile.x - 1, tile.y));

            // up
            if (isInBounds(tile.y, -1) && isInBounds(tile.x, 0) && tiles[tile.y - 1][tile.x] == COULD_BE_BEACON) {
                tiles[tile.y - 1][tile.x] = COULD_NOT_BE_BEACON;
            }
            searchQueue.add(new Point(tile.x, tile.y - 1));

            // right
            if (isInBounds(tile.y, 0) && isInBounds(tile.x, +1) && tiles[tile.y][tile.x + 1] == COULD_BE_BEACON) {
                tiles[tile.y][tile.x + 1] = COULD_NOT_BE_BEACON;
            }
            searchQueue.add(new Point(tile.x + 1, tile.y));

            // down
            if (isInBounds(tile.y, +1) && isInBounds(tile.x, 0) && tiles[tile.y + 1][tile.x] == COULD_BE_BEACON) {
                tiles[tile.y + 1][tile.x] = COULD_NOT_BE_BEACON;
            }
            searchQueue.add(new Point(tile.x, tile.y + 1));
        }
    }

    private static boolean isInBounds(int signal, int signalDelta) {
        return (signal + signalDelta) >= 0;
    }

    private static int getMaxValue(final List<Point> sensors, final ToIntFunction<Point> mapToInt) {
        return sensors.stream()
                      .mapToInt(mapToInt)
                      .max()
                      .orElseThrow();
    }

    private static int getMinValue(final List<Point> sensors, final ToIntFunction<Point> mapToInt) {
        return sensors.stream()
                      .mapToInt(mapToInt)
                      .min()
                      .orElseThrow();
    }
}
