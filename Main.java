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
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{2f, 4f, 5f, 1f}, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, new float[]{-4f, 3f, 2f, 1f}, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, new float[]{0.2f, 0.2f, 0.5f, 1f}, 0);
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
        gl.glClearColor(0.04f, 0.04f, 0.10f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        glu.gluLookAt(0, 4.5, 10, 0, 0, -3, 0, 1, 0);

        drawBackdrop(gl);
        drawFloorGrid(gl);

        angle += 1f;
        gl.glPushMatrix();
        gl.glTranslatef(0f, 0f, -4f);
        gl.glRotatef(angle, 0f, 1f, 0f);
        gl.glColor3f(0.2f, 0.9f, 0.4f);
        drawCube(gl, 1.5f);
        gl.glPopMatrix();
    }

    private void drawBackdrop(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glBegin(GL2.GL_POINTS);
        for (int i = 0; i < 100; i++) {
            float x = -10f + (i % 20);
            float y = 2f + ((i * 7) % 8);
            gl.glColor3f(0.7f, 0.7f, 0.9f);
            gl.glVertex3f(x, y, -15f);
        }
        gl.glEnd();
        gl.glEnable(GL2.GL_LIGHTING);
    }

    private void drawFloorGrid(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor3f(0.15f, 0.20f, 0.28f);
        gl.glBegin(GL2.GL_LINES);
        for (float x = -8f; x <= 8f; x += 1f) {
            gl.glVertex3f(x, -1.2f, -12f);
            gl.glVertex3f(x, -1.2f, 2f);
        }
        for (float z = -12f; z <= 2f; z += 1f) {
            gl.glVertex3f(-8f, -1.2f, z);
            gl.glVertex3f(8f, -1.2f, z);
        }
        gl.glEnd();
        gl.glEnable(GL2.GL_LIGHTING);
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
