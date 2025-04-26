package spacegame;

import java.awt.*;

public class Player {

    // Player Constants //

    // Player Dimensions
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;

    // Player Speed
    private static final int SPEED = 5; // Default Value: 5

    // Dash Properties
    private static final int DASH_DISTANCE = 100;
    private static final long DASH_COOLDOWN_MS = 3000;

    private int x, y;

    // Player Health Points
    private int health = 3;

    // Invincibility
    private boolean invincible = false;
    private long invincibilityEnd = 0;

    // Dash
    private boolean canDash = true;
    private long dashCooldownEnd = 0;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void moveLeft() {
        if (x > 0) x -= SPEED;
    }

    public void moveRight(int boundary) {
        if (x < boundary - WIDTH) x += SPEED;
    }

    public void dashLeft() {
        if (canDash) {
            x = Math.max(0, x - DASH_DISTANCE);
            canDash = false;
            dashCooldownEnd = System.currentTimeMillis() + DASH_COOLDOWN_MS;
        }
    }

    public void dashRight(int boundary) {
        if (canDash) {
            x = Math.min(boundary - WIDTH, x + DASH_DISTANCE);
            canDash = false;
            dashCooldownEnd = System.currentTimeMillis() + DASH_COOLDOWN_MS;
        }
    }

    public void updateStatus() {
        if (invincible && System.currentTimeMillis() > invincibilityEnd) {
            invincible = false;
        }
        if (!canDash && System.currentTimeMillis() > dashCooldownEnd) {
            canDash = true;
        }
    }

    public void takeDamage() {
        if (!invincible) {
            health--;
            invincible = true;
            invincibilityEnd = System.currentTimeMillis() + 1000;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public boolean isInvincible() { return invincible; }
    public boolean canDash() { return canDash; }

    // Draw Method: Graphics
    public void draw(Graphics g) {
        if (!invincible || (System.currentTimeMillis() / 100 % 2 == 0)) {
            g.setColor(Color.BLUE);
            int noseX = x + WIDTH / 2;
            int noseY = y;
            int leftWingX = x;
            int leftWingY = y + HEIGHT;
            int rightWingX = x + WIDTH;
            int rightWingY = y + HEIGHT;
            int tailNotchX = x + WIDTH / 2;
            int tailNotchY = y + HEIGHT - 10;
            int[] xPoints = {noseX, rightWingX, tailNotchX, leftWingX};
            int[] yPoints = {noseY, rightWingY, tailNotchY, leftWingY};
            g.fillPolygon(xPoints, yPoints, 4);
        }
    }
}
