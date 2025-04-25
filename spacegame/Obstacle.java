
package spacegame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Obstacle {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    private static final int SPEED = 3;

    private int x, y;
    private BufferedImage obstacleImage;

    public Obstacle(int startX, BufferedImage spriteSheet) {
        this.x = startX;
        this.y = 0;

        if (spriteSheet != null) {
            int spriteSize = 64;
            int spritesPerRow = spriteSheet.getWidth() / spriteSize;
            Random rand = new Random();
            int spriteIndex = rand.nextInt(4);
            int sx = (spriteIndex % spritesPerRow) * spriteSize;
            int sy = (spriteIndex / spritesPerRow) * spriteSize;
            obstacleImage = spriteSheet.getSubimage(sx, sy, spriteSize, spriteSize);
        }
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

    public void draw(Graphics g) {
        if (obstacleImage != null) {
            g.drawImage(obstacleImage, x, y, WIDTH, HEIGHT, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, WIDTH, HEIGHT);
        }
    }
}
