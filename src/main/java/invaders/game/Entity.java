package invaders.game;

public class Entity {

    public float x, y, z;
    public float vx, vy, vz;      // velocity, units/second
    public float scale = 1.0f;

    // bounding box for collision (axis-aligned, centered on x,y,z)
    public float halfWidth  = 0.5f;
    public float halfHeight = 0.5f;
    public float halfDepth  = 0.5f;
    
    public boolean alive = true;

    public String modelId;

    public Entity(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Advances position by velocity. Call once per fixed update tick.
    public void update(float dt) {
        x += vx * dt;
        y += vy * dt;
        z += vz * dt;
    }

    public boolean intersects(Entity other) {
        return Math.abs(this.x - other.x) <= (this.halfWidth  + other.halfWidth)
            && Math.abs(this.y - other.y) <= (this.halfHeight + other.halfHeight)
            && Math.abs(this.z - other.z) <= (this.halfDepth  + other.halfDepth);
    }
}