package git.doomshade.aoc._111;

import git.doomshade.aoc.shared.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.IntUnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2>--- Day 11: Monkey in the Middle ---</h2><p>As you finally start making your way upriver, you realize your pack is much lighter than you remember. Just then, one of
 * the items from your pack goes flying overhead. Monkeys are playing <a href="https://en.wikipedia.org/wiki/Keep_away" target="_blank">Keep Away</a> with your missing
 * things!</p>
 * <p>To get your stuff back, you need to be able to predict where the monkeys will throw your items. After some careful observation, you realize the monkeys operate based
 * on <em>how worried you are about each item</em>.</p>
 * <p>You take some notes (your puzzle input) on the items each monkey currently has, how worried you are about those items, and how the monkey makes decisions based on
 * your worry level. For example:</p>
 * <pre><code>
 * Monkey 0:
 *   Starting items: 79, 98
 *   Operation: new = old * 19
 *   Test: divisible by 23
 *     If true: throw to monkey 2
 *     If false: throw to monkey 3
 *
 * Monkey 1:
 *   Starting items: 54, 65, 75, 74
 *   Operation: new = old + 6
 *   Test: divisible by 19
 *     If true: throw to monkey 2
 *     If false: throw to monkey 0
 *
 * Monkey 2:
 *   Starting items: 79, 60, 97
 *   Operation: new = old * old
 *   Test: divisible by 13
 *     If true: throw to monkey 1
 *     If false: throw to monkey 3
 *
 * Monkey 3:
 *   Starting items: 74
 *   Operation: new = old + 3
 *   Test: divisible by 17
 *     If true: throw to monkey 0
 *     If false: throw to monkey 1
 * </code></pre>
 * <p>Each monkey has several attributes:</p>
 * <ul>
 * <li><code>Starting items</code> lists your <em>worry level</em> for each item the monkey is currently holding in the order they will be inspected.</li>
 * <li><code>Operation</code> shows how your worry level changes as that monkey inspects an item. (An operation like <code>new = old * 5</code> means that your worry level
 * after the monkey inspected the item is five times whatever your worry level was before inspection.)</li>
 * <li><code>Test</code> shows how the monkey uses your worry level to decide where to throw an item next.
 *   <ul>
 *   <li><code>If true</code> shows what happens with an item if the <code>Test</code> was true.</li>
 *   <li><code>If false</code> shows what happens with an item if the <code>Test</code> was false.</li>
 *   </ul>
 * </li>
 * </ul>
 * <p>After each monkey inspects an item but before it tests your worry level, your relief that the monkey's inspection didn't damage the item causes your worry level to be
 * <em>divided by three</em> and rounded down to the nearest integer.</p>
 * <p>The monkeys take turns inspecting and throwing items. On a single monkey's <em>turn</em>, it inspects and throws all of the items it is holding one at a time and in
 * the order listed. Monkey <code>0</code> goes first, then monkey <code>1</code>, and so on until each monkey has had one turn. The process of each monkey taking a single
 * turn is called a <em>round</em>.</p>
 * <p>When a monkey throws an item to another monkey, the item goes on the <em>end</em> of the recipient monkey's list. A monkey that starts a round with no items could end
 * up inspecting and throwing many items by the time its turn comes around. If a monkey is holding no items at the start of its turn, its turn ends.</p>
 * <p>In the above example, the first round proceeds as follows:</p>
 * <pre><code>
 * Monkey 0:
 *   Monkey inspects an item with a worry level of 79.
 *     Worry level is multiplied by 19 to 1501.
 *     Monkey gets bored with item. Worry level is divided by 3 to 500.
 *     Current worry level is not divisible by 23.
 *     Item with worry level 500 is thrown to monkey 3.
 *   Monkey inspects an item with a worry level of 98.
 *     Worry level is multiplied by 19 to 1862.
 *     Monkey gets bored with item. Worry level is divided by 3 to 620.
 *     Current worry level is not divisible by 23.
 *     Item with worry level 620 is thrown to monkey 3.
 * Monkey 1:
 *   Monkey inspects an item with a worry level of 54.
 *     Worry level increases by 6 to 60.
 *     Monkey gets bored with item. Worry level is divided by 3 to 20.
 *     Current worry level is not divisible by 19.
 *     Item with worry level 20 is thrown to monkey 0.
 *   Monkey inspects an item with a worry level of 65.
 *     Worry level increases by 6 to 71.
 *     Monkey gets bored with item. Worry level is divided by 3 to 23.
 *     Current worry level is not divisible by 19.
 *     Item with worry level 23 is thrown to monkey 0.
 *   Monkey inspects an item with a worry level of 75.
 *     Worry level increases by 6 to 81.
 *     Monkey gets bored with item. Worry level is divided by 3 to 27.
 *     Current worry level is not divisible by 19.
 *     Item with worry level 27 is thrown to monkey 0.
 *   Monkey inspects an item with a worry level of 74.
 *     Worry level increases by 6 to 80.
 *     Monkey gets bored with item. Worry level is divided by 3 to 26.
 *     Current worry level is not divisible by 19.
 *     Item with worry level 26 is thrown to monkey 0.
 * Monkey 2:
 *   Monkey inspects an item with a worry level of 79.
 *     Worry level is multiplied by itself to 6241.
 *     Monkey gets bored with item. Worry level is divided by 3 to 2080.
 *     Current worry level is divisible by 13.
 *     Item with worry level 2080 is thrown to monkey 1.
 *   Monkey inspects an item with a worry level of 60.
 *     Worry level is multiplied by itself to 3600.
 *     Monkey gets bored with item. Worry level is divided by 3 to 1200.
 *     Current worry level is not divisible by 13.
 *     Item with worry level 1200 is thrown to monkey 3.
 *   Monkey inspects an item with a worry level of 97.
 *     Worry level is multiplied by itself to 9409.
 *     Monkey gets bored with item. Worry level is divided by 3 to 3136.
 *     Current worry level is not divisible by 13.
 *     Item with worry level 3136 is thrown to monkey 3.
 * Monkey 3:
 *   Monkey inspects an item with a worry level of 74.
 *     Worry level increases by 3 to 77.
 *     Monkey gets bored with item. Worry level is divided by 3 to 25.
 *     Current worry level is not divisible by 17.
 *     Item with worry level 25 is thrown to monkey 1.
 *   Monkey inspects an item with a worry level of 500.
 *     Worry level increases by 3 to 503.
 *     Monkey gets bored with item. Worry level is divided by 3 to 167.
 *     Current worry level is not divisible by 17.
 *     Item with worry level 167 is thrown to monkey 1.
 *   Monkey inspects an item with a worry level of 620.
 *     Worry level increases by 3 to 623.
 *     Monkey gets bored with item. Worry level is divided by 3 to 207.
 *     Current worry level is not divisible by 17.
 *     Item with worry level 207 is thrown to monkey 1.
 *   Monkey inspects an item with a worry level of 1200.
 *     Worry level increases by 3 to 1203.
 *     Monkey gets bored with item. Worry level is divided by 3 to 401.
 *     Current worry level is not divisible by 17.
 *     Item with worry level 401 is thrown to monkey 1.
 *   Monkey inspects an item with a worry level of 3136.
 *     Worry level increases by 3 to 3139.
 *     Monkey gets bored with item. Worry level is divided by 3 to 1046.
 *     Current worry level is not divisible by 17.
 *     Item with worry level 1046 is thrown to monkey 1.
 * </code></pre>
 * <p>After round 1, the monkeys are holding items with these worry levels:</p>
 * <pre><code>
 * Monkey 0: 20, 23, 27, 26
 * Monkey 1: 2080, 25, 167, 207, 401, 1046
 * Monkey 2:
 * Monkey 3:
 * </code></pre>
 * <p>Monkeys 2 and 3 aren't holding any items at the end of the round; they both inspected items during the round and threw them all before the round ended.</p>
 * <p>This process continues for a few more rounds:</p>
 * <pre><code>
 * After round 2, the monkeys are holding items with these worry levels:
 * Monkey 0: 695, 10, 71, 135, 350
 * Monkey 1: 43, 49, 58, 55, 362
 * Monkey 2:
 * Monkey 3:
 *
 * After round 3, the monkeys are holding items with these worry levels:
 * Monkey 0: 16, 18, 21, 20, 122
 * Monkey 1: 1468, 22, 150, 286, 739
 * Monkey 2:
 * Monkey 3:
 *
 * After round 4, the monkeys are holding items with these worry levels:
 * Monkey 0: 491, 9, 52, 97, 248, 34
 * Monkey 1: 39, 45, 43, 258
 * Monkey 2:
 * Monkey 3:
 *
 * After round 5, the monkeys are holding items with these worry levels:
 * Monkey 0: 15, 17, 16, 88, 1037
 * Monkey 1: 20, 110, 205, 524, 72
 * Monkey 2:
 * Monkey 3:
 *
 * After round 6, the monkeys are holding items with these worry levels:
 * Monkey 0: 8, 70, 176, 26, 34
 * Monkey 1: 481, 32, 36, 186, 2190
 * Monkey 2:
 * Monkey 3:
 *
 * After round 7, the monkeys are holding items with these worry levels:
 * Monkey 0: 162, 12, 14, 64, 732, 17
 * Monkey 1: 148, 372, 55, 72
 * Monkey 2:
 * Monkey 3:
 *
 * After round 8, the monkeys are holding items with these worry levels:
 * Monkey 0: 51, 126, 20, 26, 136
 * Monkey 1: 343, 26, 30, 1546, 36
 * Monkey 2:
 * Monkey 3:
 *
 * After round 9, the monkeys are holding items with these worry levels:
 * Monkey 0: 116, 10, 12, 517, 14
 * Monkey 1: 108, 267, 43, 55, 288
 * Monkey 2:
 * Monkey 3:
 *
 * After round 10, the monkeys are holding items with these worry levels:
 * Monkey 0: 91, 16, 20, 98
 * Monkey 1: 481, 245, 22, 26, 1092, 30
 * Monkey 2:
 * Monkey 3:
 *
 * ...
 *
 * After round 15, the monkeys are holding items with these worry levels:
 * Monkey 0: 83, 44, 8, 184, 9, 20, 26, 102
 * Monkey 1: 110, 36
 * Monkey 2:
 * Monkey 3:
 *
 * ...
 *
 * After round 20, the monkeys are holding items with these worry levels:
 * Monkey 0: 10, 12, 14, 26, 34
 * Monkey 1: 245, 93, 53, 199, 115
 * Monkey 2:
 * Monkey 3:
 * </code></pre>
 * <p>Chasing all of the monkeys at once is impossible; you're going to have to focus on the <em>two most active</em> monkeys if you want any hope of getting your stuff
 * back. Count the <em>total number of times each monkey inspects items</em> over 20 rounds:</p>
 * <pre><code>
 * <em>Monkey 0 inspected items 101 times.</em>
 * Monkey 1 inspected items 95 times.
 * Monkey 2 inspected items 7 times.
 * <em>Monkey 3 inspected items 105 times.</em>
 * </code></pre>
 * <p>In this example, the two most active monkeys inspected items 101 and 105 times. The level of <em>monkey business</em> in this situation can be found by multiplying
 * these together: <code><em>10605</em></code>.</p>
 * <p>Figure out which monkeys to chase by counting how many items they inspect over 20 rounds. <em>What is the level of monkey business after 20 rounds of stuff-slinging
 * simian shenanigans?</em></p>
 */
