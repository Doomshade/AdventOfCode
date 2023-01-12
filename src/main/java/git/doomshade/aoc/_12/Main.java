package git.doomshade.aoc._12;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.*;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>By the time you calculate the answer to the Elves' question, they've already realized that the Elf carrying the most Calories of
 * food might eventually <em>run out of snacks</em>.</p>
 * <p>To avoid this unacceptable situation, the Elves would instead like to know the total Calories carried by the <em>top three</em> Elves carrying the most Calories. That
 * way, even if one of those Elves runs out of snacks, they still have two backups.</p>
 * <p>In the example above, the top three Elves are the fourth Elf (with <code>24000</code> Calories), then the third Elf (with <code>11000</code> Calories), then the fifth
 * Elf (with <code>10000</code> Calories). The sum of the Calories carried by these three elves is <code><em>45000</em></code>.</p>
 * <p>Find the top three Elves carrying the most Calories. <em>How many Calories are those Elves carrying in total?</em></p>
 */
public class Main implements Runnable {
    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            final PriorityQueue<Integer> results = new PriorityQueue<>(Comparator.reverseOrder());
            int curr = 0;
            for (String s : input) {
                if (s.isEmpty()) {
                    results.offer(curr);
                    curr = 0;
                } else {
                    curr += Integer.parseInt(s);
                }
            }
            int res = 0;
            for (int i = 0; i < 3; i++) {
                res += results.poll();
            }
            System.out.println(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
