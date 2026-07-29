package invaders.game;

public class Bunker extends Entity {

    public static final int MAX_LIVES = 3;

    public int lives = MAX_LIVES;

    public String livesLabel = String.valueOf(MAX_LIVES);

    public Bunker(float x, float y, float z) {
        super(x, y, z);
        this.modelId = "bunker";
        this.halfWidth = 1.0f;
        this.halfHeight = 0.7f;
        this.halfDepth = 0.5f;
    }

    // Absorbs one shot. Bunker disappears once lives reach zero.
    public void hit() {
        if (lives <= 0) return; // already destroyed, nothing to do
        lives--;
        if (lives <= 0) {
            lives = 0;
            alive = false;
        }
        livesLabel = String.valueOf(lives);
    }
}