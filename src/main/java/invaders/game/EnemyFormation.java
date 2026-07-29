package invaders.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyFormation {

    public final List<Enemy> enemies = new ArrayList<>();

    private final float fieldHalfWidth;
    private final float dropDistance;
    private final float baseSpeed;
    private float currentDirection = 1f; // 1 = right, -1 = left
    private int dropCount = 0;
    private final Random rng = new Random();

    private float fireTimer = 0f;
    private final float fireIntervalMin = 0.36f;  // was 0.4f  (10% faster)
    private final float fireIntervalMax = 1.08f;  // was 1.2f  (10% faster)

    // How far (world units) a shot's spawn x can drift from the shooter's
    // exact x. Without this, every shot from a given lane lands on the same
    // barrier column, punches one hole, and every later shot just passes
    // through that hole instead of spreading damage across the bunker.
    private final float shotXJitter = 0.35f;
    
    public EnemyFormation(int rows, int cols, float spacingX, float spacingZ,
                           float originX, float originY, float originZ,
                           float fieldHalfWidth, float baseSpeed, float dropDistance) {
        this.fieldHalfWidth = fieldHalfWidth;
        this.baseSpeed = baseSpeed;
        this.dropDistance = dropDistance;

        for (int r = 0; r < rows; r++) {
            Enemy.Type type = pickTypeForRow(r);
            for (int c = 0; c < cols; c++) {
                float x = originX + c * spacingX;
                float z = originZ + r * spacingZ;
                enemies.add(new Enemy(x, originY, z, type, r, c));
            }
        }
        fireTimer = nextFireDelay();
    }

    private Enemy.Type pickTypeForRow(int row) {
        if (row == 0) return Enemy.Type.ELITE;
        if (row <= 2) return Enemy.Type.SCOUT;
        return Enemy.Type.GRUNT;
    }

    // Speed scales up as the formation thins out and as it advances toward
    // the player -- classic difficulty ramp.
    private float currentSpeed() {
        int aliveCount = 0;
        for (Enemy e : enemies) if (e.alive) aliveCount++;
        int total = enemies.size();
        float countMultiplier = 1f;
        if (total > 0) {
            float ratio = (float) aliveCount / total;
            // as ratio -> 0, this multiplier grows up to ~3x
            countMultiplier = 1f + 2f * (1f - ratio);
        }
        float advanceMultiplier = 1f + 0.15f * (dropCount / 2);
        return baseSpeed * countMultiplier * advanceMultiplier;
    }

    public List<Projectile> update(float dt) {
        float speed = currentSpeed();
        boolean hitEdge = false;

        // check if moving would exceed the field bound
        for (Enemy e : enemies) {
            if (!e.alive) continue;
            float nextX = e.x + currentDirection * speed * dt;
            if (nextX > fieldHalfWidth || nextX < -fieldHalfWidth) {
                hitEdge = true;
                break;
            }
        }

        if (hitEdge) {
            currentDirection *= -1f;
            dropCount++;
            for (Enemy e : enemies) {
                if (e.alive) e.z += dropDistance;
            }
        } else {
            for (Enemy e : enemies) {
                if (e.alive) e.x += currentDirection * speed * dt;
            }
        }

        List<Projectile> shots = new ArrayList<>();
        fireTimer -= dt;
        if (fireTimer <= 0) {
            Enemy shooter = pickRandomShooter();
            if (shooter != null) {
                shots.add(Projectile.enemyShot(shooter.x, shooter.y, shooter.z, shooter.type));
            }
            fireTimer = nextFireDelay();
        }
        return shots;
    }

    private Enemy pickRandomShooter() {
        List<Enemy> alive = new ArrayList<>();
        for (Enemy e : enemies) if (e.alive) alive.add(e);
        if (alive.isEmpty()) return null;
        return alive.get(rng.nextInt(alive.size()));
    }

    private float nextFireDelay() {
        return fireIntervalMin + rng.nextFloat() * (fireIntervalMax - fireIntervalMin);
    }

    public boolean allDead() {
        for (Enemy e : enemies) if (e.alive) return false;
        return true;
    }

    // True if any living enemy has reached or passed the given z threshold (near player).
    public boolean anyReached(float zThreshold) {
        for (Enemy e : enemies) {
            if (e.alive && e.z >= zThreshold) return true;
        }
        return false;
    }
}