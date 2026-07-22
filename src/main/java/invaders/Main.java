package invaders;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
//import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import invaders.model.Models;
import invaders.model.VoxelModel;
import invaders.ui.Input;
import invaders.ui.MenuScreen;

/**
 * Title menu with three spinning invaders behind the text.
 */
public class Main implements GLEventListener {

    private final GLU glu = new GLU();
    private float angle = 0f;

    private final Input input = new Input();
    private final MenuScreen menu = new MenuScreen();
    private boolean inMenu = true;
    private int width = 1024, height = 768;

    private VoxelModel top, mid, bottom;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
            //GLCanvas canvas = new GLCanvas(caps);
            GLJPanel canvas = new GLJPanel(caps);
            Main app = new Main();
            canvas.addGLEventListener(app);
            canvas.addKeyListener(app.input);
            canvas.setFocusable(true);

            JFrame frame = new JFrame("3D Space Invaders - Team 7");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1024, 768);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            canvas.requestFocusInWindow();

            new FPSAnimator(canvas, 60, true).start();
        });
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{2f, 4f, 5f, 1f}, 0);

        top = Models.invaderTopFrames()[0];
        mid = Models.invaderMidFrames()[0];
        bottom = Models.invaderBottomFrames()[0];
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        this.width = w;
        this.height = Math.max(h, 1);
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, (double) w / this.height, 0.1, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        if (inMenu && input.consumeStart()) {
            inMenu = false;
        }

        angle += 0.7f;

        gl.glClearColor(0.03f, 0.03f, 0.06f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(0, 0.5, 8.5, 0, 0.3, 0, 0, 1, 0);

        drawSpinningInvaders(gl);

        if (inMenu) {
            menu.renderMenu(width, height);
        }
    }

    private void drawSpinningInvaders(GL2 gl) {
        VoxelModel[] show = { top, mid, bottom };
        for (int i = 0; i < show.length; i++) {
            gl.glPushMatrix();
            gl.glTranslatef((i - 1) * 2.6f, -0.2f, 0f);
            gl.glRotatef(angle, 0f, 1f, 0f);
            show[i].draw(gl);
            gl.glPopMatrix();
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }
}
