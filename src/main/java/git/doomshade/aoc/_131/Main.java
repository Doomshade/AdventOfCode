package git.doomshade.aoc._131;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2>--- Day 13: Distress Signal ---</h2><p>You climb the hill and again try contacting the Elves. However, you instead receive a signal you weren't expecting: a
 * <em>distress signal</em>.</p>
 * <p>Your handheld device must still not be working properly; the packets from the distress signal got decoded <em>out of order</em>. You'll need to re-order the list of
 * received packets (your puzzle input) to decode the message.</p>
 * <p>Your list consists of pairs of packets; pairs are separated by a blank line. You need to identify <em>how many pairs of packets are in the right order</em>.</p>
 * <p>For example:</p>
 * <pre><code>
 * [1,1,3,1,1]
 * [1,1,5,1,1]
 *
 * [[1],[2,3,4]]
 * [[1],4]
 *
 * [9]
 * [[8,7,6]]
 *
 * [[4,4],4,4]
 * [[4,4],4,4,4]
 *
 * [7,7,7,7]
 * [7,7,7]
 *
 * []
 * [3]
 *
 * [[[]]]
 * [[]]
 *
 * [1,[2,[3,[4,[5,6,7]]]],8,9]
 * [1,[2,[3,[4,[5,6,0]]]],8,9]
 * </code></pre>
 * <p><span title="The snailfish called. They want their distress signal back.">Packet data consists of lists and integers.</span> Each list starts with <code>[</code>,
 * ends with <code>]</code>, and contains zero or more comma-separated values (either integers or other lists). Each packet is always a list and appears on its own line.</p>
 * <p>When comparing two values, the first value is called <em>left</em> and the second value is called <em>right</em>. Then:</p>
 * <ul>
 * <li>If <em>both values are integers</em>, the <em>lower integer</em> should come first. If the left integer is lower than the right integer, the inputs are in the right
 * order. If the left integer is higher than the right integer, the inputs are not in the right order. Otherwise, the inputs are the same integer; continue checking the
 * next part of the input.</li>
 * <li>If <em>both values are lists</em>, compare the first value of each list, then the second value, and so on. If the left list runs out of items first, the inputs are
 * in the right order. If the right list runs out of items first, the inputs are not in the right order. If the lists are the same length and no comparison makes a decision
 * about the order, continue checking the next part of the input.</li>
 * <li>If <em>exactly one value is an integer</em>, convert the integer to a list which contains that integer as its only value, then retry the comparison. For example, if
 * comparing <code>[0,0,0]</code> and <code>2</code>, convert the right value to <code>[2]</code> (a list containing <code>2</code>); the result is then found by instead
 * comparing <code>[0,0,0]</code> and <code>[2]</code>.</li>
 * </ul>
 * <p>Using these rules, you can determine which of the pairs in the example are in the right order:</p>
 * <pre><code>
 * == Pair 1 ==
 * - Compare [1,1,3,1,1] vs [1,1,5,1,1]
 *   - Compare 1 vs 1
 *   - Compare 1 vs 1
 *   - Compare 3 vs 5
 *     - Left side is smaller, so inputs are <em>in the right order</em>
 *
 * == Pair 2 ==
 * - Compare [[1],[2,3,4]] vs [[1],4]
 *   - Compare [1] vs [1]
 *     - Compare 1 vs 1
 *   - Compare [2,3,4] vs 4
 *     - Mixed types; convert right to [4] and retry comparison
 *     - Compare [2,3,4] vs [4]
 *       - Compare 2 vs 4
 *         - Left side is smaller, so inputs are <em>in the right order</em>
 *
 * == Pair 3 ==
 * - Compare [9] vs [[8,7,6]]
 *   - Compare 9 vs [8,7,6]
 *     - Mixed types; convert left to [9] and retry comparison
 *     - Compare [9] vs [8,7,6]
 *       - Compare 9 vs 8
 *         - Right side is smaller, so inputs are <em>not</em> in the right order
 *
 * == Pair 4 ==
 * - Compare [[4,4],4,4] vs [[4,4],4,4,4]
 *   - Compare [4,4] vs [4,4]
 *     - Compare 4 vs 4
 *     - Compare 4 vs 4
 *   - Compare 4 vs 4
 *   - Compare 4 vs 4
 *   - Left side ran out of items, so inputs are <em>in the right order</em>
 *
 * == Pair 5 ==
 * - Compare [7,7,7,7] vs [7,7,7]
 *   - Compare 7 vs 7
 *   - Compare 7 vs 7
 *   - Compare 7 vs 7
 *   - Right side ran out of items, so inputs are <em>not</em> in the right order
 *
 * == Pair 6 ==
 * - Compare [] vs [3]
 *   - Left side ran out of items, so inputs are <em>in the right order</em>
 *
 * == Pair 7 ==
 * - Compare [[[]]] vs [[]]
 *   - Compare [[]] vs []
 *     - Right side ran out of items, so inputs are <em>not</em> in the right order
 *
 * == Pair 8 ==
 * - Compare [1,[2,[3,[4,[5,6,7]]]],8,9] vs [1,[2,[3,[4,[5,6,0]]]],8,9]
 *   - Compare 1 vs 1
 *   - Compare [2,[3,[4,[5,6,7]]]] vs [2,[3,[4,[5,6,0]]]]
 *     - Compare 2 vs 2
 *     - Compare [3,[4,[5,6,7]]] vs [3,[4,[5,6,0]]]
 *       - Compare 3 vs 3
 *       - Compare [4,[5,6,7]] vs [4,[5,6,0]]
 *         - Compare 4 vs 4
 *         - Compare [5,6,7] vs [5,6,0]
 *           - Compare 5 vs 5
 *           - Compare 6 vs 6
 *           - Compare 7 vs 0
 *             - Right side is smaller, so inputs are <em>not</em> in the right order
 * </code></pre>
 * <p>What are the indices of the pairs that are already <em>in the right order</em>? (The first pair has index 1, the second pair has index 2, and so on.) In the above
 * example, the pairs in the right order are 1, 2, 4, and 6; the sum of these indices is <code><em>13</em></code>.</p>
 * <p>Determine which pairs of packets are already in the right order. <em>What is the sum of the indices of those pairs?</em></p>
 */
