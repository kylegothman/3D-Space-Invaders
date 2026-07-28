package invaders.game;

public class ScoreManager {

    public enum State { PLAYING, WON, LOST }

    private int score = 0;
    private int highScore = 0;
    private State state = State.PLAYING;

    public void addScore(int amount) {
        score += amount;
        if (score > highScore) highScore = score;
    }

    public int getScore() { return score; }
    public int getHighScore() { return highScore; }
    public State getState() { return state; }

    public void setWon() { if (state == State.PLAYING) state = State.WON; }
    public void setLost() { if (state == State.PLAYING) state = State.LOST; }

    public void reset() {
        score = 0;
        state = State.PLAYING;
    }
}