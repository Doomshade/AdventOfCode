package git.doomshade.aoc._112;

import git.doomshade.aoc.shared.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>You're worried you might not ever get your items back. So worried, in fact, that your relief that a monkey's inspection didn't
 * damage an item <em>no longer causes your worry level to be divided by three</em>.</p>
 * <p>Unfortunately, that relief was all that was keeping your worry levels from reaching <em>ridiculous levels</em>. You'll need to <em>find another way to keep your worry
 * levels manageable</em>.</p>
 * <p>At this rate, you might be putting up with these monkeys for a <em>very long time</em> - possibly <em><code>10000</code> rounds</em>!</p>
 * <p>With these new rules, you can still figure out the <span title="Monkey business monkey business monkey business, monkey numbers... is this working?">monkey
 * business</span> after 10000 rounds. Using the same example above:</p>
 * <pre><code>
 * == After round 1 ==
 * Monkey 0 inspected items 2 times.
 * Monkey 1 inspected items 4 times.
 * Monkey 2 inspected items 3 times.
 * Monkey 3 inspected items 6 times.
 *
 * == After round 20 ==
 * Monkey 0 inspected items 99 times.
 * Monkey 1 inspected items 97 times.
 * Monkey 2 inspected items 8 times.
 * Monkey 3 inspected items 103 times.
 *
 * == After round 1000 ==
 * Monkey 0 inspected items 5204 times.
 * Monkey 1 inspected items 4792 times.
 * Monkey 2 inspected items 199 times.
 * Monkey 3 inspected items 5192 times.
 *
 * == After round 2000 ==
 * Monkey 0 inspected items 10419 times.
 * Monkey 1 inspected items 9577 times.
 * Monkey 2 inspected items 392 times.
 * Monkey 3 inspected items 10391 times.
 *
 * == After round 3000 ==
 * Monkey 0 inspected items 15638 times.
 * Monkey 1 inspected items 14358 times.
 * Monkey 2 inspected items 587 times.
 * Monkey 3 inspected items 15593 times.
 *
 * == After round 4000 ==
 * Monkey 0 inspected items 20858 times.
 * Monkey 1 inspected items 19138 times.
 * Monkey 2 inspected items 780 times.
 * Monkey 3 inspected items 20797 times.
 *
 * == After round 5000 ==
 * Monkey 0 inspected items 26075 times.
 * Monkey 1 inspected items 23921 times.
 * Monkey 2 inspected items 974 times.
 * Monkey 3 inspected items 26000 times.
 *
 * == After round 6000 ==
 * Monkey 0 inspected items 31294 times.
 * Monkey 1 inspected items 28702 times.
 * Monkey 2 inspected items 1165 times.
 * Monkey 3 inspected items 31204 times.
 *
 * == After round 7000 ==
 * Monkey 0 inspected items 36508 times.
 * Monkey 1 inspected items 33488 times.
 * Monkey 2 inspected items 1360 times.
 * Monkey 3 inspected items 36400 times.
 *
 * == After round 8000 ==
 * Monkey 0 inspected items 41728 times.
 * Monkey 1 inspected items 38268 times.
 * Monkey 2 inspected items 1553 times.
 * Monkey 3 inspected items 41606 times.
 *
 * == After round 9000 ==
 * Monkey 0 inspected items 46945 times.
 * Monkey 1 inspected items 43051 times.
 * Monkey 2 inspected items 1746 times.
 * Monkey 3 inspected items 46807 times.
 *
 * == After round 10000 ==
 * <em>Monkey 0 inspected items 52166 times.</em>
 * Monkey 1 inspected items 47830 times.
 * Monkey 2 inspected items 1938 times.
 * <em>Monkey 3 inspected items 52013 times.</em>
 * </code></pre>
 * <p>After 10000 rounds, the two most active monkeys inspected items 52166 and 52013 times. Multiplying these together, the level of <em>monkey business</em> in this
 * situation is now <code><em>2713310158</em></code>.</p>
 * <p>Worry levels are no longer divided by three after each item is inspected; you'll need to find another way to keep your worry levels manageable. Starting again from
 * the initial state in your puzzle input, <em>what is the level of monkey business after 10000 rounds?</em></p>
 */
public class Main implements Runnable {
    private static final String MSG_DIVISIBLE_FORMAT = "\t\tCurrent worry level is %sdivisible by %d.%n";

    private static class Monkey {
        /**
         * First value is old. Second value is a number or old.
         */
        private final UnaryOperator<BigInteger> worryOperation;
        private final Function<BigInteger, Integer> worryTest;
        private int inspectedItemCount = 0;
        private final Deque<BigInteger> items = new LinkedList<>();

        private Monkey(final Collection<BigInteger> startingItems, final UnaryOperator<BigInteger> worryOperation, final Function<BigInteger, Integer> worryTest) {
            this.worryOperation = worryOperation;
            this.worryTest = worryTest;
            this.items.addAll(startingItems);
        }

