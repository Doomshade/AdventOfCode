package git.doomshade.aoc._32;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.List;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>As you finish identifying the misplaced items, the Elves come to you with another issue.</p>
 * <p>For safety, the Elves are divided into groups of three. Every Elf carries a badge that identifies their group. For efficiency, within each group of three Elves, the
 * badge is the <em>only item type carried by all three Elves</em>. That is, if a group's badge is item type <code>B</code>, then all three Elves will have item type
 * <code>B</code> somewhere in their rucksack, and at most two of the Elves will be carrying any other item type.</p>
 * <p>The problem is that someone forgot to put this year's updated authenticity sticker on the badges. All of the badges need to be pulled out of the rucksacks so the new
 * authenticity stickers can be attached.</p>
 * <p>Additionally, nobody wrote down which item type corresponds to each group's badges. The only way to tell which item type is the right one is by finding the one item
 * type that is <em>common between all three Elves</em> in each group.</p>
 * <p>Every set of three lines in your list corresponds to a single group, but each group can have a different badge item type. So, in the above example, the first group's
 * rucksacks are the first three lines:</p>
 * <pre><code>
 * vJrwpWtwJgWrhcsFMMfFFhFp
 * jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
 * PmmdzqPrVvPwwTWBwg
 * </code></pre>
 * <p>And the second group's rucksacks are the next three lines:</p>
 * <pre><code>
 * wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
 * ttgJtRGJQctTZtZT
 * CrZsJsPPZsGzwwsLwLmpwMDw
 * </code></pre>
 * <p>In the first group, the only item type that appears in all three rucksacks is lowercase <code>r</code>; this must be their badges. In the second group, their badge
 * item type must be <code>Z</code>.</p>
 * <p>Priorities for these items must still be found to organize the sticker attachment efforts: here, they are 18 (<code>r</code>) for the first group and 52
 * (<code>Z</code>) for the second group. The sum of these is <code><em>70</em></code>.</p>
 * <p>Find the item type that corresponds to the badges of each three-Elf group. <em>What is the sum of the priorities of those item types?</em></p>
 */
public class Main implements Runnable {
    @Override
    public void run() {
        final List<String> input;
        try {
            input = Util.readStringInput(getClass(), "input.txt");
            int result = 0;
            for (int i = 0; i < input.size(); i++) {
                final long first = mapToLong(input.get(i++));
                final long second = mapToLong(input.get(i++));
                final long third = mapToLong(input.get(i));
                final long commonItem = first & second & third;
                long temp = commonItem;
                char badge = 'A';
                for (long j = 0; j < 63; j++, temp >>= 1) {
                    if (temp == 1) {
                        break;
                    }
                    badge++;
                }
                if (Character.isUpperCase(badge)) {
                    result += (badge - 'A' + 27);
                } else {
                    result += (badge - 'a' + 1);
                }
            }
            System.out.println(result);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static long mapToLong(String s) {
        long num = 0;
        for (int i = 0; i < s.length(); i++) {
            final char c = s.charAt(i);
            final int codePoint = c - 'A';
            num |= (1L << codePoint);
        }
        return num;
    }
}
