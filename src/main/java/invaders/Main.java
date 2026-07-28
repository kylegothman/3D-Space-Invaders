package invaders;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
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
    private GameState state = GameState.MENU;
    private int width = Config.WINDOW_WIDTH, height = Config.WINDOW_HEIGHT;
    private long lastFrameNanos;

    private VoxelModel top, mid, bottom, barrier, ship;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
            GLJPanel canvas = new GLJPanel(caps);
            Main app = new Main();
            canvas.addGLEventListener(app);
            canvas.addKeyListener(app.input);
            canvas.setFocusable(true);

            JFrame frame = new JFrame("3D Space Invaders - Team 7");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            canvas.requestFocusInWindow();

            new FPSAnimator(canvas, Config.TARGET_FPS, true).start();
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

        lastFrameNanos = System.nanoTime();
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

        update(nextFrameDt());
        render(gl);
    }

    private float nextFrameDt() {
        long now = System.nanoTime();
        float dt = (now - lastFrameNanos) / 1_000_000_000f;
        lastFrameNanos = now;
        return Math.min(dt, Config.MAX_FRAME_DT);
    }

    private void update(float dt) {
        switch (state) {
            case MENU -> updateMenu(dt);
            case PLAYING -> updatePlaying(dt);
            case GAME_OVER, WIN -> updateEnded(dt);
        }
    }

    private void updateMenu(float dt) {
        if (input.consumeStart()) {
            state = GameState.PLAYING;
            return;
        }
        angle += Config.MENU_SPIN_DEG_PER_SEC * dt;
    }

    private void updatePlaying(float dt) {
    }

    private void updateEnded(float dt) {
        if (input.consumeStart()) state = GameState.MENU;
    }

    private void render(GL2 gl) {
        applyProjection(gl);
        gl.glClearColor(0.03f, 0.03f, 0.06f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if (state == GameState.MENU) {
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
        double fov = state == GameState.MENU ? 55.0 : 38.0;
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
        for (int r = 0; r < Config.ROWS; r++) {
            for (int c = 0; c < Config.COLS; c++) {
                gl.glPushMatrix();
                float x = (c - (Config.COLS - 1) * 0.5f) * Config.SPACING_X;
                float y = 3.2f - r * Config.SPACING_Y;
                float z = -(Config.ROWS - 1 - r) * Config.ROW_DEPTH;
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
        gl.glRotatef(-Config.GROUND_TILT_DEG, 1f, 0f, 0f);
        ship.draw(gl);
        gl.glPopMatrix();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }
}
