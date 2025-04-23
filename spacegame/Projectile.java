package spacegame;

import java.awt.*;

public class Projectile {
    public static final int WIDTH = 5;
    public static final int HEIGHT = 10;
    private static final int SPEED = 10;

    private int x, y;
    private boolean visible = false;

    public void fire(int startX, int startY) {
        if (!visible) {
            this.x = startX;
            this.y = startY;
            visible = true;
        }
    }

    public void update() {
        if (visible) {
            y -= SPEED;
            if (y < 0) visible = false;
        }
    }

    public void draw(Graphics g) {
        if (visible) {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, WIDTH, HEIGHT);
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void hide() {
        visible = false;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }
}
