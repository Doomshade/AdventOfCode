package git.doomshade.aoc._21;

import git.doomshade.aoc.shared.Util;

import java.io.IOException;
import java.util.List;

/**
 * <h2>--- Day 2: Rock Paper Scissors ---</h2><p>The Elves begin to set up camp on the beach. To decide whose tent gets to be closest to the snack storage, a giant
 * <a href="https://en.wikipedia.org/wiki/Rock_paper_scissors" target="_blank">Rock Paper Scissors</a> tournament is already in progress.</p>
 * <p>Rock Paper Scissors is a game between two players. Each game contains many rounds; in each round, the players each simultaneously choose one of Rock, Paper, or
 * Scissors using a hand shape. Then, a winner for that round is selected: Rock defeats Scissors, Scissors defeats Paper, and Paper defeats Rock. If both players choose the
 * same shape, the round instead ends in a draw.</p>
 * <p>Appreciative of your help yesterday, one Elf gives you an <em>encrypted strategy guide</em> (your puzzle input) that they say will be sure to help you win. "The first
 * column is what your opponent is going to play: <code>A</code> for Rock, <code>B</code> for Paper, and <code>C</code> for Scissors. The second column--" Suddenly, the Elf
 * is called away to help with someone's tent.</p>
 * <p>The second column, <span title="Why do you keep guessing?!">you reason</span>, must be what you should play in response: <code>X</code> for Rock, <code>Y</code> for
 * Paper, and <code>Z</code> for Scissors. Winning every time would be suspicious, so the responses must have been carefully chosen.</p>
 * <p>The winner of the whole tournament is the player with the highest score. Your <em>total score</em> is the sum of your scores for each round. The score for a single
 * round is the score for the <em>shape you selected</em> (1 for Rock, 2 for Paper, and 3 for Scissors) plus the score for the <em>outcome of the round</em> (0 if you lost,
 * 3 if the round was a draw, and 6 if you won).</p>
 * <p>Since you can't be sure if the Elf is trying to help you or trick you, you should calculate the score you would get if you were to follow the strategy guide.</p>
 * <p>For example, suppose you were given the following strategy guide:</p>
 * <pre><code>
 * A Y
 * B X
 * C Z
 * </code></pre>
 * <p>This strategy guide predicts and recommends the following:</p>
 * <ul>
 * <li>In the first round, your opponent will choose Rock (<code>A</code>), and you should choose Paper (<code>Y</code>). This ends in a win for you with a score of
 * <em>8</em> (2 because you chose Paper + 6 because you won).</li>
 * <li>In the second round, your opponent will choose Paper (<code>B</code>), and you should choose Rock (<code>X</code>). This ends in a loss for you with a score of
 * <em>1</em> (1 + 0).</li>
 * <li>The third round is a draw with both players choosing Scissors, giving you a score of 3 + 3 = <em>6</em>.</li>
 * </ul>
 * <p>In this example, if you were to follow the strategy guide, you would get a total score of <code><em>15</em></code> (8 + 1 + 6).</p>
 * <p><em>What would your total score be if everything goes exactly according to your strategy guide?</em></p>
 */
public class Main implements Runnable {

    @Override
    public void run() {
        try {
            final List<String> input = Util.readStringInput(getClass(), "input.txt");
            int score = 0;
            for (String s : input) {
                final String[] split = s.split(" ");
                final int opponent = split[0].charAt(0) - 'A';
                final int player = split[1].charAt(0) - 'X';
                final int delta = Math.floorMod(player - opponent, 3);
                score += player + 1;

                switch (delta) {
                    // draw
                    case 0:
                        score += 3;
                        break;
                    // win
                    case 1:
                        score += 6;
                        break;
                    // lose
                    case 2:
                        break;
                }
            }
            System.out.println(score);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
