package spacegame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player implements GameObject {
    public static final int WIDTH = 40;
    public static final int HEIGHT = 40;

    private int x, y;
    private int health = 3;
    private boolean canDash = true;

    private BufferedImage sprite;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Allow GamePanel to pass in the sprite
    public void setSprite(BufferedImage sprite) {
        this.sprite = sprite;
    }

    public void moveLeft() {
        x -= 5;
        if (x < 0) x = 0;
    }

    public void moveRight(int panelWidth) {
        x += 5;
        if (x + WIDTH > panelWidth) x = panelWidth - WIDTH;
    }

    public void dashLeft() {
        x -= 50;
        canDash = false;
    }

    public void dashRight(int panelWidth) {
        x += 50;
        if (x + WIDTH > panelWidth) x = panelWidth - WIDTH;
        canDash = false;
    }

    public boolean canDash() {
        return canDash;
    }

    public void updateStatus() {
        // Simple cooldown reset logic for now (can be expanded later)
        canDash = true;
    }

    public void takeDamage() {
        health--;
    }

    public int getHealth() {
        return health;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public void update() {
        updateStatus();
    }

    @Override
    public void draw(Graphics g) {
        if (sprite != null) {
            g.drawImage(sprite, x, y, WIDTH, HEIGHT, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(x, y, WIDTH, HEIGHT);
        }
    }
}
