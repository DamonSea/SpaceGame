package spacegame;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel {

    // Game constants
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;

    // Input Manager
    private final InputManager input = new InputManager();

    // Game state
    private enum GameState { MENU, PLAYING, GAME_OVER }
    private GameState gameState = GameState.MENU;

    // Game objects and visuals
    private Player player;
    private Projectile projectile;
    private BufferedImage shipImage;
    private BufferedImage spriteSheet;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Star> stars = new ArrayList<>();
    private final List<ParticleExplosion> explosions = new ArrayList<>();
    private final List<AfterImage> afterImages = new ArrayList<>();


    private final HUD hud = new HUD();
    private final List<GameObject> gameObjects = new ArrayList<>();


    // UI
    private final Timer timer;
    private int score = 0;
    private long startTime, elapsedTime;

    // Input state
    private boolean isFiring = false;
    private int dashTrailFramesLeft = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        addKeyListener(input);
        setLayout(null);
        add(hud.getLabel());

        try {
            shipImage = ImageIO.read(new File("Images/Asteroid Destroyer.png"));
            spriteSheet = ImageIO.read(new File("Images/AngryGuy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        generateStaticStars(200);
        updateHUD();

        timer = new Timer(20, e -> {
            update();
            repaint();
        });
        timer.start();

    }

    private void generateStaticStars(int count) {
        stars.clear();
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            stars.add(new Star(r.nextInt(WIDTH), r.nextInt(HEIGHT)));
        }
    }

    private void updateHUD() {
        int health = (player != null) ? player.getHealth() : 0;
        long time = (gameState == GameState.PLAYING)
                ? (System.currentTimeMillis() - startTime)
                : elapsedTime;
        hud.update(score, health, time);
    }


    private void update() {

        if (player == null || projectile == null) {
            initializeGameObjects();
            return;
        }

        if (gameState == GameState.MENU && input.isEnterPressed()) {
            initializeGameObjects();
            startTime = System.currentTimeMillis();
            gameState = GameState.PLAYING;
            input.resetOneTimeActions();
        }

        if (gameState == GameState.GAME_OVER && input.isRestartPressed()) {
            player = null;
            projectile = null;
            obstacles.clear();
            explosions.clear();
            score = 0;
            elapsedTime = 0;
            gameState = GameState.MENU;
            updateHUD();
            input.resetOneTimeActions();
        }

        for (GameObject obj : gameObjects) {
            obj.update();
        }

        if (dashTrailFramesLeft > 0) {
            afterImages.add(new AfterImage(player.getX(), player.getY()));
            dashTrailFramesLeft--;
        }

        handleInput();
        updateObstacles();
        updateExplosions();
        spawnObstaclesRandomly();
        updateHUD();

        // Fire projectile
        if (input.isFirePressed()) {
            fireProjectileIfPossible();
        }

        // Dash input
        if (input.isDashPressed() && player != null && player.canDash()) {
            if (input.isLeftPressed()) player.dashLeft();
            if (input.isRightPressed()) player.dashRight(WIDTH);
            dashTrailFramesLeft = 6;
        }

    }



    private void initializeGameObjects() {
        player = new Player(WIDTH / 2 - Player.WIDTH / 2, HEIGHT - Player.HEIGHT - 20);

//        player.setSprite(shipImage); // Adds Ship Image

        projectile = new Projectile();
        obstacles.clear();
        explosions.clear();
        gameObjects.clear();

        gameObjects.add(player);
        gameObjects.add(projectile);
        gameObjects.addAll(obstacles); // (this will be updated dynamically later)
        score = 0;
        updateHUD();

    }

    private void handleInput() {
        if (input.isLeftPressed()) player.moveLeft();
        if (input.isRightPressed()) player.moveRight(WIDTH);
    }

    private void updateObstacles() {
        Iterator<Obstacle> it = obstacles.iterator();
        while (it.hasNext()) {
            Obstacle o = it.next();
            o.update();
            if (o.isOffScreen(HEIGHT)) {
                it.remove();
                continue;
            }
            if (player.getBounds().intersects(o.getBounds())) {
                explosions.add(new ParticleExplosion(player.getX() + Player.WIDTH / 2, player.getY() + Player.HEIGHT / 2));
                player.takeDamage();
                if (player.getHealth() <= 0) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                    gameState = GameState.GAME_OVER;
                }
                it.remove();
                continue;
            }
            if (projectile.isVisible() && projectile.getBounds().intersects(o.getBounds())) {
                explosions.add(new ParticleExplosion(o.getX(), o.getY()));
                projectile.hide();
                it.remove();
                playPopSound();
                score += 10;
            }
        }
    }

    private void updateExplosions() {
        for (Iterator<ParticleExplosion> it = explosions.iterator(); it.hasNext(); ) {
            ParticleExplosion p = it.next();
            p.update();
            if (!p.isActive()) it.remove();
        }
    }

    private void spawnObstaclesRandomly() {
        if (Math.random() < 0.02) {
            int x = new Random().nextInt(WIDTH - Obstacle.WIDTH);

            Obstacle o = new Obstacle(x, spriteSheet);
            obstacles.add(o);
            gameObjects.add(o);

        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (Star s : stars) {
            s.twinkle(HEIGHT);
            g.setColor(s.getColor());
            g.fillOval(s.getX(), s.getY(), 2, 2);
        }


        if (gameState == GameState.MENU) {
            drawMenu(g);
            return;
        }

        drawPlayerAndEffects(g);
        for (GameObject obj : gameObjects) {
            obj.draw(g);
        }

        for (ParticleExplosion pe : explosions) pe.draw(g);

        if (gameState == GameState.GAME_OVER) drawGameOver(g);
    }

    private void drawMenu(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();

        String title = "STAR FIRE";
        int y = HEIGHT / 2;

        // Draw title
        g.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, y - 40);

        // Draw instructions
        String[] lines = {
                "Press RETURN to Start",
                "WASD / Arrows to Move",
                "W / Up to Shoot",
                "S / Down + Direction to Rainbow Dash!"
        };
        for (String line : lines) {
            g.drawString(line, (WIDTH - fm.stringWidth(line)) / 2, y);
            y += 30;
        }
    }


    private void drawPlayerAndEffects(Graphics g) {
        if (player != null && shipImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            for (Iterator<AfterImage> it = afterImages.iterator(); it.hasNext(); ) {
                AfterImage a = it.next();
                Composite original = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a.alpha));
                float hue = (1.0f - a.alpha) * 0.8f;
                g2d.setColor(Color.getHSBColor(hue, 1.0f, 1.0f));
                g2d.fillRect(a.x, a.y, Player.WIDTH, Player.HEIGHT);
                g2d.setComposite(original);
                a.alpha -= 0.1f;
                if (a.alpha <= 0) it.remove();
            }
            g.drawImage(shipImage, player.getX(), player.getY(), null);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String msg = "GAME OVER";
        String scoreMsg = "Final Score: " + score;
        String timeMsg = "You survived: " + (elapsedTime / 1000) + " seconds";
        String restart = "Press R to Restart";

        g.drawString(msg, (WIDTH - fm.stringWidth(msg)) / 2, HEIGHT / 2 - 60);
        g.drawString(scoreMsg, (WIDTH - fm.stringWidth(scoreMsg)) / 2, HEIGHT / 2 - 20);
        g.drawString(timeMsg, (WIDTH - fm.stringWidth(timeMsg)) / 2, HEIGHT / 2 + 20);
        g.drawString(restart, (WIDTH - fm.stringWidth(restart)) / 2, HEIGHT / 2 + 60);
    }

    private void fireProjectileIfPossible() {
        if (!isFiring && player != null) {
            projectile.fire(player.getX() + Player.WIDTH / 2 - Projectile.WIDTH / 2, player.getY());
            playFireSound();
            isFiring = true;
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
                isFiring = false;
            }).start();
        }
    }

    private void playFireSound() {
        SoundPlayer.play("/Sounds/pewpew.wav");
    }

    private void playPopSound() {
        SoundPlayer.play("/Sounds/poppy.wav");
    }


    // Inner class to manage dash afterimages
    private static class AfterImage {
        int x, y;
        float alpha;
        AfterImage(int x, int y) {
            this.x = x;
            this.y = y;
            this.alpha = 1.0f;
        }
    }
}