public class Main implements Runnable {

    private static class Data {
        private final Integer value;

        private Data() {
            this(null);
        }

        private Data(Integer value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value == null ? "" : value.toString();
        }
    }

    private static int compareData(Data left, Data right) {
        // instances are the same
        if (left.getClass()
                .equals(right.getClass())) {
            if (left.getClass()
                    .equals(Data.class)) {
                System.out.printf("- Compare %s vs %s%n", left, right);
                return left.value.compareTo(right.value);
            }
            if (left.getClass()
                    .equals(DataArray.class)) {
                return compareDataArrays((DataArray) left, (DataArray) right);
            }
            throw new IllegalStateException("Unknown implementation");
        }

        // instances are not the same
        // check for left first
        Data temp;
        if (left.getClass()
                .equals(Data.class)) {
            temp = left;
            left = new DataArray();
            ((DataArray) left).addData(temp);
        } else {
            temp = right;
            right = new DataArray();
            ((DataArray) right).addData(temp);
        }

        return compareDataArrays((DataArray) left, (DataArray) right);
    }

    private static int compareDataArrays(final DataArray left, final DataArray right) {
        System.out.printf("- Compare arr %s vs %s%n", left, right);
        final List<Data> leftInnerData = left.innerData;
        final List<Data> rightInnerData = right.innerData;
        for (int i = 0; i < rightInnerData.size(); i++) {
            if (leftInnerData.size() == i) {
                System.out.printf("- Left side ran out of items, so inputs are in the right order%n");
                return -1;
            }
            final Data l = leftInnerData.get(i);
            final Data r = rightInnerData.get(i);
            final int cmp = compareData(l, r);
            if (cmp != 0) {
                System.out.printf("RIP %s %s%n", l, r);
                if (cmp < 0) {
                    System.out.println("- Left side is smaller, so inputs are in the right order");
                } else {
                    System.out.println("- Right side is smaller, so inputs are not in the right order");
                }
                return cmp;
            }
        }
        final boolean differentSize = leftInnerData.size() > rightInnerData.size();
        // right size is smaller and we returned
        if (differentSize) {
            System.out.println("- Right side ran out of items, so inputs are not in the right order");
        }
        return Integer.compare(leftInnerData.size(), rightInnerData.size());
    }

    private static class DataArray extends Data {
        private final List<Data> innerData = new ArrayList<>();

        public void addData(Data data) {
            this.innerData.add(data);
        }

        @Override
        public String toString() {
            return "[" + innerData.stream()
                                  .map(Data::toString)
                                  .collect(Collectors.joining(", ")) + "]";
        }
    }

    private static final class PacketPair {
        private final DataArray left;
        private final DataArray right;

        private PacketPair(DataArray left, DataArray right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (PacketPair) obj;
            return Objects.equals(this.left, that.left) && Objects.equals(this.right, that.right);
        }

        @Override
        public int hashCode() {
            return Objects.hash(left, right);
        }

        @Override
        public String toString() {
            return "PacketPair[firstPacket=" + left + ", " + "secondPacket=" + right + ']';
        }

        public boolean comparePackets() {
            return compareDataArrays(left, right) < 0;
        }
    }

    private static DataArray parseDataArray(String inputStr) {
        final CharacterIterator iterator = new StringCharacterIterator(inputStr);
        if (iterator.current() != '[') {
            throw new IllegalArgumentException();
        }

        iterator.next();
        final DataArray rootContainer = new DataArray();
        parseDataArray(iterator, rootContainer);

        return rootContainer;
    }

    private static int parseInt(final CharacterIterator iterator) {
        char next = iterator.current();
        int parsedInt = 0;
        do {
            if (next >= '0' && next <= '9') {
                parsedInt = Math.multiplyExact(parsedInt, 10);
                parsedInt = Math.addExact(parsedInt, next - '0');
            } else {
                break;
            }
        }
        while ((next = iterator.next()) != CharacterIterator.DONE);
        return parsedInt;
    }

    private static void parseDataArray(final CharacterIterator iterator, final DataArray container) {
        if (iterator.current() == '[') {
            iterator.next();
            final DataArray newContainer = new DataArray();
            container.addData(newContainer);
            parseDataArray(iterator, newContainer);
        }
        if (iterator.current() == ']') {
            iterator.next();
            return;
        }
        if (iterator.current() == ',') {
            iterator.next();
            parseDataArray(iterator, container);
            return;
        }
        container.addData(new Data(parseInt(iterator)));
        parseDataArray(iterator, container);
    }

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            final List<PacketPair> packetPairs = new ArrayList<>();
            for (int i = 0; i < input.size(); i += 3) {
                final DataArray firstPacket = parseDataArray(input.get(i));
                final DataArray secondPacket = parseDataArray(input.get(i + 1));
                packetPairs.add(new PacketPair(firstPacket, secondPacket));
            }

            int result = 0;
            for (int i = 0; i < packetPairs.size(); i++) {
                final PacketPair packetPair = packetPairs.get(i);
                System.out.printf("%n== Pair %d ==%n", i + 1);
                if (packetPair.comparePackets()) {
                    result += i + 1;
                }
            }

            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
