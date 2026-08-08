package invaders;

import java.util.Random;

import com.jogamp.opengl.GL2;

public class Starfield {

    private static final int COUNT = 150;
    private static final float SPAWN_Z = -80f;
    private static final float DESPAWN_Z = 18f;
    private static final float MIN_SPEED = 40f;
    private static final float MAX_SPEED = 90f;
    private static final float STREAK_SECONDS = 0.05f;
    private static final float FLICKER_PER_STAR = 0.03f;
    private static final float FLICKER_MAX = 0.12f;
    private static final float FLICKER_RADIUS = 16f;

    private final float[] x = new float[COUNT];
    private final float[] y = new float[COUNT];
    private final float[] z = new float[COUNT];
    private final float[] speed = new float[COUNT];
    private final Random rng = new Random();

    private float brightnessBoost = 0f;

    public Starfield() {
        for (int i = 0; i < COUNT; i++) {
            respawn(i);
            z[i] = SPAWN_Z + rng.nextFloat() * (DESPAWN_Z - SPAWN_Z);
        }
    }

    private void respawn(int i) {
        float sx, sy;
        do {
            sx = -30f + rng.nextFloat() * 60f;
            sy = -14f + rng.nextFloat() * 32f;
        } while (Math.abs(sx) < 11f && sy > -3f && sy < 5f);
        x[i] = sx;
        y[i] = sy;
        z[i] = SPAWN_Z - rng.nextFloat() * 20f;
        speed[i] = MIN_SPEED + rng.nextFloat() * (MAX_SPEED - MIN_SPEED);
    }

    public void update(float dt) {
        int passing = 0;
        for (int i = 0; i < COUNT; i++) {
            z[i] += speed[i] * dt;
            if (z[i] > DESPAWN_Z) respawn(i);
            if (z[i] > -5f && z[i] < 12f) {
                float dx = x[i];
                float dy = y[i] - 1f;
                if (dx * dx + dy * dy < FLICKER_RADIUS * FLICKER_RADIUS) passing++;
            }
        }
        brightnessBoost = Math.min(FLICKER_MAX, passing * FLICKER_PER_STAR);
    }

    public float brightnessBoost() {
        return brightnessBoost;
    }

    public void draw(GL2 gl) {
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glLineWidth(1.5f);
        gl.glBegin(GL2.GL_LINES);
        for (int i = 0; i < COUNT; i++) {
            float progress = (z[i] - SPAWN_Z) / (DESPAWN_Z - SPAWN_Z);
            float head = 0.35f + 0.65f * Math.min(1f, progress * 1.6f);
            float tailZ = z[i] - speed[i] * STREAK_SECONDS;
            gl.glColor3f(head * 0.25f, head * 0.28f, head * 0.4f);
            gl.glVertex3f(x[i], y[i], tailZ);
            gl.glColor3f(head * 0.85f, head * 0.9f, head);
            gl.glVertex3f(x[i], y[i], z[i]);
        }
        gl.glEnd();
        gl.glLineWidth(1f);
        gl.glEnable(GL2.GL_LIGHTING);
    }
}
