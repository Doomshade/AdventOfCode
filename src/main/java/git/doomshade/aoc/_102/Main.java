package git.doomshade.aoc._102;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>It seems like the <code>X</code> register controls the horizontal position of a
 * <a href="https://en.wikipedia.org/wiki/Sprite_(computer_graphics)" target="_blank">sprite</a>
 * . Specifically, the sprite is 3 pixels wide, and the <code>X</code> register sets the horizontal position of the <em>middle</em> of that sprite. (In this system, there is
 * no such thing as "vertical position": if the sprite's horizontal position puts its pixels where the CRT is currently drawing, then those pixels will be drawn.)</p>
 * <p>You count the pixels on the CRT: 40 wide and 6 high. This CRT screen draws the top row of pixels left-to-right, then the row below that, and so on. The left-most
 * pixel in each row is in position <code>0</code>, and the right-most pixel in each row is in position <code>39</code>.</p>
 * <p>Like the CPU, the CRT is tied closely to the clock circuit: the CRT draws <em>a single pixel during each cycle</em>. Representing each pixel of the screen as a
 * <code>#</code>, here are the cycles during which the first and last pixel in each row are drawn:</p>
 * <pre><code>
 * Cycle   1 -&gt; <em>#</em>######################################<em>#</em> &lt;- Cycle  40
 * Cycle  41 -&gt; <em>#</em>######################################<em>#</em> &lt;- Cycle  80
 * Cycle  81 -&gt; <em>#</em>######################################<em>#</em> &lt;- Cycle 120
 * Cycle 121 -&gt; <em>#</em>######################################<em>#</em> &lt;- Cycle 160
 * Cycle 161 -&gt; <em>#</em>######################################<em>#</em> &lt;- Cycle 200
 * Cycle 201 -&gt; <em>#</em>######################################<em>#</em> &lt;- Cycle 240
 * </code></pre>
 * <p>So, by <a href="https://en.wikipedia.org/wiki/Racing_the_Beam" target="_blank">carefully</a>
 * <a href="https://www.youtube.com/watch?v=sJFnWZH5FXc" target="_blank"><span title="While you're at it, go watch everything else by Retro Game Mechanics Explained,
 * too.">timing</span></a> the CPU instructions and the CRT drawing operations, you should be able to determine whether the sprite is visible the instant each pixel is drawn.
 * If the sprite is positioned such that one of its three pixels is the pixel currently being drawn, the screen produces a <em>lit</em> pixel (<code>#</code>); otherwise, the
 * screen leaves the pixel <em>dark</em> (<code>.</code>).
 * </p><p>The first few pixels from the larger example above are drawn as follows:</p>
 * <pre><code>
 * Sprite position: ###.....................................
 *
 * Start cycle   1: begin executing addx 15
 * During cycle  1: CRT draws pixel in position 0
 * Current CRT row: #
 *
 * During cycle  2: CRT draws pixel in position 1
 * Current CRT row: ##
 * End of cycle  2: finish executing addx 15 (Register X is now 16)
 * Sprite position: ...............###......................
 *
 * Start cycle   3: begin executing addx -11
 * During cycle  3: CRT draws pixel in position 2
 * Current CRT row: ##.
 *
 * During cycle  4: CRT draws pixel in position 3
 * Current CRT row: ##..
 * End of cycle  4: finish executing addx -11 (Register X is now 5)
 * Sprite position: ....###.................................
 *
 * Start cycle   5: begin executing addx 6
 * During cycle  5: CRT draws pixel in position 4
 * Current CRT row: ##..#
 *
 * During cycle  6: CRT draws pixel in position 5
 * Current CRT row: ##..##
 * End of cycle  6: finish executing addx 6 (Register X is now 11)
 * Sprite position: ..........###...........................
 *
 * Start cycle   7: begin executing addx -3
 * During cycle  7: CRT draws pixel in position 6
 * Current CRT row: ##..##.
 *
 * During cycle  8: CRT draws pixel in position 7
 * Current CRT row: ##..##..
 * End of cycle  8: finish executing addx -3 (Register X is now 8)
 * Sprite position: .......###..............................
 *
 * Start cycle   9: begin executing addx 5
 * During cycle  9: CRT draws pixel in position 8
 * Current CRT row: ##..##..#
 *
 * During cycle 10: CRT draws pixel in position 9
 * Current CRT row: ##..##..##
 * End of cycle 10: finish executing addx 5 (Register X is now 13)
 * Sprite position: ............###.........................
 *
 * Start cycle  11: begin executing addx -1
 * During cycle 11: CRT draws pixel in position 10
 * Current CRT row: ##..##..##.
 *
 * During cycle 12: CRT draws pixel in position 11
 * Current CRT row: ##..##..##..
 * End of cycle 12: finish executing addx -1 (Register X is now 12)
 * Sprite position: ...........###..........................
 *
 * Start cycle  13: begin executing addx -8
 * During cycle 13: CRT draws pixel in position 12
 * Current CRT row: ##..##..##..#
 *
 * During cycle 14: CRT draws pixel in position 13
 * Current CRT row: ##..##..##..##
 * End of cycle 14: finish executing addx -8 (Register X is now 4)
 * Sprite position: ...###..................................
 *
 * Start cycle  15: begin executing addx 13
 * During cycle 15: CRT draws pixel in position 14
 * Current CRT row: ##..##..##..##.
 *
 * During cycle 16: CRT draws pixel in position 15
 * Current CRT row: ##..##..##..##..
 * End of cycle 16: finish executing addx 13 (Register X is now 17)
 * Sprite position: ................###.....................
 *
 * Start cycle  17: begin executing addx 4
 * During cycle 17: CRT draws pixel in position 16
 * Current CRT row: ##..##..##..##..#
 *
 * During cycle 18: CRT draws pixel in position 17
 * Current CRT row: ##..##..##..##..##
 * End of cycle 18: finish executing addx 4 (Register X is now 21)
 * Sprite position: ....................###.................
 *
 * Start cycle  19: begin executing noop
 * During cycle 19: CRT draws pixel in position 18
 * Current CRT row: ##..##..##..##..##.
 * End of cycle 19: finish executing noop
 *
 * Start cycle  20: begin executing addx -1
 * During cycle 20: CRT draws pixel in position 19
 * Current CRT row: ##..##..##..##..##..
 *
 * During cycle 21: CRT draws pixel in position 20
 * Current CRT row: ##..##..##..##..##..#
 * End of cycle 21: finish executing addx -1 (Register X is now 20)
 * Sprite position: ...................###..................
 * </code></pre>
 * <p>Allowing the program to run to completion causes the CRT to produce the following image:</p>
 * <pre><code>
 * ##..##..##..##..##..##..##..##..##..##..
 * ###...###...###...###...###...###...###.
 * ####....####....####....####....####....
 * #####.....#####.....#####.....#####.....
 * ######......######......######......####
 * #######.......#######.......#######.....
 * </code></pre>
 * <p>Render the image given by your program. <em>What eight capital letters appear on your CRT?</em></p>
 */
public class Main implements Runnable {
    private enum CPUInstruction {
        NOOP((regX, value) -> regX.get(), 1),
        ADDX(AtomicInteger::addAndGet, 2);

        private final BiFunction<AtomicInteger, Integer, Integer> intFunction;
        private final int cycleCount;

        CPUInstruction(final BiFunction<AtomicInteger, Integer, Integer> intFunction, final int cycleCount) {
            this.intFunction = intFunction;
            this.cycleCount = cycleCount;
        }

        public void applyInstruction(final AtomicInteger regX, final int value) {
            intFunction.apply(regX, value);
        }
    }

    private static class VirtualCPU {
        private int cycle = 0;

        public void pushInstruction(final CPUInstruction instruction, final AtomicInteger regX, final int value) {
            for (int i = 0; i < instruction.cycleCount; i++) {
                drawPixel(regX);
                cycle++;
            }
            instruction.applyInstruction(regX, value);
        }

        private void drawPixel(final AtomicInteger regX) {
            final int cyclePosition = cycle % 40;
            final int spritePosition = regX.get();
            if (cycle != 0 && cyclePosition == 0) {
                System.out.println();
            }
            final String pixel = cyclePosition <= spritePosition + 1 && cyclePosition >= spritePosition - 1 ? "#" : ".";
            System.out.print(pixel);
        }

    }

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            final AtomicInteger regX = new AtomicInteger(1);
            final VirtualCPU cpu = new VirtualCPU();

            for (String s : input) {
                final String[] split = s.split(" ");
                final CPUInstruction cpuInstruction = CPUInstruction.valueOf(split[0].toUpperCase());
                final int value = switch (cpuInstruction) {
                    case ADDX -> Integer.parseInt(split[1]);
                    case NOOP -> 0;
                };

                cpu.pushInstruction(cpuInstruction, regX, value);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
