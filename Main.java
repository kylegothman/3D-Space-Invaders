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

        glu.gluLookAt(0, 4.0, 10.0, 0, 0, -3, 0, 1, 0);

        drawFloor(gl);

        drawBarrier(gl, -3f, -0.7f, -2f);
        drawBarrier(gl, 0f, -0.7f, -2f);
        drawBarrier(gl, 3f, -0.7f, -2f);

        drawAlien(gl, -2f, 1.6f, -6f);
        drawAlien(gl, 0f, 1.6f, -6f);
        drawAlien(gl, 2f, 1.6f, -6f);

        drawShip(gl, 0f, -0.9f, 0f);
    }

    private void drawFloor(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glColor3f(0.12f, 0.16f, 0.18f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-8f, -1.2f, -12f);
        gl.glVertex3f(8f, -1.2f, -12f);
        gl.glVertex3f(8f, -1.2f, 2f);
        gl.glVertex3f(-8f, -1.2f, 2f);
        gl.glEnd();
        gl.glEnable(GL2.GL_LIGHTING);
    }

    private void drawBox(GL2 gl, float sx, float sy, float sz) {
        gl.glPushMatrix();
        gl.glScalef(sx, sy, sz);
        drawCube(gl, 1f);
        gl.glPopMatrix();
    }

    private void drawBarrier(GL2 gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glColor3f(0.55f, 0.35f, 0.15f);
        drawBox(gl, 1.2f, 0.5f, 0.5f);
        gl.glPopMatrix();
    }

    private void drawAlien(GL2 gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glColor3f(0.3f, 0.9f, 0.6f);

        drawBox(gl, 0.8f, 0.35f, 0.5f);

        gl.glPushMatrix();
        gl.glTranslatef(0f, 0.3f, 0f);
        drawBox(gl, 0.45f, 0.25f, 0.35f);
        gl.glPopMatrix();

        gl.glPopMatrix();
    }

    private void drawShip(GL2 gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glTranslatef(x, y, z);
        gl.glColor3f(0.2f, 0.85f, 0.3f);

        drawBox(gl, 1.2f, 0.25f, 0.7f);

        gl.glPushMatrix();
        gl.glTranslatef(0f, 0.25f, 0f);
        drawBox(gl, 0.6f, 0.2f, 0.4f);
        gl.glPopMatrix();

        gl.glPopMatrix();
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
