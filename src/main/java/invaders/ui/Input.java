package invaders.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import invaders.game.GameLogic;

// Keyboard input for both the menu (Enter) and gameplay (A/D or arrow keys,
// Space to fire). Enter also restarts once the round has ended.
// Add as a KeyListener on the canvas.
public class Input extends KeyAdapter implements GameLogic.InputSource {

    // Set on ENTER press, cleared once read via consumeStart().
    private boolean startEdge;

    private final AtomicBoolean left    = new AtomicBoolean(false);
    private final AtomicBoolean right   = new AtomicBoolean(false);
    private final AtomicBoolean fire    = new AtomicBoolean(false);
    private final AtomicBoolean enter   = new AtomicBoolean(false);

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) startEdge = true;
        setFlag(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setFlag(e.getKeyCode(), false);
    }

    private void setFlag(int keyCode, boolean value) {
        switch (keyCode) {
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left.set(value);
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right.set(value);
            case KeyEvent.VK_SPACE -> fire.set(value);
            case KeyEvent.VK_ENTER -> enter.set(value);
            default -> {
            }
        }
    }

    // True exactly once per ENTER press.
    public boolean consumeStart() { boolean v = startEdge; startEdge = false; return v; }

    @Override public boolean isLeftPressed()  { return left.get(); }
    @Override public boolean isRightPressed() { return right.get(); }
    @Override public boolean isFirePressed()  { return fire.get(); }

    // Enter doubles as both the menu-start key (edge-triggered via consumeStart())
    // and the post-game-over restart key (level-triggered, like fire/left/right).
    @Override public boolean isRestartPressed() { return enter.get(); }
}