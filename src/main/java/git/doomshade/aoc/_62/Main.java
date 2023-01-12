package git.doomshade.aoc._62;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>Your device's communication system is correctly detecting packets, but still isn't working. It looks like it also needs to look
 * for <em>messages</em>.</p>
 * <p>A <em>start-of-message marker</em> is just like a start-of-packet marker, except it consists of <em>14 distinct characters</em> rather than 4.</p>
 * <p>Here are the first positions of start-of-message markers for all of the above examples:</p>
 * <ul>
 * <li><code>mjqjpqmgbljsphdztnvjfqwrcgsmlb</code>: first marker after character <code><em>19</em></code></li>
 * <li><code>bvwbjplbgvbhsrlpgdmjqwftvncz</code>: first marker after character <code><em>23</em></code></li>
 * <li><code>nppdvjthqldpwncqszvftbrmjlhg</code>: first marker after character <code><em>23</em></code></li>
 * <li><code>nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg</code>: first marker after character <code><em>29</em></code></li>
 * <li><code>zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw</code>: first marker after character <code><em>26</em></code></li>
 * </ul>
 * <p><em>How many characters need to be processed before the first start-of-message marker is detected?</em></p>
 */
public class Main implements Runnable {

    public static final int CHARS_CHECKED = 14;

    private static void setBit(char c, AtomicInteger bits) {
        final int bit = 1 << (c - 'a');
        bits.set(bits.get() | bit);
    }

    private static void unsetBit(char c, AtomicInteger bits) {
        final int bit = 1 << (c - 'a');
        bits.set(bits.get() & ~bit);
    }

    private static boolean isSetBit(char c, AtomicInteger bits) {
        final int bit = 1 << (c - 'a');
        return (bits.get() & bit) != 0;
    }

    @Override
    public void run() {
        try {
            final Deque<Character> deque = new LinkedList<>();
            final String input = Util.readStringInput(getClass(), "input.txt")
                                     .get(0);
            for (int j = 0; j < CHARS_CHECKED; j++) {
                final char c = input.charAt(j);
                deque.add(c);
            }

            for (int j = CHARS_CHECKED; j < input.length() && !foundSolution(deque, j); j++) {
                deque.removeFirst();
                deque.addLast(input.charAt(j));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean foundSolution(final Deque<Character> deque, int position) {
        final AtomicInteger bits = new AtomicInteger();
        boolean allUnique = true;
        for (char c : deque) {
            if (isSetBit(c, bits)) {
                allUnique = false;
                break;
            }
            setBit(c, bits);
        }
        if (allUnique) {
            System.out.println(position);
            return true;
        }
        return false;
    }

}
