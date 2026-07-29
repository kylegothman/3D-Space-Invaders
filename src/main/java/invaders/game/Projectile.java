package invaders.game;

public class Projectile extends Entity {

    public enum Owner { PLAYER, ENEMY }

    public static final float PLAYER_SPEED = 14.0f;
    public static final float ENEMY_SPEED  = 6.0f;

    public final Owner owner;

    private Projectile(float x, float y, float z, Owner owner) {
        super(x, y, z);
        this.owner = owner;
        this.halfWidth = 0.08f;
        this.halfHeight = 0.08f;
        this.halfDepth = 0.2f;
        this.modelId = owner == Owner.PLAYER ? "player_bolt" : "enemy_bolt";
    }

    public static Projectile playerShot(float x, float y, float z) {
        Projectile p = new Projectile(x, y, z, Owner.PLAYER);
        p.vz = -PLAYER_SPEED; // travels toward enemies
        return p;
    }

    public static Projectile enemyShot(float x, float y, float z) {
        Projectile p = new Projectile(x, y, z, Owner.ENEMY);
        p.vz = ENEMY_SPEED; // travels toward player
        return p;
    }
}