package git.doomshade.aoc._42;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>It seems like there is still quite a bit of duplicate work planned. Instead, the Elves would <span title="If you like this, you'll
 * *love* axis-aligned bounding box intersection testing.">like</span> to know the number of pairs that <em>overlap at all</em>.</p>
 * <p>In the above example, the first two pairs (<code>2-4,6-8</code> and <code>2-3,4-5</code>) don't overlap, while the remaining four pairs (<code>5-7,7-9</code>,
 * <code>2-8,3-7</code>, <code>6-6,4-6</code>, and <code>2-6,4-8</code>) do overlap:</p>
 * <ul>
 * <li><code>5-7,7-9</code> overlaps in a single section, <code>7</code>.</li>
 * <li><code>2-8,3-7</code> overlaps all of the sections <code>3</code> through <code>7</code>.</li>
 * <li><code>6-6,4-6</code> overlaps in a single section, <code>6</code>.</li>
 * <li><code>2-6,4-8</code> overlaps in sections <code>4</code>, <code>5</code>, and <code>6</code>.</li>
 * </ul>
 * <p>So, in this example, the number of overlapping assignment pairs is <code><em>4</em></code>.</p>
 * <p><em>In how many assignment pairs do the ranges overlap?</em></p>
 */
public class Main implements Runnable {
    public static final Pattern PAIR_PATTERN = Pattern.compile("(?<A>(?<AL>\\d+)-(?<AU>\\d+)),(?<B>(?<BL>\\d+)-(?<BU>\\d+))");

    @Override
    public void run() {

        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            int result = 0;
            for (String s : input) {
                final Matcher matcher = PAIR_PATTERN.matcher(s);
                if (!matcher.find()) {
                    throw new IllegalStateException();
                }

                final int lowerA = Integer.parseInt(matcher.group("AL"));
                final int upperA = Integer.parseInt(matcher.group("AU"));
                final int lowerB = Integer.parseInt(matcher.group("BL"));
                final int upperB = Integer.parseInt(matcher.group("BU"));

                final boolean firstCase = lowerA <= lowerB && upperA >= upperB;
                final boolean secondCase = lowerB <= lowerA && upperB >= upperA;
                final boolean thirdCase = upperA >= lowerB && lowerA <= lowerB;
                final boolean fourthCase = upperB >= lowerA && lowerB <= lowerA;
                if (firstCase || secondCase || thirdCase || fourthCase) {
                    result++;
                }
            }
            System.out.println(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
