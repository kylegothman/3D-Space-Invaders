package invaders.model;

public final class Models {

    private Models() {}

    private static final float UNIT  = 0.11f;
    private static final float DEPTH = 0.18f;

    // Each invader has two poses to make aliens appear to flap.

    public static VoxelModel[] invaderTopFrames() {
        String[] a = {
            "...##...",
            "..####..",
            ".######.",
            "##.##.##",
            "########",
            "..#..#..",
            ".#.##.#.",
            "#.#..#.#",
        };
        String[] b = {
            "...##...",
            "..####..",
            ".######.",
            "##.##.##",
            "########",
            ".#.##.#.",
            "#......#",
            ".#....#.",
        };
        return new VoxelModel[] {
            new VoxelModel(a, UNIT, DEPTH, 0.95f, 0.95f, 0.95f),
            new VoxelModel(b, UNIT, DEPTH, 0.95f, 0.95f, 0.95f),
        };
    }

    public static VoxelModel[] invaderMidFrames() {
        String[] a = {
            "..#.....#..",
            "...#...#...",
            "..#######..",
            ".##.###.##.",
            "###########",
            "#.#######.#",
            "#.#.....#.#",
            "...##.##...",
        };
        String[] b = {
            "..#.....#..",
            "#..#...#..#",
            "#.#######.#",
            "###.###.###",
            "###########",
            ".#########.",
            "..#.....#..",
            ".#.......#.",
        };
        return new VoxelModel[] {
            new VoxelModel(a, UNIT, DEPTH, 0.90f, 0.95f, 1.0f),
            new VoxelModel(b, UNIT, DEPTH, 0.90f, 0.95f, 1.0f),
        };
    }

    public static VoxelModel[] invaderBottomFrames() {
        String[] a = {
            "...#####...",
            ".#########.",
            "###########",
            "###.###.###",
            "###########",
            "...##.##...",
            "..##.#.##..",
            "##.......##",
        };
        String[] b = {
            "...#####...",
            ".#########.",
            "###########",
            "###.###.###",
            "###########",
            "..##.#.##..",
            ".#.##.##.#.",
            "#.#.....#.#",
        };
        return new VoxelModel[] {
            new VoxelModel(a, UNIT, DEPTH, 0.85f, 0.90f, 0.98f),
            new VoxelModel(b, UNIT, DEPTH, 0.85f, 0.90f, 0.98f),
        };
    }

    public static VoxelModel playerShip() {
        String[] bmp = {
            "......#......",
            ".....###.....",
            ".....###.....",
            ".###########.",
            "#############",
            "#############",
        };
        return new VoxelModel(bmp, 0.11f, 0.22f, 0.20f, 0.90f, 0.40f);
    }

    // Scattered debris shown in place of the ship when it is destroyed.
    public static VoxelModel shipExplosion() {
        String[] bmp = {
            "...#...#..#..",
            ".#...#...#...",
            "..#..#.#..#..",
            ".#.#######.#.",
            "#.##########.",
            ".############",
        };
        return new VoxelModel(bmp, 0.11f, 0.22f, 0.20f, 0.90f, 0.40f);
    }

    public static VoxelModel ufo() {
        String[] bmp = {
            "....#####....",
            "..#########..",
            ".###########.",
            "#.#.#.#.#.#.#",
            "..##.....##..",
        };
        return new VoxelModel(bmp, 0.11f, 0.20f, 0.95f, 0.25f, 0.25f);
    }

    // Player laser: a straight bolt.
    public static VoxelModel playerBolt() {
        String[] bmp = { "#", "#", "#", "#" };
        return new VoxelModel(bmp, 0.09f, 0.09f, 1.0f, 0.20f, 0.20f);
    }

    // Alien cross shot; two frames that alternate as it falls.
    public static VoxelModel[] alienBoltCross() {
        String[] f0 = {
            ".#.",
            "###",
            ".#.",
            ".#.",
            ".#.",
        };
        String[] f1 = {
            ".#.",
            ".#.",
            "###",
            ".#.",
            ".#.",
        };
        return new VoxelModel[] {
            new VoxelModel(f0, 0.09f, 0.09f, 0.95f, 0.95f, 0.95f),
            new VoxelModel(f1, 0.09f, 0.09f, 0.95f, 0.95f, 0.95f),
        };
    }

    // Alien zigzag shot; two frames that make it wiggle.
    public static VoxelModel[] alienBoltZigzag() {
        String[] f0 = {
            "#..",
            ".#.",
            "..#",
            ".#.",
            "#..",
        };
        String[] f1 = {
            "..#",
            ".#.",
            "#..",
            ".#.",
            "..#",
        };
        return new VoxelModel[] {
            new VoxelModel(f0, 0.09f, 0.09f, 0.95f, 0.95f, 0.95f),
            new VoxelModel(f1, 0.09f, 0.09f, 0.95f, 0.95f, 0.95f),
        };
    }
}
