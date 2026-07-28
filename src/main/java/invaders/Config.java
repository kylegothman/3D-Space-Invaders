package invaders;

public final class Config {

    private Config() {}

    public static final int WINDOW_WIDTH = 1024;
    public static final int WINDOW_HEIGHT = 768;
    public static final int TARGET_FPS = 60;

    public static final int ROWS = 5;
    public static final int COLS = 5;
    public static final float SPACING_X = 1.5f;
    public static final float SPACING_Y = 1.15f;
    public static final float ROW_DEPTH = 0.6f;
    public static final float GROUND_TILT_DEG = 18f;

    public static final float MENU_SPIN_DEG_PER_SEC = 42f;
    public static final float MAX_FRAME_DT = 0.1f;
}
