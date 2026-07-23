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

public class Main implements GLEventListener {

    private final GLU glu = new GLU();
    private float angle = 0f;

    private final Input input = new Input();
    private final MenuScreen menu = new MenuScreen();
    private boolean inMenu = true;
    private int width = 1024, height = 768;

    private VoxelModel top, mid, bottom, barrier, ship;

    private static final int ROWS = 5, COLS = 5;
    private static final float SPACING_X = 1.5f, SPACING_Y = 1.15f, ROW_DEPTH = 0.6f;
    private static final float GROUND_TILT_DEG = 18f;

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
        barrier = Models.barrier();
        ship = Models.playerShip();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        this.width = w;
        this.height = Math.max(h, 1);
        drawable.getGL().getGL2().glViewport(0, 0, w, h);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        if (inMenu && input.consumeStart()) inMenu = false;
        if (inMenu) angle += 0.7f;

        applyProjection(gl);
        gl.glClearColor(0.03f, 0.03f, 0.06f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if (inMenu) {
            glu.gluLookAt(0, 0.5, 8.5, 0, 0.3, 0, 0, 1, 0);
            drawSpinningInvaders(gl);
            menu.renderMenu(width, height);
        } else {
            glu.gluLookAt(0, 2.5, 15.5, 0, 0.4, 0, 0, 1, 0);
            drawArrangedScene(gl);
        }
    }

    private void applyProjection(GL2 gl) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        double fov = inMenu ? 55.0 : 38.0;
        glu.gluPerspective(fov, (double) width / height, 0.1, 200.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
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

    private void drawArrangedScene(GL2 gl) {
        VoxelModel[] rowModel = { top, top, mid, mid, bottom };
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                gl.glPushMatrix();
                float x = (c - (COLS - 1) * 0.5f) * SPACING_X;
                float y = 3.2f - r * SPACING_Y;
                float z = -(ROWS - 1 - r) * ROW_DEPTH;
                gl.glTranslatef(x, y, z);
                rowModel[r].draw(gl);
                gl.glPopMatrix();
            }
        }

        for (int i = 0; i < 4; i++) {
            gl.glPushMatrix();
            gl.glTranslatef((i - 1.5f) * 2.6f, -2.4f, 1.0f);
            barrier.draw(gl);
            gl.glPopMatrix();
        }

        gl.glPushMatrix();
        gl.glTranslatef(0f, -3.3f, 1.8f);
        gl.glRotatef(-GROUND_TILT_DEG, 1f, 0f, 0f);
        ship.draw(gl);
        gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }
}
