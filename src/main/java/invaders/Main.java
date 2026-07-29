package invaders;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextRenderer;

import invaders.game.Bunker;
import invaders.game.Entity;
import invaders.game.GameLogic;
import invaders.model.Models;
import invaders.model.VoxelModel;
import invaders.ui.HudRenderer;
import invaders.ui.Input;
import invaders.ui.MenuScreen;

public class Main implements GLEventListener {

    private final GLU glu = new GLU();
    private float angle = 0f;

    private final Input input = new Input();
    private final MenuScreen menu = new MenuScreen();
    private final HudRenderer hud = new HudRenderer();
    private boolean inMenu = true;
    private int width = Config.WINDOW_WIDTH, height = Config.WINDOW_HEIGHT;

    private VoxelModel barrier, ship, shipExplosion;
    private final Map<String, VoxelModel> modelsById = new HashMap<>();

    // Each enemy bolt shape alternates between two poses instead of a fixed model.
    private VoxelModel[] crossBoltFrames, zigzagBoltFrames;
    private int boltFrame = 0;
    private int boltTick = 0;
    private static final int BOLT_FRAME_INTERVAL = 6;

    private VoxelModel[] topFrames, midFrames, bottomFrames;
    private int flapFrame = 0;
    private int flapTick = 0;
    private static final int FLAP_FRAME_INTERVAL = 20;

    private GameLogic gameLogic;
    private long lastFrameNanos = 0L;

    // Separate from HudRenderer -- this draws small labels anchored to a
    // 3D world position (bunker lives), not the fixed screen-space HUD.
    private TextRenderer bunkerLabelRenderer;

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

        topFrames = Models.invaderTopFrames();
        midFrames = Models.invaderMidFrames();
        bottomFrames = Models.invaderBottomFrames();
        barrier = Models.barrier();
        ship = Models.playerShip();
        shipExplosion = Models.shipExplosion();

        // Map Entity.modelId -> renderable model. These strings must match
        // what Enemy/Player/Projectile set as modelId in the game package.
        modelsById.put("player_ship", ship);
        modelsById.put("player_bolt", Models.playerBolt());
        modelsById.put("bunker", barrier);
        crossBoltFrames = Models.alienBoltCross();
        zigzagBoltFrames = Models.alienBoltZigzag();

        gameLogic = new GameLogic(input);
        bunkerLabelRenderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 36));
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

        if (inMenu && input.consumeStart()) {
            inMenu = false;
            lastFrameNanos = System.nanoTime(); // avoid a huge dt on the first game frame
        }
        if (inMenu) angle += 0.7f;

        if (++flapTick >= FLAP_FRAME_INTERVAL) {
            flapTick = 0;
            flapFrame = 1 - flapFrame;
        }

        applyProjection(gl);
        gl.glClearColor(0.03f, 0.03f, 0.06f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        if (inMenu) {
            glu.gluLookAt(0, 0.5, 8.5, 0, 0.3, 0, 0, 1, 0);
            drawSpinningInvaders(gl);
            menu.renderMenu(width, height);
        } else {
            long now = System.nanoTime();
            float dt = (now - lastFrameNanos) / 1_000_000_000f;
            lastFrameNanos = now;
            gameLogic.update(Math.min(dt, GameLogic.MAX_ACCUMULATED_DT));

            if (++boltTick >= BOLT_FRAME_INTERVAL) {
                boltTick = 0;
                boltFrame = 1 - boltFrame;
            }

            glu.gluLookAt(0, 2.5, 15.5, 0, 0.4, 0, 0, 1, 0);
            drawGameplay(gl);

            hud.renderHud(gameLogic, width, height);
        }
    }

    private void applyProjection(GL2 gl) {
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        double fov = inMenu ? 55.0 : 48.0;
        glu.gluPerspective(fov, (double) width / height, 0.1, 200.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    private void drawSpinningInvaders(GL2 gl) {
        VoxelModel[] show = { topFrames[flapFrame], midFrames[flapFrame], bottomFrames[flapFrame] };
        for (int i = 0; i < show.length; i++) {
            gl.glPushMatrix();
            gl.glTranslatef((i - 1) * 2.6f, -0.2f, 0f);
            gl.glRotatef(angle, 0f, 1f, 0f);
            show[i].draw(gl);
            gl.glPopMatrix();
        }
    }

    private void drawGameplay(GL2 gl) {
        for (Entity e : gameLogic.getRenderables()) {
            boolean isBolt = e.modelId.equals("enemy_bolt_cross") || e.modelId.equals("enemy_bolt_zigzag")
                    || e.modelId.equals("player_bolt");
            VoxelModel model;
            if (e.modelId.equals("enemy_bolt_cross")) {
                model = crossBoltFrames[boltFrame];
            } else if (e.modelId.equals("enemy_bolt_zigzag")) {
                model = zigzagBoltFrames[boltFrame];
            } else if (e.modelId.equals("enemy_elite")) {
                model = topFrames[flapFrame];
            } else if (e.modelId.equals("enemy_scout")) {
                model = midFrames[flapFrame];
            } else if (e.modelId.equals("enemy_grunt")) {
                model = bottomFrames[flapFrame];
            } else if (e.modelId.equals("player_ship") && !e.alive) {
                model = shipExplosion;
            } else {
                model = modelsById.get(e.modelId);
            }
            if (model == null) continue;
            gl.glPushMatrix();
            gl.glTranslatef(e.x, e.y, e.z);
            if (isBolt) gl.glRotatef(90f, 1f, 0f, 0f);
            if (e.modelId.equals("player_ship")) gl.glRotatef(-45f, 1f, 0f, 0f);
            model.draw(gl);
            gl.glPopMatrix();
        }

        drawBunkerLivesLabels();
    }

    //draw each alive bunker's remaining-lives
    //number as text anchored just above it.
    private void drawBunkerLivesLabels() {
        bunkerLabelRenderer.begin3DRendering();
        bunkerLabelRenderer.setColor(Color.WHITE);
        for (Bunker b : gameLogic.bunkers) {
            if (!b.alive) continue;
            bunkerLabelRenderer.draw3D(b.livesLabel, b.x, b.y - b.halfHeight - 0.5f, b.z, 0.01f);
        }
        bunkerLabelRenderer.end3DRendering();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }
}