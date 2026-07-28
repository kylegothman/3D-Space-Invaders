package invaders.game;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    public interface InputSource {
        boolean isLeftPressed();
        boolean isRightPressed();
        boolean isFirePressed();
        // True while the player is pressing whatever "restart" action the UI exposes
        // (e.g. a button press or a specific key) after a game-over.
        boolean isRestartPressed();
    }

    private static final float FIXED_DT = 1f / 60f;
    private static final float PLAYER_Z = 9.0f;
    private static final float ENEMY_DANGER_Z = 6.0f;
    private static final float PROJECTILE_Z_MIN = -20f;
    private static final float PROJECTILE_Z_MAX = 20f;

    // Bunkers sit between the formation's danger line and the player, so
    // they naturally get bypassed once enemies cross ENEMY_DANGER_Z.
    private static final int BUNKER_COUNT = 4;
    private static final float BUNKER_Z = 4.5f;
    private static final float BUNKER_SPACING_X = 3.0f; // gaps between bunkers

    public Player player;
    public EnemyFormation formation;
    public final List<Bunker> bunkers = new ArrayList<>();
    public final List<Projectile> projectiles = new ArrayList<>();
    public final ScoreManager score = new ScoreManager();
    public final CollisionSystem collisions = new CollisionSystem();

    private final InputSource input;
    private float accumulator = 0f;

    public GameLogic(InputSource input) {
        this.input = input;
        this.player = new Player(0f, 0f, PLAYER_Z);
        this.formation = buildFormation();
        buildBunkers();
    }

    private EnemyFormation buildFormation() {
        return new EnemyFormation(
                /* rows */ 4, /* cols */ 8,
                /* spacingX */ 2f, /* spacingZ */ 1.5f,
                /* originX */ -5.25f, /* originY */ 0f, /* originZ */ -12f,
                /* fieldHalfWidth */ 9f,
                /* baseSpeed */ 1.5f,
                /* dropDistance */ 0.6f);
    }

    private void buildBunkers() {
        bunkers.clear();
        float startX = -((BUNKER_COUNT - 1) * BUNKER_SPACING_X) / 2f;
        for (int i = 0; i < BUNKER_COUNT; i++) {
            bunkers.add(new Bunker(startX + i * BUNKER_SPACING_X, 0f, BUNKER_Z));
        }
    }

    public void update(float frameDeltaSeconds) {
        if (score.getState() != ScoreManager.State.PLAYING) {
            // Game is over (won or lost, e.g. player was hit 3 times and ran
            // out of lives) -- offer the player the option to restart.
            if (input.isRestartPressed()) {
                restart();
            }
            return;
        }

        accumulator += frameDeltaSeconds;
        if (accumulator > 0.25f) accumulator = 0.25f;

        while (accumulator >= FIXED_DT) {
            step(FIXED_DT);
            accumulator -= FIXED_DT;
        }
    }

    private void step(float dt) {
        pollInput();

        List<Projectile> newShots = player.tick(dt);
        projectiles.addAll(newShots);

        List<Projectile> enemyShots = formation.update(dt);
        projectiles.addAll(enemyShots);

        for (Projectile p : projectiles) {
            p.update(dt);
        }

        CollisionSystem.CollisionResult result =
                collisions.resolve(player, formation, bunkers, projectiles);
        if (result.scoreGained > 0) score.addScore(result.scoreGained);

        collisions.cullProjectiles(projectiles, PROJECTILE_Z_MIN, PROJECTILE_Z_MAX);

        if (!player.alive) {
            score.setLost();
        } else if (formation.anyReached(ENEMY_DANGER_Z)) {
            score.setLost();
        } else if (result.formationCleared) {
            score.setWon();
        }
    }

    private void pollInput() {
        player.moveLeft  = input.isLeftPressed();
        player.moveRight = input.isRightPressed();
        player.firing    = input.isFirePressed();
    }

    public boolean isGameOver() {
        return score.getState() != ScoreManager.State.PLAYING;
    }

    public List<Entity> getRenderables() {
        List<Entity> all = new ArrayList<>();
        if (player.alive) all.add(player);
        for (Enemy e : formation.enemies) if (e.alive) all.add(e);
        for (Bunker b : bunkers) if (b.alive) all.add(b);
        for (Projectile p : projectiles) if (p.alive) all.add(p);
        return all;
    }

    public void restart() {
        player = new Player(0f, 0f, PLAYER_Z);
        formation = buildFormation();
        buildBunkers();
        projectiles.clear();
        score.reset();
        accumulator = 0f;
    }
}