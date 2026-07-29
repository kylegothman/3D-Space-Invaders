package invaders.game;

import java.util.Random;

import invaders.model.Models;
import invaders.model.VoxelModel;

public class Bunker extends Entity {

    private static final int MIN_DAMAGE_RADIUS = 0;
    private static final int MAX_DAMAGE_RADIUS = 1;

    private static final Random rng = new Random();

    public Bunker(float x, float y, float z) {
        super(x, y, z);
        this.modelId = "bunker";
        this.model = Models.barrier();
        this.halfWidth = 1.0f;
        this.halfHeight = 0.7f;
        this.halfDepth = 0.5f;
    }

    public boolean hitAt(float worldX, float worldY) {
        if (!alive) return false;

        float localX = worldX - this.x;
        float localY = worldY - this.y;
        int col = model.colForLocalX(localX);
        int row = model.rowForLocalY(localY);

        if (!model.isSolid(row, col)) return false;

        int radius = MIN_DAMAGE_RADIUS + rng.nextInt(MAX_DAMAGE_RADIUS - MIN_DAMAGE_RADIUS + 1);
        model.destroyAt(row, col, radius);
        if (model.isFullyDestroyed()) {
            alive = false;
        }
    }
}
