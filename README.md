# 3D Space Invaders — CS 4361, Team 7

3D remake of Space Invaders in Java + JOGL (OpenGL). See the proposal PDF for full scope.

## Run it

Requires JDK 17 or newer ([download](https://adoptium.net)). Nothing else to install — the Gradle wrapper fetches JOGL automatically.

```
git clone https://github.com/kylegothman/3D-Space-Invaders.git
cd 3D-Space-Invaders
./gradlew run        # Windows: gradlew.bat run
```

You should see the title menu with three spinning invaders. Press ENTER to play: clear all 3 waves to win, each wave starts faster than the last.

Controls: ARROWS / A D move, SPACE fires, ENTER starts and restarts.

IDE: open the project folder as a Gradle project (IntelliJ / Eclipse / VS Code all support this). Don't commit IDE config files — .gitignore handles it.

## Code layout

- `src/main/java/invaders/Main.java` — window bootstrap, camera, rendering, model/animation lookup
- `src/main/java/invaders/Config.java` — window and frame-rate constants
- `src/main/java/invaders/game/` — all gameplay: entities, formation, collisions, waves, scoring (`GameLogic.update` is the entry point)
- `src/main/java/invaders/model/` — voxel bitmap models (invaders, ship, barriers, bolts, UFO, explosion)
- `src/main/java/invaders/ui/` — keyboard input, menu text, HUD

## Git workflow

1. Branch off `main`: `git checkout -b yourname/what-youre-doing`
2. Commit and push to your branch, then open a pull request on GitHub.
3. Any teammate approves your PR, then you merge it. Never push straight to `main`.

CI compiles every PR on Windows, macOS, and Linux — a red X means it won't build on someone's machine, so fix it before merging.

Review a PR when asked — a quick look is fine, it just can't be your own PR.

## Who owns what

- **Kyle** — schedule, repo/merges, report & presentation
- **Samuel** — rendering: scene, models, camera, lighting, effects
- **Jacob** — gameplay: movement, enemy behavior, collisions, scoring, game loop
- **Eshwar** — assets, input/menus, testing, screenshots

## Deadlines

- **Aug 10** — presentation + working demo (submit `CS4361_ProjectPresentation_Team7`)
- **Aug 12** — final report with repo link (submit `CS4361_ProjectFinalReport_Team7`)
