package git.doomshade.aoc._51;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2>--- Day 5: Supply Stacks ---</h2><p>The expedition can depart as soon as the final supplies have been unloaded from the ships. Supplies are stored in stacks of
 * marked <em>crates</em>, but because the needed supplies are buried under many other crates, the crates need to be rearranged.</p>
 * <p>The ship has a <em>giant cargo crane</em> capable of moving crates between stacks. To ensure none of the crates get crushed or fall over, the crane operator will
 * rearrange them in a series of carefully-planned steps. After the crates are rearranged, the desired crates will be at the top of each stack.</p>
 * <p>The Elves don't want to interrupt the crane operator during this delicate procedure, but they forgot to ask her <em>which</em> crate will end up where, and they want
 * to be ready to unload them as soon as possible so they can embark.</p>
 * <p>They do, however, have a drawing of the starting stacks of crates <em>and</em> the rearrangement procedure (your puzzle input). For example:</p>
 * <pre><code>
 *     [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 *
 * move 1 from 2 to 1
 * move 3 from 1 to 3
 * move 2 from 2 to 1
 * move 1 from 1 to 2
 * </code></pre>
 * <p>In this example, there are three stacks of crates. Stack 1 contains two crates: crate <code>Z</code> is on the bottom, and crate <code>N</code> is on top. Stack 2
 * contains three crates; from bottom to top, they are crates <code>M</code>, <code>C</code>, and <code>D</code>. Finally, stack 3 contains a single crate,
 * <code>P</code>.</p>
 * <p>Then, the rearrangement procedure is given. In each step of the procedure, a quantity of crates is moved from one stack to a different stack. In the first step of the
 * above rearrangement procedure, one crate is moved from stack 2 to stack 1, resulting in this configuration:</p>
 * <pre><code>
 * [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 * </code></pre>
 * <p>In the second step, three crates are moved from stack 1 to stack 3. Crates are moved <em>one at a time</em>, so the first crate to be moved (<code>D</code>) ends up
 * below the second and third crates:</p>
 * <pre><code>
 *         [Z]
 *         [N]
 *     [C] [D]
 *     [M] [P]
 *  1   2   3
 * </code></pre>
 * <p>Then, both crates are moved from stack 2 to stack 1. Again, because crates are moved <em>one at a time</em>, crate <code>C</code> ends up below crate
 * <code>M</code>:</p>
 * <pre><code>
 *         [Z]
 *         [N]
 * [M]     [D]
 * [C]     [P]
 *  1   2   3
 * </code></pre>
 * <p>Finally, one crate is moved from stack 1 to stack 2:</p>
 * <pre><code>
 *         [<em>Z</em>]
 *         [N]
 *         [D]
 * [<em>C</em>] [<em>M</em>] [P]
 *  1   2   3
 * </code></pre>
 * <p>The Elves just need to know <em>which crate will end up on top of each stack</em>; in this example, the top crates are <code>C</code> in stack 1, <code>M</code> in
 * stack 2, and <code>Z</code> in stack 3, so you should combine these together and give the Elves the message <code><em>CMZ</em></code>.</p>
 * <p><em>After the rearrangement procedure completes, what crate ends up on top of each stack?</em></p>
 */
public class Main implements Runnable {
    private static final Pattern PATTERN = Pattern.compile("move (\\d+) from (\\d+) to (\\d+)");

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            final List<Stack<Character>> allCrates = new ArrayList<>();
            int instructionIndex = -1;
            for (int i = 0; i < input.size(); i++) {
                final String s = input.get(i);
                if (s.isEmpty() || s.contains("1")) {
                    instructionIndex = i;
                    break;
                }
                for (int j = 0; j < s.length(); j += 4) {
                    final char c = s.charAt(j + 1);
                    if (Character.isSpaceChar(c)) {
                        continue;
                    }
                    final int idx = j / 4;
                    while (allCrates.size() <= idx) {
                        allCrates.add(new Stack<>());
                    }
                    final Stack<Character> stack = allCrates.get(idx);
                    stack.push(c);
                }
            }

            for (int i = 0; i < allCrates.size(); i++) {
                Collections.reverse(allCrates.get(i));
            }

            for (int i = instructionIndex + 2; i < input.size(); i++) {
                final String s = input.get(i);
                final Matcher matcher = PATTERN.matcher(s);
                if (!matcher.find()) {
                    throw new IllegalStateException();
                }
                final int count = Integer.parseInt(matcher.group(1));
                final int from = Integer.parseInt(matcher.group(2));
                final int to = Integer.parseInt(matcher.group(3));
//                System.out.printf("move %d from %d to %d%n", count, from, to);

                final Stack<Character> crateFrom = allCrates.get(from - 1);
                final Stack<Character> crateTo = allCrates.get(to - 1);
                for (int j = 0; j < count; j++) {
                    final Character pop = crateFrom.pop();
//                    System.out.printf("Moved %c%n", pop);
                    crateTo.push(pop);
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < allCrates.size(); i++) {
                sb.append(allCrates.get(i).pop());
            }
            System.out.println(sb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