public class Main implements Runnable {
    private static final String MSG_DIVISIBLE_FORMAT = "\t\tCurrent worry level is %sdivisible by %d.%n";

    private static class Monkey {
        /**
         * First value is old. Second value is a number or old.
         */
        private final IntUnaryOperator worryOperation;
        private final IntUnaryOperator worryTest;
        private int inspectedItemCount = 0;
        private final Deque<Integer> items = new LinkedList<>();

        private Monkey(final Collection<Integer> startingItems, final IntUnaryOperator worryOperation, final IntUnaryOperator worryTest) {
            this.worryOperation = worryOperation;
            this.worryTest = worryTest;
            this.items.addAll(startingItems);
        }

        public void takeTurn(List<Monkey> monkeys) {
            while (!items.isEmpty()) {
                // inspect item
                final int itemWorryLevel = inspectItem();
                System.out.printf("\tMonkey inspects an item with worry level of %d.%n", itemWorryLevel);

                // apply worry function
                int worryLevel = increaseWorryLevel(itemWorryLevel);
                System.out.printf("\t\tWorry level is increased to %d.%n", worryLevel);

                // divide worry by 3
                worryLevel = decreaseWorryLevel(worryLevel);
                System.out.printf("\t\tMonkey gets bored with item. Worry level is divided by 3 to %d.%n", worryLevel);

                final int monkeyId = worryTest.applyAsInt(worryLevel);
                System.out.printf("\t\tItem with worry level %d is thrown to monkey %d.%n", worryLevel, monkeyId);
                monkeys.get(monkeyId)
                       .receiveItem(worryLevel);
            }
        }

