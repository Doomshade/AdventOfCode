package git.doomshade.aoc._132;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>Now, you just need to put <em>all</em> of the packets in the right order. Disregard the blank lines in your list of received
 * packets.</p>
 * <p>The distress signal protocol also requires that you include two additional <em>divider packets</em>:</p>
 * <pre><code>
 * [[2]]
 * [[6]]
 * </code></pre>
 * <p>Using the same rules as before, organize all packets - the ones in your list of received packets as well as the two divider packets - into the correct order.</p>
 * <p>For the example above, the result of putting the packets in the correct order is:</p>
 * <pre><code>
 * []
 * [[]]
 * [[[]]]
 * [1,1,3,1,1]
 * [1,1,5,1,1]
 * [[1],[2,3,4]]
 * [1,[2,[3,[4,[5,6,0]]]],8,9]
 * [1,[2,[3,[4,[5,6,7]]]],8,9]
 * [[1],4]
 * <em>[[2]]</em>
 * [3]
 * [[4,4],4,4]
 * [[4,4],4,4,4]
 * <em>[[6]]</em>
 * [7,7,7]
 * [7,7,7,7]
 * [[8,7,6]]
 * [9]
 * </code></pre>
 * <p>Afterward, locate the divider packets. To find the <em>decoder key</em> for this distress signal, you need to determine the indices of the two divider packets and
 * multiply them together. (The first packet is at index 1, the second packet is at index 2, and so on.) In this example, the divider packets are <em>10th</em> and
 * <em>14th</em>, and so the decoder key is <code><em>140</em></code>.</p>
 * <p>Organize all of the packets into the correct order. <em>What is the decoder key for the distress signal?</em></p>
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
        } while ((next = iterator.next()) != CharacterIterator.DONE);
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

            final List<DataArray> packets = packetPairs.stream()
                                                       .collect(ArrayList::new, (list, packetPair) -> {
                                                           list.add(packetPair.left);
                                                           list.add(packetPair.right);
                                                       }, ArrayList::addAll);
            final DataArray first = getDividerPacket(2);
            final DataArray second = getDividerPacket(6);
            packets.add(first);
            packets.add(second);
            packets.sort(Main::compareDataArrays);
            final int firstPacketIndex = packets.indexOf(first) + 1;
            final int secondPacketIndex = packets.indexOf(second) + 1;
            System.out.println(firstPacketIndex * secondPacketIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataArray getDividerPacket(final int value) {
        final DataArray outer = new DataArray();
        final DataArray inner = new DataArray();
        final Data data = new Data(value);
        inner.addData(data);
        outer.addData(inner);
        return outer;
    }
}
