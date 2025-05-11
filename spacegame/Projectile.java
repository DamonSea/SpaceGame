package spacegame;

import java.awt.*;
import spacegame.GameObject;


public class Projectile implements GameObject {

    // Projectile size constants
    public static final int WIDTH = 5; // Default Value is 5
    public static final int HEIGHT = 10; // Default Value is 10
    private static final int SPEED = 10; // Upward movement speed, DF is 10

    private int x, y;
    private boolean visible = false; // Visibility state

    // Fires the projectile from a given starting position
    public void fire(int startX, int startY) {
        if (!visible) {
            this.x = startX;
            this.y = startY;
            visible = true;
        }
    }

    // Update projectile position each frame
    public void update() {
        if (visible) {
            y -= SPEED;
            if (y < 0) visible = false; // Hide if off screen
        }
    }

    // Draw projectile if visible
    public void draw(Graphics g) {
        if (visible) {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, WIDTH, HEIGHT);
        }
    }

    // Check if projectile is currently visible
    public boolean isVisible() {
        return visible;
    }

    // Hide the projectile manually
    public void hide() {
        visible = false;
    }

    // Return bounding box for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}