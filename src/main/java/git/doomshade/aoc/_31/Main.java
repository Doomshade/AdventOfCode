package git.doomshade.aoc._31;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.List;

/**
 * <h2>--- Day 3: Rucksack Reorganization ---</h2><p>One Elf has the important job of loading all of the
 * <a href="https://en.wikipedia.org/wiki/Rucksack" target="_blank">rucksacks</a>
 * with supplies for the <span title="Where there's jungle, there's hijinxs.">jungle</span> journey. Unfortunately, that Elf didn't quite follow the packing instructions, and
 * so a few items now need to be rearranged.</p>
 * <p>Each rucksack has two large <em>compartments</em>. All items of a given type are meant to go into exactly one of the two compartments. The Elf that did the packing
 * failed to follow this rule for exactly one item type per rucksack.</p>
 * <p>The Elves have made a list of all of the items currently in each rucksack (your puzzle input), but they need your help finding the errors. Every item type is
 * identified by a single lowercase or uppercase letter (that is, <code>a</code> and <code>A</code> refer to different types of items).</p>
 * <p>The list of items for each rucksack is given as characters all on a single line. A given rucksack always has the same number of items in each of its two compartments,
 * so the first half of the characters represent items in the first compartment, while the second half of the characters represent items in the second compartment.</p>
 * <p>For example, suppose you have the following list of contents from six rucksacks:</p>
 * <pre><code>
 * vJrwpWtwJgWrhcsFMMfFFhFp
 * jqHRNqRjqzjGDLGLrsFMfFZSrLrFZsSL
 * PmmdzqPrVvPwwTWBwg
 * wMqvLMZHhHMvwLHjbvcjnnSBnvTQFn
 * ttgJtRGJQctTZtZT
 * CrZsJsPPZsGzwwsLwLmpwMDw
 * </code></pre>
 * <ul>
 * <li>The first rucksack contains the items <code>vJrwpWtwJgWrhcsFMMfFFhFp</code>, which means its first compartment contains the items <code>vJrwpWtwJgWr</code>, while
 * the second compartment contains the items <code>hcsFMMfFFhFp</code>. The only item type that appears in both compartments is lowercase <code><em>p</em></code>.</li>
 * <li>The second rucksack's compartments contain <code>jqHRNqRjqzjGDLGL</code> and <code>rsFMfFZSrLrFZsSL</code>. The only item type that appears in both compartments is
 * uppercase <code><em>L</em></code>.</li>
 * <li>The third rucksack's compartments contain <code>PmmdzqPrV</code> and <code>vPwwTWBwg</code>; the only common item type is uppercase <code><em>P</em></code>.</li>
 * <li>The fourth rucksack's compartments only share item type <code><em>v</em></code>.</li>
 * <li>The fifth rucksack's compartments only share item type <code><em>t</em></code>.</li>
 * <li>The sixth rucksack's compartments only share item type <code><em>s</em></code>.</li>
 * </ul>
 * <p>To help prioritize item rearrangement, every item type can be converted to a <em>priority</em>:</p>
 * <ul>
 * <li>Lowercase item types <code>a</code> through <code>z</code> have priorities 1 through 26.</li>
 * <li>Uppercase item types <code>A</code> through <code>Z</code> have priorities 27 through 52.</li>
 * </ul>
 * <p>In the above example, the priority of the item type that appears in both compartments of each rucksack is 16 (<code>p</code>), 38 (<code>L</code>), 42
 * (<code>P</code>), 22 (<code>v</code>), 20 (<code>t</code>), and 19 (<code>s</code>); the sum of these is <code><em>157</em></code>.</p>
 * <p>Find the item type that appears in both compartments of each rucksack. <em>What is the sum of the priorities of those item types?</em></p>
 */
public class Main implements Runnable {
    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            int result = 0;
            for (String s : input) {
                long first = 0;
                final String firstCompartment = s.substring(0, s.length() / 2);
                final String secondCompartment = s.substring(s.length() / 2);
                for (int i = 0; i < firstCompartment.length(); i++) {
                    final char c = firstCompartment.charAt(i);
                    final int codePoint = c - 'A';
                    System.out.print(codePoint + " ");
                    first |= (1L << codePoint);
                }
                System.out.println();
                long temp = first;
                printBinary(temp);
                System.out.println();
                printAlphabetReverse();
                System.out.println(firstCompartment + " / " + secondCompartment);

                for (int i = 0; i < secondCompartment.length(); i++) {
                    final char c = secondCompartment.charAt(i);
                    final int codePoint = c - 'A';
                    if ((first & (1L << codePoint)) != 0) {
                        int itemTypePriority;
                        if (Character.isUpperCase(c)) {
                            itemTypePriority = c - 'A' + 27;
                        } else {
                            itemTypePriority = c - 'a' + 1;
                        }
                        result += itemTypePriority;
                        break;
                    }
                }
                System.out.println("\n----------------------------------------------------------------");
                System.out.println();
            }
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printAlphabetReverse() {
        System.out.print("000000");
        for (int i = 122; i >= 65; i--) {
            System.out.print((char) i);
        }
        System.out.println();
    }

    public static void printBinary(long temp) {
        for (int i = 0; i < 64; i++, temp <<= 1) {
            final long res = temp & (1L << 63);
            System.out.print(res == 0 ? "0" : "1");
        }
    }
}