        private int inspectItem() {
            inspectedItemCount++;
            return items.pop();
        }

        private int increaseWorryLevel(int itemWorryLevel) {
            return worryOperation.applyAsInt(itemWorryLevel);
        }

        private int decreaseWorryLevel(int itemWorryLevel) {
            return itemWorryLevel / 3;
        }

        public void receiveItem(int item) {
            items.addLast(item);
        }
    }

    private static final Pattern ITEMS_PATTERN = Pattern.compile("Starting items: (.+)");
    private static final Pattern OPERATION_PATTERN = Pattern.compile("Operation: new = old (.) (.+)");
    private static final Pattern TEST_PATTERN = Pattern.compile("Test: divisible by (\\d+)");
    private static final Pattern TRUE_TEST_PATTERN = Pattern.compile("If true: throw to monkey (\\d+)");
    private static final Pattern FALSE_TEST_PATTERN = Pattern.compile("If false: throw to monkey (\\d+)");

    private Optional<Monkey> parseMonkey(BufferedReader br) throws IOException {
        final String firstLine = br.readLine();
        if (firstLine == null) {
            return Optional.empty();
        }
        final Collection<Integer> items = parseItems(br);
        final IntUnaryOperator worryOperation = parseWorryOperation(br);
        final IntUnaryOperator worryTest = parseWorryTest(br);
        final String blankLine = br.readLine();
        return Optional.of(new Monkey(items, worryOperation, worryTest));
    }

