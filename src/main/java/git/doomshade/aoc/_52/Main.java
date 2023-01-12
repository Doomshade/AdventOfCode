package git.doomshade.aoc._52;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>As you watch the crane operator expertly rearrange the crates, you notice the process isn't following your prediction.</p>
 * <p>Some mud was covering the writing on the side of the crane, and you quickly wipe it away. The crane isn't a CrateMover 9000 - it's a <em><span title="It's way better
 * than the old CrateMover 1006.">CrateMover 9001</span></em>.</p>
 * <p>The CrateMover 9001 is notable for many new and exciting features: air conditioning, leather seats, an extra cup holder, and <em>the ability to pick up and move
 * multiple crates at once</em>.</p>
 * <p>Again considering the example above, the crates begin in the same configuration:</p>
 * <pre><code>
 *     [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 * </code></pre>
 * <p>Moving a single crate from stack 2 to stack 1 behaves the same as before:</p>
 * <pre><code>
 * [D]
 * [N] [C]
 * [Z] [M] [P]
 *  1   2   3
 * </code></pre>
 * <p>However, the action of moving three crates from stack 1 to stack 3 means that those three moved crates <em>stay in the same order</em>, resulting in this new
 * configuration:</p>
 * <pre><code>
 *         [D]
 *         [N]
 *     [C] [Z]
 *     [M] [P]
 *  1   2   3
 * </code></pre>
 * <p>Next, as both crates are moved from stack 2 to stack 1, they <em>retain their order</em> as well:</p>
 * <pre><code>
 *         [D]
 *         [N]
 * [C]     [Z]
 * [M]     [P]
 *  1   2   3
 * </code></pre>
 * <p>Finally, a single crate is still moved from stack 1 to stack 2, but now it's crate <code>C</code> that gets moved:</p>
 * <pre><code>
 *         [<em>D</em>]
 *         [N]
 *         [Z]
 * [<em>M</em>] [<em>C</em>] [P]
 *  1   2   3
 * </code></pre>
 * <p>In this example, the CrateMover 9001 has put the crates in a totally different order: <code><em>MCD</em></code>.</p>
 * <p>Before the rearrangement process finishes, update your simulation so that the Elves know where they should stand to be ready to unload the final supplies. <em>After
 * the rearrangement procedure completes, what crate ends up on top of each stack?</em></p>
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
                final Stack<Character> temp = new Stack<>();
                for (int j = 0; j < count; j++) {
                    temp.push(crateFrom.pop());
                }

                for (int j = 0; j < count; j++) {
                    crateTo.push(temp.pop());
                }
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < allCrates.size(); i++) {
                sb.append(allCrates.get(i)
                                   .pop());
            }
            System.out.println(sb);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
