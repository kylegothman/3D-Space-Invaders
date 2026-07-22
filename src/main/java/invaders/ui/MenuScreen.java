package invaders.ui;

import com.jogamp.opengl.util.awt.TextRenderer;

import java.awt.Font;
import java.awt.geom.Rectangle2D;

// Title menu text. TextRenderers are built lazily since they need a live GL context.
public class MenuScreen {

    private TextRenderer big, mid, small;

    private static final float GR = 0.20f, GG = 0.90f, GB = 0.40f;

    private TextRenderer big() {
        if (big == null) big = new TextRenderer(new Font("Monospaced", Font.BOLD, 64));
        return big;
    }

    private TextRenderer mid() {
        if (mid == null) mid = new TextRenderer(new Font("Monospaced", Font.BOLD, 28));
        return mid;
    }

    private TextRenderer small() {
        if (small == null) small = new TextRenderer(new Font("Monospaced", Font.PLAIN, 22));
        return small;
    }

    public void renderMenu(int w, int h) {
        drawCentered(big(),   "3D SPACE INVADERS",    w, h, 0.62f, GR, GG, GB);
        drawCentered(mid(),   "TEAM 7",               w, h, 0.52f, 0.8f, 0.8f, 0.8f);
        drawCentered(small(), "PRESS ENTER TO START", w, h, 0.34f, 1f, 1f, 1f);
        drawCentered(small(), "ARROWS / A D  MOVE    SPACE  FIRE", w, h, 0.26f, 0.6f, 0.6f, 0.6f);
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
