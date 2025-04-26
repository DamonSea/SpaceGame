// File: SpaceGame.java

package spacegame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.*;

/**
 * Main game window and loop for Space Game.
 * Handles rendering, input, game states, and updates.
 */
public class SpaceGame extends JFrame implements KeyListener {

    // Constants for window size
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;

    // Different game states
    private enum GameState { MENU, PLAYING, GAME_OVER }
    private GameState gameState = GameState.MENU;

    private final JPanel gamePanel;
    private final JLabel scoreLabel;
    private final javax.swing.Timer timer;

    // Core game entities
    private Player player;
    private BufferedImage shipImage;
    private BufferedImage spriteSheet;
    private Projectile projectile;

    // Visual effect when dashing
    private static class AfterImage {
        int x, y;
        float alpha;
        AfterImage(int x, int y) {
            this.x = x;
            this.y = y;
            this.alpha = 1.0f;
        }
    }

    // Collections for game objects and effects
    private final List<AfterImage> afterImages = new ArrayList<>();
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Star> stars = new ArrayList<>();
    private final List<ParticleExplosion> explosions = new ArrayList<>();

    // Input flags
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean isFiring = false;
    private int dashTrailFramesLeft = 0;
    private int score = 0;

    public SpaceGame() {
        try {
            shipImage = ImageIO.read(new File("spacegame/Asteroid Destroyer.png"));
            spriteSheet = ImageIO.read(new File("spacegame/AngryGuy.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setTitle("Space Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Initialize main panel with custom paint
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

        // Display score and health
        scoreLabel = new JLabel();
        scoreLabel.setForeground(Color.GREEN);
        scoreLabel.setOpaque(true);
        scoreLabel.setBackground(Color.BLACK);
        scoreLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        gamePanel.add(scoreLabel);

        add(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.addKeyListener(this);
        gamePanel.requestFocusInWindow();

        generateStaticStars(200);
        updateScoreLabel();

        // Game loop timer
        timer = new javax.swing.Timer(20, e -> {
            if (gameState == GameState.PLAYING) {
                update();
            }
            gamePanel.repaint();
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

    private void updateScoreLabel() {
        StringBuilder hearts = new StringBuilder();
        if (player != null) {
            for (int i = 0; i < player.getHealth(); i++) hearts.append("\u2665");
        }
        scoreLabel.setText("Score: " + score + "    Health: " + hearts);
    }

    /**
     * Main update loop for game logic.
     */
    private void update() {
        if (player == null || projectile == null) {
            initializeGameObjects();
            return;
        }

        player.updateStatus();

        if (dashTrailFramesLeft > 0) {
            afterImages.add(new AfterImage(player.getX(), player.getY()));
            dashTrailFramesLeft--;
        }

        handleMovementInput();
        projectile.update();
        updateObstacles();
        updateExplosions();
        spawnObstaclesRandomly();
        updateScoreLabel();
    }

    private void initializeGameObjects() {
        player = new Player(WIDTH / 2 - Player.WIDTH / 2, HEIGHT - Player.HEIGHT - 20);
        projectile = new Projectile();
        obstacles.clear();
        explosions.clear();
        score = 0;
        updateScoreLabel();
    }

    private void handleMovementInput() {
        if (leftPressed) player.moveLeft();
        if (rightPressed) player.moveRight(WIDTH);
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
                if (player.getHealth() <= 0) gameState = GameState.GAME_OVER;
                it.remove();
                continue;
            }
            if (projectile.isVisible() && projectile.getBounds().intersects(o.getBounds())) {
                explosions.add(new ParticleExplosion(o.getBounds().x + Obstacle.WIDTH / 2, o.getBounds().y + Obstacle.HEIGHT / 2));
                projectile.hide();
                it.remove();
                playPopSound();
                score += 10;
            }
        }
    }

    private void updateExplosions() {
        for (Iterator<ParticleExplosion> peIt = explosions.iterator(); peIt.hasNext(); ) {
            ParticleExplosion p = peIt.next();
            p.update();
            if (!p.isActive()) peIt.remove();
        }
    }

    private void spawnObstaclesRandomly() {
        if (Math.random() < 0.02) {
            int x = new Random().nextInt(WIDTH - Obstacle.WIDTH);
            obstacles.add(new Obstacle(x, spriteSheet));
        }
    }

    private void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        drawStars(g);

        if (gameState == GameState.MENU) {
            drawMenu(g);
            return;
        }

        drawPlayerAndEffects(g);
        drawProjectilesAndObstacles(g);

        if (gameState == GameState.GAME_OVER) {
            drawGameOverScreen(g);
        }
    }

    private void drawStars(Graphics g) {
        for (Star star : stars) {
            star.twinkle();
            g.setColor(star.getColor());
            g.fillOval(star.x, star.y, 2, 2);
        }
    }

    private void drawMenu(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String title = "STAR FIRE";
        String start = "Press ENTER to Start";
        String instructions = "WASD / Arrows to Move, W / Up to Shoot";

        g.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, HEIGHT / 2 - 40);
        g.drawString(start, (WIDTH - fm.stringWidth(start)) / 2, HEIGHT / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString(instructions, (WIDTH - g.getFontMetrics().stringWidth(instructions)) / 2, HEIGHT / 2 + 40);
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

    private void drawProjectilesAndObstacles(Graphics g) {
        if (projectile != null) projectile.draw(g);
        for (Obstacle obs : obstacles) obs.draw(g);
        for (ParticleExplosion explosion : explosions) explosion.draw(g);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String msg = "GAME OVER";
        String scoreMsg = "Final Score: " + score;
        String restart = "Press R to Restart";
        g.drawString(msg, (WIDTH - fm.stringWidth(msg)) / 2, HEIGHT / 2 - 40);
        g.drawString(scoreMsg, (WIDTH - fm.stringWidth(scoreMsg)) / 2, HEIGHT / 2);
        g.drawString(restart, (WIDTH - fm.stringWidth(restart)) / 2, HEIGHT / 2 + 40);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        handleKeyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) rightPressed = false;
    }

    private void handleKeyPressed(int key) {
        if (gameState == GameState.MENU && key == KeyEvent.VK_ENTER) {
            initializeGameObjects();
            gameState = GameState.PLAYING;
            return;
        }

        if (gameState == GameState.GAME_OVER && key == KeyEvent.VK_R) {
            player = null;
            projectile = null;
            obstacles.clear();
            explosions.clear();
            score = 0;
            gameState = GameState.MENU;
            updateScoreLabel();
            return;
        }

        if (gameState != GameState.PLAYING) return;

        switch (key) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                leftPressed = true;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                rightPressed = true;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (player.canDash()) {
                    if (leftPressed) player.dashLeft();
                    if (rightPressed) player.dashRight(WIDTH);
                    dashTrailFramesLeft = 6;
                }
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                fireProjectileIfPossible();
                break;
        }
    }

    private void fireProjectileIfPossible() {
        if (!isFiring) {
            projectile.fire(player.getX() + Player.WIDTH / 2 - Projectile.WIDTH / 2, player.getY());
            playSound();
            isFiring = true;
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
                isFiring = false;
            }).start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpaceGame().setVisible(true));
    }

    public void playSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/spacegame/pewpew.wav")
            );
            Clip newClip = AudioSystem.getClip();
            newClip.open(audioInputStream);
            newClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playPopSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/spacegame/poppy.wav")
            );
            Clip newClip = AudioSystem.getClip();
            newClip.open(audioInputStream);
            newClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
