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

            // Bunkers block shots from either direction before anything
            // behind them gets a chance to be hit.
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
            if (p.intersects(b)) {
                b.hit();
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