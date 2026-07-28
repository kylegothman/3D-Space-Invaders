package invaders.ui;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

import com.jogamp.opengl.util.awt.TextRenderer;

import invaders.game.GameLogic;
import invaders.game.ScoreManager;

public class HudRenderer {

    private TextRenderer hud, big, mid;

    private static final float LIFE_R = 0.90f, LIFE_G = 0.25f, LIFE_B = 0.25f;
    private static final float WIN_R  = 0.20f, WIN_G  = 0.90f, WIN_B  = 0.40f;
    private static final float LOSE_R = 0.95f, LOSE_G = 0.30f, LOSE_B = 0.30f;

    private TextRenderer hud() {
        if (hud == null) hud = new TextRenderer(new Font("Monospaced", Font.BOLD, 22));
        return hud;
    }

    private TextRenderer big() {
        if (big == null) big = new TextRenderer(new Font("Monospaced", Font.BOLD, 56));
        return big;
    }

    private TextRenderer mid() {
        if (mid == null) mid = new TextRenderer(new Font("Monospaced", Font.BOLD, 26));
        return mid;
    }

    public void renderHud(GameLogic logic, int w, int h) {
        drawLives(logic.player.lives, w, h);

        if (logic.isGameOver()) {
            drawRestartPrompt(logic, w, h);
        }
    }

    private void drawLives(int lives, int w, int h) {
        StringBuilder pips = new StringBuilder();
        for (int i = 0; i < lives; i++) pips.append("\u25B2 "); // one triangle "ship" per life
        String text = "LIVES  " + pips.toString().trim();

        TextRenderer tr = hud();
        tr.beginRendering(w, h);
        tr.setColor(LIFE_R, LIFE_G, LIFE_B, 1f);
        tr.draw(text, 20, h - 40);
        tr.endRendering();
    }

    private void drawRestartPrompt(GameLogic logic, int w, int h) {
        boolean won = logic.score.getState() == ScoreManager.State.WON;
        String headline = won ? "YOU WIN" : "GAME OVER";
        String subline = "PRESS ENTER TO RESTART";

        float hr = won ? WIN_R : LOSE_R;
        float hg = won ? WIN_G : LOSE_G;
        float hb = won ? WIN_B : LOSE_B;

        drawCentered(big(), headline, w, h, 0.70f, hr, hg, hb);
        drawCentered(mid(), subline, w, h, 0.60f, 1f, 1f, 1f);
    }

    private void drawCentered(TextRenderer tr, String text, int w, int h,
                              float yFraction, float r, float g, float b) {
        Rectangle2D bounds = tr.getBounds(text);
        int x = (int) ((w - bounds.getWidth()) / 2.0);
        int y = (int) (h * yFraction);
        tr.beginRendering(w, h);
        tr.setColor(r, g, b, 1f);
        tr.draw(text, x, y);
        tr.endRendering();
    }
}