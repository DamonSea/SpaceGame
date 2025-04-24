package spacegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SpaceGame extends JFrame implements KeyListener {
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;

    // Game state management
    private enum GameState { MENU, PLAYING, GAME_OVER }
    private GameState gameState = GameState.MENU;

    // UI Components
    private JPanel gamePanel;
    private JLabel scoreLabel;
    private javax.swing.Timer timer;

    // Game entities
    private Player player;
    private Projectile projectile;
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Star> stars = new ArrayList<>();
    private List<ParticleExplosion> explosions = new ArrayList<>();

    // Input and scoring state
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean isFiring = false;
    private int score = 0;

    public SpaceGame() {
        setTitle("Space Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Setup drawing panel
        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

        // Score display
        scoreLabel = new JLabel();
        scoreLabel.setForeground(Color.WHITE);
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

        // Game loop timer (updates every 20ms)
        timer = new javax.swing.Timer(20, e -> {
            if (gameState == GameState.PLAYING) {
                update();
            }
            gamePanel.repaint();
        });
        timer.start();
    }

    // Generates random static stars for background
    private void generateStaticStars(int count) {
        stars.clear();
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            stars.add(new Star(r.nextInt(WIDTH), r.nextInt(HEIGHT)));
        }
    }

    // Updates score and health UI
    private void updateScoreLabel() {
        StringBuilder hearts = new StringBuilder();
        if (player != null) {
            for (int i = 0; i < player.getHealth(); i++) hearts.append("\u2665");
        }
        scoreLabel.setText("Score: " + score + "    Health: " + hearts);
    }

    // Main game logic: input, collision, scoring
    private void update() {
        if (player == null || projectile == null) {
            player = new Player(WIDTH / 2 - Player.WIDTH / 2, HEIGHT - Player.HEIGHT - 20);
            projectile = new Projectile();
            obstacles.clear();
            explosions.clear();
            score = 0;
            updateScoreLabel();
            return;
        }

        player.updateStatus();
        if (leftPressed) player.moveLeft();
        if (rightPressed) player.moveRight(WIDTH);

        projectile.update();

        // Obstacle updates and collisions
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
                score += 10;
            }
        }

        // Update and remove finished explosions
        for (Iterator<ParticleExplosion> peIt = explosions.iterator(); peIt.hasNext(); ) {
            ParticleExplosion p = peIt.next();
            p.update();
            if (!p.isActive()) peIt.remove();
        }

        // Random enemy spawn
        if (Math.random() < 0.02) {
            int x = new Random().nextInt(WIDTH - Obstacle.WIDTH);
            obstacles.add(new Obstacle(x));
        }

        updateScoreLabel();
    }

    // Draws all game states and UI
    private void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (Star star : stars) {
            star.twinkle();
            g.setColor(star.getColor());
            g.fillOval(star.x, star.y, 2, 2);
        }

        if (gameState == GameState.MENU) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fm = g.getFontMetrics();
            String title = "SPACE GAME";
            String start = "Press ENTER to Start";
            String instructions = "WASD / Arrows to Move, W / Up to Shoot";
            g.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, HEIGHT / 2 - 40);
            g.drawString(start, (WIDTH - fm.stringWidth(start)) / 2, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString(instructions, (WIDTH - g.getFontMetrics().stringWidth(instructions)) / 2, HEIGHT / 2 + 40);
            return;
        }

        if (player != null) player.draw(g);
        if (projectile != null) projectile.draw(g);
        for (Obstacle obs : obstacles) obs.draw(g);
        for (ParticleExplosion explosion : explosions) explosion.draw(g);

        if (gameState == GameState.GAME_OVER) {
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
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Handles all key press events by game state
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (gameState == GameState.MENU && key == KeyEvent.VK_ENTER) {
            player = new Player(WIDTH / 2 - Player.WIDTH / 2, HEIGHT - Player.HEIGHT - 20);
            projectile = new Projectile();
            score = 0;
            obstacles.clear();
            explosions.clear();
            updateScoreLabel();
            gameState = GameState.PLAYING;
            return;
        }

        if (gameState == GameState.GAME_OVER && key == KeyEvent.VK_R) {
            player = null;
            projectile = null;
            score = 0;
            obstacles.clear();
            explosions.clear();
            updateScoreLabel();
            gameState = GameState.MENU;
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
                    else if (rightPressed) player.dashRight(WIDTH);
                }
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (!isFiring) {
                    projectile.fire(player.getX() + Player.WIDTH / 2 - Projectile.WIDTH / 2, player.getY());
                    isFiring = true;
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignored) {}
                        isFiring = false;
                    }).start();
                }
                break;
        }
    }

    // Tracks key releases
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) rightPressed = false;
    }

    // Launches game window
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpaceGame().setVisible(true));
    }
}
