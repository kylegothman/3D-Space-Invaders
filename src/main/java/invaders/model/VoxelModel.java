package invaders.model;

import com.jogamp.opengl.GL2;

// A 3D model where every '#' becomes a lit cube.
public class VoxelModel {

    private final String[] bitmap;
    private final int rows;
    private final int cols;
    private final float unit;
    private final float voxel;
    private final float depthHalf;
    private final float r, g, b;

    public VoxelModel(String[] bitmap, float unit, float depth,
                      float r, float g, float b) {
        this.bitmap = bitmap;
        this.rows = bitmap.length;
        int maxCols = 0;
        for (String row : bitmap) maxCols = Math.max(maxCols, row.length());
        this.cols = maxCols;
        this.unit = unit;
        this.voxel = unit * 0.46f;
        this.depthHalf = depth * 0.5f;
        this.r = r; this.g = g; this.b = b;
    }

    public float width()  { return cols * unit; }
    public float height() { return rows * unit; }

    public void draw(GL2 gl) {
        gl.glColor3f(r, g, b);
        float xOffset = (cols - 1) * 0.5f;
        float yOffset = (rows - 1) * 0.5f;
        for (int row = 0; row < rows; row++) {
            String line = bitmap[row];
            for (int col = 0; col < line.length(); col++) {
                if (line.charAt(col) != '#') continue;
                float x = (col - xOffset) * unit;
                float y = (yOffset - row) * unit;
                gl.glPushMatrix();
                gl.glTranslatef(x, y, 0f);
                drawBox(gl, voxel, voxel, depthHalf);
                gl.glPopMatrix();
            }
        }
    }

    // One lit box centered on the origin, half-extents hx/hy/hz.
    private void drawBox(GL2 gl, float hx, float hy, float hz) {
        float[][] n = {{0,0,1},{0,0,-1},{0,1,0},{0,-1,0},{1,0,0},{-1,0,0}};
        float[][][] f = {
            {{-hx,-hy, hz},{ hx,-hy, hz},{ hx, hy, hz},{-hx, hy, hz}},
            {{ hx,-hy,-hz},{-hx,-hy,-hz},{-hx, hy,-hz},{ hx, hy,-hz}},
            {{-hx, hy, hz},{ hx, hy, hz},{ hx, hy,-hz},{-hx, hy,-hz}},
            {{-hx,-hy,-hz},{ hx,-hy,-hz},{ hx,-hy, hz},{-hx,-hy, hz}},
            {{ hx,-hy, hz},{ hx,-hy,-hz},{ hx, hy,-hz},{ hx, hy, hz}},
            {{-hx,-hy,-hz},{-hx,-hy, hz},{-hx, hy, hz},{-hx, hy,-hz}},
        };
        gl.glBegin(GL2.GL_QUADS);
        for (int i = 0; i < 6; i++) {
            gl.glNormal3fv(n[i], 0);
            for (float[] v : f[i]) gl.glVertex3fv(v, 0);
        }
        gl.glEnd();
    }
}
