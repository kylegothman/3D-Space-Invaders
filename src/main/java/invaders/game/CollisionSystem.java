package invaders.game;

import java.util.List;

public class CollisionSystem {

    public static class CollisionResult {
        public int scoreGained = 0;
        public boolean playerWasHit = false;
        public boolean formationCleared = false;
    }

    public CollisionResult resolve(Player player, EnemyFormation formation,
                                    List<Bunker> bunkers, List<Projectile> projectiles) {
        CollisionResult result = new CollisionResult();

        for (Projectile p : projectiles) {
            if (!p.alive) continue;

            if (hitsBunker(p, bunkers)) continue;

            if (p.owner == Projectile.Owner.PLAYER) {
                for (Enemy e : formation.enemies) {
                    if (!e.alive) continue;
                    if (p.intersects(e)) {
                        e.alive = false;
                        p.alive = false;
                        result.scoreGained += e.type.points;
                        break;
                    }
                }
            } else { // enemy bolt vs player
                if (player.alive && p.intersects(player)) {
                    p.alive = false;
                    player.hit();
                    result.playerWasHit = true;
                }
            }
        }

        if (formation.allDead()) {
            result.formationCleared = true;
        }

        return result;
    }

    private boolean hitsBunker(Projectile p, List<Bunker> bunkers) {
        for (Bunker b : bunkers) {
            if (!b.alive) continue;
            // Broad-phase: is the shot even inside the bunker's bounding box?
            if (!p.intersects(b)) continue;
            // Narrow-phase: does the exact impact point land on a still-solid
            // voxel, or has that spot already been shot away? Only a solid
            // hit destroys the local pixels and absorbs the shot; a hit on an
            // existing hole lets the projectile continue on its way.
            if (b.hitAt(p.x, p.y)) {
                p.alive = false;
                return true;
            }
        }
        return false;
    }

    // Removes dead / out-of-bounds projectiles. Call after resolve() each tick.
    public void cullProjectiles(List<Projectile> projectiles, float zMin, float zMax) {
        projectiles.removeIf(p -> !p.alive || p.z < zMin || p.z > zMax);
    }
}