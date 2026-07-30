package invaders.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemyFormation {

    public final List<Enemy> enemies = new ArrayList<>();

    private final float fieldHalfWidth;
    private final float dropDistance;
    private final float baseSpeed;
    private float currentDirection = 1f;
    private int dropCount = 0;
    private final Random rng = new Random();

    private float fireTimer = 0f;
    private final float fireIntervalMin = 0.36f;
    private final float fireIntervalMax = 1.08f;

    private final float shotXJitter = 0.35f;

    private final float shotYJitter = 0.3f;
    
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

    private static final float MAX_SPEED_MULTIPLIER = 3.0f;

    private float currentSpeed() {
        int aliveCount = 0;
        for (Enemy e : enemies) if (e.alive) aliveCount++;
        int total = enemies.size();
        float countMultiplier = 1f;
        if (total > 0) {
            float ratio = (float) aliveCount / total;
            countMultiplier = 1f + 2f * (1f - ratio);
        }

        int rampedDrops = Math.max(0, dropCount - 3);
        float advanceMultiplier = 1f + 0.08f * (rampedDrops / 2);

        float totalMultiplier = Math.min(countMultiplier * advanceMultiplier, MAX_SPEED_MULTIPLIER);
        return baseSpeed * totalMultiplier;
    }

    public List<Projectile> update(float dt) {
        float speed = currentSpeed();
        boolean hitEdge = false;

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
                float jitterX = (rng.nextFloat() * 2f - 1f) * shotXJitter;
                float jitterY = (rng.nextFloat() * 2f - 1f) * shotYJitter;
                shots.add(Projectile.enemyShot(shooter.x + jitterX, shooter.y + jitterY, shooter.z, shooter.type));
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

    public boolean anyReached(float zThreshold) {
        for (Enemy e : enemies) {
            if (e.alive && e.z >= zThreshold) return true;
        }
        return false;
    }
}