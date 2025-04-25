package spacegame;

import java.awt.*;

public class Obstacle {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    private static final int SPEED = 3;

    public int x, y;

    public Obstacle(int startX) {
        this.x = startX;
        this.y = 0;
    }

    public void update() {
        y += SPEED;
    }

    public boolean isOffScreen(int height) {
        return y > height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

//    public void draw(Graphics g) {
//        g.setColor(Color.RED);
//        g.fillRect(x, y, WIDTH, HEIGHT);
//    }
}