        public void takeTurn(List<Monkey> monkeys) {
            while (!items.isEmpty()) {
                // inspect item
                final BigInteger itemWorryLevel = inspectItem();

                // apply worry function
                BigInteger worryLevel = increaseWorryLevel(itemWorryLevel);

                worryLevel = decreaseWorryLevel(worryLevel);

                final int monkeyId = decideMonkeyTarget(worryLevel);
                monkeys.get(monkeyId)
                       .receiveItem(worryLevel);
            }
        }

        private int decideMonkeyTarget(final BigInteger worryLevel) {
            return worryTest.apply(worryLevel);
        }

        private BigInteger inspectItem() {
            inspectedItemCount++;
            final BigInteger worryLevel = items.pop();
//            System.out.printf("Monkey inspects an item with a worry level of %d.%n", worryLevel.intValue());
            return worryLevel;
        }

        private BigInteger decreaseWorryLevel(BigInteger worryLevel) {
            BigInteger newWorryLevel = worryLevel.mod(modulo);
//            System.out.printf("Monkey gets bored with item. Worry level is divided by 3 to %d.%n", newWorryLevel);
            return newWorryLevel;
        }

        private BigInteger increaseWorryLevel(BigInteger itemWorryLevel) {
            final BigInteger apply = worryOperation.apply(itemWorryLevel);
//            System.out.printf("Worry level is increased to %d.%n", apply);
            return apply;
        }

        public void receiveItem(BigInteger item) {
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
        final Collection<BigInteger> items = parseItems(br);
        final UnaryOperator<BigInteger> worryOperation = parseWorryOperation(br);
        final Function<BigInteger, Integer> worryTest = parseWorryTest(br);
        final String blankLine = br.readLine();
        return Optional.of(new Monkey(items, worryOperation, worryTest));
    }

    private static BigInteger modulo = BigInteger.ONE;

    private Function<BigInteger, Integer> parseWorryTest(final BufferedReader br) throws IOException {
        Matcher m = createMatcher(br, TEST_PATTERN);
        final BigInteger divisibleBy = new BigInteger(m.group(1));
        modulo = modulo.multiply(divisibleBy);

        m = createMatcher(br, TRUE_TEST_PATTERN);
        final int monkeyIfTrue = Integer.parseInt(m.group(1));

        m = createMatcher(br, FALSE_TEST_PATTERN);
        final int monkeyIfFalse = Integer.parseInt(m.group(1));
        return number -> {
            if (number.mod(divisibleBy)
                      .compareTo(BigInteger.ZERO) == 0) {
                return monkeyIfTrue;
            }
            return monkeyIfFalse;
        };
    }

    private static Collection<BigInteger> parseItems(final BufferedReader br) throws IOException {
        final Matcher m = createMatcher(br, ITEMS_PATTERN);
        return Arrays.stream(m.group(1)
                              .split(", "))
                     .map(BigInteger::new)
                     .toList();
    }

    private static UnaryOperator<BigInteger> parseWorryOperation(final BufferedReader br) throws IOException {
        final Matcher m = createMatcher(br, OPERATION_PATTERN);
        final char operator = m.group(1)
                               .charAt(0);
        final String operand = m.group(2);
        final UnaryOperator<BigInteger> unaryOperation;
        if (operand.equals("old")) {
            unaryOperation = switch (operator) {
                case '*' -> old -> old.multiply(old);
                case '+' -> old -> old.add(old);
                case '-' -> old -> BigInteger.ZERO;
                case '/' -> old -> BigInteger.ONE;
                default -> throw new IllegalStateException();
            };
        } else {
            final BigInteger numOperand = new BigInteger(operand);
            unaryOperation = switch (operator) {
                case '*' -> old -> old.multiply(numOperand);
                case '+' -> old -> old.add(numOperand);
                case '-' -> old -> old.subtract(numOperand);
                case '/' -> old -> old.divide(numOperand);
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

            for (int i = 0; i < 10_000; i++) {
                for (int j = 0; j < monkeys.size(); j++) {
                    final Monkey monkey = monkeys.get(j);
//                    System.out.printf("Monkey %d:%n", j);
                    monkey.takeTurn(monkeys);
                }

            }
            monkeys.sort(Comparator.comparingInt(a -> -a.inspectedItemCount));
            for (int j = 0; j < monkeys.size(); j++) {
                final Monkey monkey = monkeys.get(j);
                System.out.printf("Monkey %d inspected items %d times%n", j, monkey.inspectedItemCount);
            }
            final long a = monkeys.get(0).inspectedItemCount;
            final long b = monkeys.get(1).inspectedItemCount;
            System.out.printf("%d * %d = %d", a, b, a * b);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
