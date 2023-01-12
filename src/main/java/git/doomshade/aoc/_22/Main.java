package git.doomshade.aoc._22;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.List;

/**
 * <h2 id="part2">--- Part Two ---</h2><p>The Elf finishes helping with the tent and sneaks back over to you. "Anyway, the second column says how the round needs to end:
 * <code>X</code> means you need to lose, <code>Y</code> means you need to end the round in a draw, and <code>Z</code> means you need to win. Good luck!"</p>
 * <p>The total score is still calculated in the same way, but now you need to figure out what shape to choose so the round ends as indicated. The example above now goes
 * like this:</p>
 * <ul>
 * <li>In the first round, your opponent will choose Rock (<code>A</code>), and you need the round to end in a draw (<code>Y</code>), so you also choose Rock. This gives
 * you a score of 1 + 3 = <em>4</em>.</li>
 * <li>In the second round, your opponent will choose Paper (<code>B</code>), and you choose Rock so you lose (<code>X</code>) with a score of 1 + 0 = <em>1</em>.</li>
 * <li>In the third round, you will defeat your opponent's Scissors with Rock for a score of 1 + 6 = <em>7</em>.</li>
 * </ul>
 * <p>Now that you're correctly decrypting the ultra top secret strategy guide, you would get a total score of <code><em>12</em></code>.</p>
 * <p>Following the Elf's instructions for the second column, <em>what would your total score be if everything goes exactly according to your strategy guide?</em></p>
 */
public class Main implements Runnable {
    private static String toolToStr(int tool) {
        return tool == 0 ? "Rock" : tool == 1 ? "Paper" : tool == 2 ? "Scissors" : "Unknown Tool";
    }

    private static String matchResultToStr(int matchResult) {
        return matchResult == 0 ? "Lose" : matchResult == 1 ? "Draw" : matchResult == 2 ? "Win" : "Unknown Result";
    }

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            int score = 0;
            for (String s : input) {
                final int prevScore = score;
                final String[] split = s.split(" ");
                final int opponent = split[0].charAt(0) - 'A';
                final int player = split[1].charAt(0) - 'X';

                // opponent is rock (0) and we must lose (0) -> paper (0 + 1 - 0 = 1) is chosen
                // opponent is scissors (2) and we must draw (1) ->  scissors (2 + 1 - 1 = 2) are chosen
                // opponent is scissors (2) and we must win (2) ->  rock (2 + 1 - 2 = 1) is chosen
                final int chosenTool = Math.floorMod(opponent + player - 1, 3);
                final int chosenToolScore = chosenTool + 1;
                score += chosenToolScore;
                System.out.printf("%s -> %s. Chosen tool: %s%n", toolToStr(opponent), matchResultToStr(player), toolToStr(chosenTool));
                final int resultScore = player * 3;
                score += resultScore;
                System.out.printf("Score: %d -> %d (%d for tool, %d for match result, %d total)%n%n",
                                  prevScore,
                                  score,
                                  chosenToolScore,
                                  resultScore,
                                  chosenToolScore + resultScore);
            }
            System.out.println(score);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
