package invaders.model;

import com.jogamp.opengl.GL2;

public class VoxelModel {

    private final boolean[][] grid;
    private final int rows;
    private final int cols;
    private final float unit;
    private final float voxel;
    private final float depthHalf;
    private final float r, g, b;

    public VoxelModel(String[] bitmap, float unit, float depth,
                      float r, float g, float b) {
        this.rows = bitmap.length;
        int maxCols = 0;
        for (String row : bitmap) maxCols = Math.max(maxCols, row.length());
        this.cols = maxCols;
        this.grid = new boolean[rows][cols];
        for (int row = 0; row < rows; row++) {
            String line = bitmap[row];
            for (int col = 0; col < line.length(); col++) {
                grid[row][col] = line.charAt(col) == '#';
            }
        }
        this.unit = unit;
        this.voxel = unit * 0.46f;
        this.depthHalf = depth * 0.5f;
        this.r = r; this.g = g; this.b = b;
    }

    public float width()  { return cols * unit; }
    public float height() { return rows * unit; }
    public int rows() { return rows; }
    public int cols() { return cols; }
    public float unit() { return unit; }

    // Converts a local x offset (relative to the model's center) into a grid
    // column. Can return an out-of-range index; check with isSolid().
    public int colForLocalX(float localX) {
        float xOffset = (cols - 1) * 0.5f;
        return Math.round(localX / unit + xOffset);
    }

    // Converts a local y offset (relative to the model's center) into a grid
    // row. Can return an out-of-range index; check with isSolid().
    public int rowForLocalY(float localY) {
        float yOffset = (rows - 1) * 0.5f;
        return Math.round(yOffset - localY / unit);
    }

    public boolean isSolid(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return false;
        return grid[row][col];
    }

    public void destroyAt(int row, int col, int radius) {
        int r2 = radius * radius;
        for (int dr = -radius; dr <= radius; dr++) {
            for (int dc = -radius; dc <= radius; dc++) {
                if (dr * dr + dc * dc > r2) continue;
                int rr = row + dr, cc = col + dc;
                if (rr < 0 || rr >= rows || cc < 0 || cc >= cols) continue;
                grid[rr][cc] = false;
            }
        }
    }

    // True once every voxel in the grid has been cleared.
    public boolean isFullyDestroyed() {
        for (boolean[] rowArr : grid) {
            for (boolean v : rowArr) {
                if (v) return false;
            }
        }
        return true;
    }

    public void draw(GL2 gl) {
        gl.glColor3f(r, g, b);
        float xOffset = (cols - 1) * 0.5f;
        float yOffset = (rows - 1) * 0.5f;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (!grid[row][col]) continue;
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