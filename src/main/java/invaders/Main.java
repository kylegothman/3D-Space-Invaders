package invaders;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Setup check: a lit, spinning cube in a perspective scene.
 * If this runs, your environment works. Build the game from here.
 */
public class Main implements GLEventListener {

    private final GLU glu = new GLU();
    private float angle = 0f;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
            GLCanvas canvas = new GLCanvas(caps);
            canvas.addGLEventListener(new Main());

            JFrame frame = new JFrame("3D Space Invaders — Team 7");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1024, 768);
            frame.add(canvas);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

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
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60.0, (double) w / Math.max(h, 1), 0.1, 100.0);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0.05f, 0.05f, 0.1f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(0, 2.5, 6, 0, 0, 0, 0, 1, 0);

        angle += 1f;
        gl.glRotatef(angle, 0f, 1f, 0f);
        gl.glColor3f(0.2f, 0.9f, 0.4f);
        drawCube(gl, 1.5f);
    }

    private void drawCube(GL2 gl, float s) {
        float h = s / 2;
        float[][] n = {{0,0,1},{0,0,-1},{0,1,0},{0,-1,0},{1,0,0},{-1,0,0}};
        float[][][] f = {
            {{-h,-h, h},{ h,-h, h},{ h, h, h},{-h, h, h}},
            {{ h,-h,-h},{-h,-h,-h},{-h, h,-h},{ h, h,-h}},
            {{-h, h, h},{ h, h, h},{ h, h,-h},{-h, h,-h}},
            {{-h,-h,-h},{ h,-h,-h},{ h,-h, h},{-h,-h, h}},
            {{ h,-h, h},{ h,-h,-h},{ h, h,-h},{ h, h, h}},
            {{-h,-h,-h},{-h,-h, h},{-h, h, h},{-h, h,-h}},
        };
        gl.glBegin(GL2.GL_QUADS);
        for (int i = 0; i < 6; i++) {
            gl.glNormal3fv(n[i], 0);
            for (float[] v : f[i]) gl.glVertex3fv(v, 0);
        }
        gl.glEnd();
    }

    @Override
    public void dispose(GLAutoDrawable drawable) { }
}
