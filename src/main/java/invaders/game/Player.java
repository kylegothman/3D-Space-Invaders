package invaders.game;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {

    public static final float SPEED = 8.0f;          // units/sec
    public static final float FIRE_COOLDOWN = 0.35f;  // seconds between shots
    public static final float PLAY_AREA_HALF_WIDTH = 10.0f; // clamp bounds

    public int lives = 3;
    private float fireCooldownRemaining = 0f;

    public boolean moveLeft, moveRight, firing;

    public Player(float x, float y, float z) {
        super(x, y, z);
        this.modelId = "player_ship";
        this.halfWidth = 0.6f;
        this.halfHeight = 0.3f;
        this.halfDepth = 0.6f;
    }

    public List<Projectile> tick(float dt) {
        vx = 0;
        if (moveLeft)  vx -= SPEED;
        if (moveRight) vx += SPEED;

        super.update(dt);

        // clamp inside play field
        if (x < -PLAY_AREA_HALF_WIDTH) x = -PLAY_AREA_HALF_WIDTH;
        if (x >  PLAY_AREA_HALF_WIDTH) x =  PLAY_AREA_HALF_WIDTH;

        List<Projectile> spawned = new ArrayList<>();
        if (fireCooldownRemaining > 0) fireCooldownRemaining -= dt;

        if (firing && fireCooldownRemaining <= 0) {
            spawned.add(Projectile.playerShot(x, y, z));
            fireCooldownRemaining = FIRE_COOLDOWN;
        }
        return spawned;
    }

    public void hit() {
        lives--;
        if (lives <= 0) {
            alive = false;
        }
    }
}