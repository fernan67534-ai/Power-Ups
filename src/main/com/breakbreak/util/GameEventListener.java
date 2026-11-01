package main.com.breakbreak.util;

public interface GameEventListener {
    void onScoreChanged(int newScore);
    void onLifeLost(int remainingLives);
    void onGameOver(int finalScore);
    void onVictory(int finalScore);
    void onReset();
}
