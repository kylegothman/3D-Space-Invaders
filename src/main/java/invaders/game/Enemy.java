package invaders.game;

public class Enemy extends Entity {

    public enum Type {
        GRUNT(10), SCOUT(20), ELITE(30);
        public final int points;
        Type(int points) { this.points = points; }
    }

    public final Type type;
    public final int row, col;

    /**
     * Frames remaining to display the death explosion before this enemy
     * is fully removed from the render list. Set to DEATH_DISPLAY_FRAMES
     * when the enemy is killed; counts down each game step.
     */
    public int deathTimer = 0;
    public static final int DEATH_DISPLAY_FRAMES = 30; // ~0.5 s at 60 fps

    public Enemy(float x, float y, float z, Type type, int row, int col) {
        super(x, y, z);
        this.type = type;
        this.row = row;
        this.col = col;
        this.modelId = "enemy_" + type.name().toLowerCase();
        this.halfWidth = 0.5f;
        this.halfHeight = 0.4f;
        this.halfDepth = 0.5f;
    }
}
