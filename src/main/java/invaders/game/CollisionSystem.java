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
                        // Start death-explosion timer so Main.java renders
                        // the explosion model for ~0.5 s before removing.
                        e.deathTimer = Enemy.DEATH_DISPLAY_FRAMES;
                        p.alive = false;
                        result.scoreGained += e.type.points;
                        break;
                    }
                }
            } else {
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

    public void cullProjectiles(List<Projectile> projectiles, float zMin, float zMax) {
        projectiles.removeIf(p -> !p.alive || p.z < zMin || p.z > zMax);
    }
}
