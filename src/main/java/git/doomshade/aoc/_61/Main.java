package git.doomshade.aoc._61;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <h2>--- Day 6: Tuning Trouble ---</h2><p>The preparations are finally complete; you and the Elves leave camp on foot and begin to make your way toward the <em
 * class="star">star</em> fruit grove.</p>
 * <p>As you move through the dense undergrowth, one of the Elves gives you a handheld <em>device</em>. He says that it has many fancy features, but the most important one
 * to set up right now is the <em>communication system</em>.</p>
 * <p>However, because he's heard you have <a href="/2016/day/6">significant</a>
 * <a href="/2016/day/25">experience</a>
 * <a href="/2019/day/7">dealing</a>
 * <a href="/2019/day/9">with</a>
 * <a href="/2019/day/16">signal-based</a>
 * <a href="/2021/day/25">systems</a>
 * , he convinced the other Elves that it would be okay to give you their one malfunctioning device - surely you'll have no problem fixing it.</p>
 * <p>As if inspired by comedic timing, the device emits a few <span title="The magic smoke, on the other hand, seems to be contained... FOR NOW!">colorful sparks</span>.</p>
 * <p>To be able to communicate with the Elves, the device needs to <em>lock on to their signal</em>. The signal is a series of seemingly-random characters that the device
 * receives one at a time.</p>
 * <p>To fix the communication system, you need to add a subroutine to the device that detects a <em>start-of-packet marker</em> in the datastream. In the protocol being
 * used by the Elves, the start of a packet is indicated by a sequence of <em>four characters that are all different</em>.</p>
 * <p>The device will send your subroutine a datastream buffer (your puzzle input); your subroutine needs to identify the first position where the four most recently
 * received characters were all different. Specifically, it needs to report the number of characters from the beginning of the buffer to the end of the first such
 * four-character marker.</p>
 * <p>For example, suppose you receive the following datastream buffer:</p>
 * <pre><code>mjqjpqmgbljsphdztnvjfqwrcgsmlb</code></pre>
 * <p>After the first three characters (<code>mjq</code>) have been received, there haven't been enough characters received yet to find the marker. The first time a marker
 * could occur is after the fourth character is received, making the most recent four characters <code>mjqj</code>. Because <code>j</code> is repeated, this isn't a marker
 * .</p>
 * <p>The first time a marker appears is after the <em>seventh</em> character arrives. Once it does, the last four characters received are <code>jpqm</code>, which are all
 * different. In this case, your subroutine should report the value <code><em>7</em></code>, because the first start-of-packet marker is complete after 7 characters have been
 * processed.</p>
 * <p>Here are a few more examples:</p>
 * <ul>
 * <li><code>bvwbjplbgvbhsrlpgdmjqwftvncz</code>: first marker after character <code><em>5</em></code></li>
 * <li><code>nppdvjthqldpwncqszvftbrmjlhg</code>: first marker after character <code><em>6</em></code></li>
 * <li><code>nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg</code>: first marker after character <code><em>10</em></code></li>
 * <li><code>zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw</code>: first marker after character <code><em>11</em></code></li>
 * </ul>
 * <p><em>How many characters need to be processed before the first start-of-packet marker is detected?</em></p>
 */
public class Main implements Runnable {

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
            for (int j = 0; j < 4; j++) {
                final char c = input.charAt(j);
                deque.add(c);
            }

            for (int j = 4; j < input.length() && !foundSolution(deque, j); j++) {
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