    private IntUnaryOperator parseWorryTest(final BufferedReader br) throws IOException {
        Matcher m = createMatcher(br, TEST_PATTERN);
        final int divisibleBy = Integer.parseInt(m.group(1));

        m = createMatcher(br, TRUE_TEST_PATTERN);
        final int monkeyIfTrue = Integer.parseInt(m.group(1));

        m = createMatcher(br, FALSE_TEST_PATTERN);
        final int monkeyIfFalse = Integer.parseInt(m.group(1));
        return number -> {
            if (number % divisibleBy == 0) {
                System.out.printf(MSG_DIVISIBLE_FORMAT, "", divisibleBy);
                return monkeyIfTrue;
            }
            System.out.printf(MSG_DIVISIBLE_FORMAT, "not ", divisibleBy);
            return monkeyIfFalse;
        };
    }

    private static Collection<Integer> parseItems(final BufferedReader br) throws IOException {
        final Matcher m = createMatcher(br, ITEMS_PATTERN);
        return Arrays.stream(m.group(1)
                              .split(", "))
                     .map(Integer::parseInt)
                     .toList();
    }

    private static IntUnaryOperator parseWorryOperation(final BufferedReader br) throws IOException {
        final Matcher m = createMatcher(br, OPERATION_PATTERN);
        final char operator = m.group(1)
                               .charAt(0);
        final String operand = m.group(2);
        final IntUnaryOperator unaryOperation;
        if (operand.equals("old")) {
            unaryOperation = switch (operator) {
                case '*' -> old -> old * old;
                case '+' -> old -> old + old;
                case '-' -> old -> 0;
                case '/' -> old -> 1;
                default -> throw new IllegalStateException();
            };
        } else {
            final int numOperand = Integer.parseInt(operand);
            unaryOperation = switch (operator) {
                case '*' -> old -> old * numOperand;
                case '+' -> old -> old + numOperand;
                case '-' -> old -> old - numOperand;
                case '/' -> old -> old / numOperand;
                default -> throw new IllegalStateException();
            };
        }
        return unaryOperation;
    }

    private static Matcher createMatcher(final BufferedReader br, final Pattern pattern) throws IOException {
        final String input = br.readLine();
        final Matcher m = pattern.matcher(input);
        if (!m.find()) {
            throw new IllegalStateException();
        }
        return m;
    }

    @Override
    public void run() {
        try {
            final String input = String.join("\n", Util.readStringInput(getClass(), "input.txt"));
            final BufferedReader reader = new BufferedReader(new StringReader(input));
            final List<Monkey> monkeys = new ArrayList<>();

            while (true) {
                final Optional<Monkey> monkey = parseMonkey(reader);
                if (monkey.isPresent()) {
                    monkeys.add(monkey.get());
                } else {
                    break;
                }
            }

            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < monkeys.size(); j++) {
                    final Monkey monkey = monkeys.get(j);
                    System.out.printf("Monkey %d:%n", j);
                    monkey.takeTurn(monkeys);
                }
            }
            for (int j = 0; j < monkeys.size(); j++) {
                final Monkey monkey = monkeys.get(j);
                System.out.printf("Monkey %d: %s%n", j, monkey.items);
            }
            for (int j = 0; j < monkeys.size(); j++) {
                final Monkey monkey = monkeys.get(j);
                System.out.printf("Monkey %d inspected items %d times%n", j, monkey.inspectedItemCount);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
