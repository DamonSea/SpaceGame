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

    private JPanel gamePanel;
    private JLabel scoreLabel;
    private Timer timer;

    private Player player;
    private Projectile projectile;
    private List<Obstacle> obstacles = new ArrayList<>();
    private List<Star> stars = new ArrayList<>();
    private List<ParticleExplosion> explosions = new ArrayList<>();

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean isFiring = false;
    private boolean isGameOver = false;
    private int score = 0;

    public SpaceGame() {
        setTitle("Space Game");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                draw(g);
            }
        };

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

        resetGame();

        timer = new Timer(20, e -> {
            if (!isGameOver) {
                update();
                gamePanel.repaint();
            }
        });
        timer.start();
    }

    private void resetGame() {
        score = 0;
        isGameOver = false;
        leftPressed = false;
        rightPressed = false;
        obstacles.clear();
        explosions.clear();
        generateStaticStars(200);

        player = new Player(WIDTH / 2 - Player.WIDTH / 2, HEIGHT - Player.HEIGHT - 20);
        projectile = new Projectile();

        updateScoreLabel();
    }

    private void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        for (Star star : stars) {
            star.twinkle();
            g.setColor(star.getColor());
            g.fillOval(star.x, star.y, 2, 2);
        }

        player.draw(g);
        projectile.draw(g);

        for (Obstacle obs : obstacles) {
            obs.draw(g);
        }

        for (ParticleExplosion explosion : explosions) {
            explosion.draw(g);
        }

        if (isGameOver) {
            g.setColor(Color.WHITE);
            Font font = new Font("Arial", Font.BOLD, 24);
            g.setFont(font);
            FontMetrics fm = g.getFontMetrics(font);
            String gameOverText = "Game Over!";
            String restartText = "Press R to Restart";
            int x1 = (WIDTH - fm.stringWidth(gameOverText)) / 2;
            int y1 = HEIGHT / 2;
            int x2 = (WIDTH - fm.stringWidth(restartText)) / 2;
            int y2 = y1 + 40;
            g.drawString(gameOverText, x1, y1);
            g.drawString(restartText, x2, y2);
        }
    }

    private void update() {
        player.updateStatus();

        if (leftPressed) player.moveLeft();
        if (rightPressed) player.moveRight(WIDTH);

        projectile.update();

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
                    isGameOver = true;
                }
                it.remove();
                continue;
            }
            if (projectile.isVisible() && projectile.getBounds().intersects(o.getBounds())) {
                explosions.add(new ParticleExplosion(o.getBounds().x + Obstacle.WIDTH / 2, o.getBounds().y + Obstacle.HEIGHT / 2));
                projectile.hide();
                it.remove();
                score += 10;
                continue;
            }
        }

        for (Iterator<ParticleExplosion> peIt = explosions.iterator(); peIt.hasNext(); ) {
            ParticleExplosion p = peIt.next();
            p.update();
            if (!p.isActive()) peIt.remove();
        }

        if (Math.random() < 0.02) {
            int x = new Random().nextInt(WIDTH - Obstacle.WIDTH);
            obstacles.add(new Obstacle(x));
        }

        updateScoreLabel();
    }

    private void updateScoreLabel() {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < player.getHealth(); i++) hearts.append("\u2665");
        scoreLabel.setText("Score: " + score + "    Health: " + hearts);
    }

    private void generateStaticStars(int count) {
        stars.clear();
        Random r = new Random();
        for (int i = 0; i < count; i++) {
            stars.add(new Star(r.nextInt(WIDTH), r.nextInt(HEIGHT)));
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (isGameOver && key == KeyEvent.VK_R) {
            resetGame();
            return;
        }

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

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SpaceGame().setVisible(true));
    }
}
