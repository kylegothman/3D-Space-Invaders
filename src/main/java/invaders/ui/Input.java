package invaders.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Keyboard input for the menu. Add as a KeyListener on the canvas.
public class Input extends KeyAdapter {

    // Set on ENTER press, cleared once read via consumeStart().
    private boolean startEdge;

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) startEdge = true;
    }

    // True exactly once per ENTER press.
    public boolean consumeStart() { boolean v = startEdge; startEdge = false; return v; }
}
