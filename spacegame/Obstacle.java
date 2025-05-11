package spacegame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import spacegame.GameObject;


public class Obstacle implements GameObject {

    // Size constants for all obstacles
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    private static final int SPEED = 3; // Downward movement speed

    private int x, y;
    private BufferedImage obstacleImage;

    // Constructor initializes position and loads a random sprite from spriteSheet
    public Obstacle(int startX, BufferedImage spriteSheet) {
        this.x = startX;
        this.y = 0;

        if (spriteSheet != null) {
            int spriteSize = 64; // Fixed sprite size in sheet
            int spritesPerRow = spriteSheet.getWidth() / spriteSize;
            Random rand = new Random();
            int spriteIndex = rand.nextInt(4); // Randomly pick among 4 sprites
            int sx = (spriteIndex % spritesPerRow) * spriteSize;
            int sy = (spriteIndex / spritesPerRow) * spriteSize;
            obstacleImage = spriteSheet.getSubimage(sx, sy, spriteSize, spriteSize);
        }
    }

    // Move the obstacle downward each frame
    public void update() {
        y += SPEED;
    }

    // Check if the obstacle has gone off screen
    public boolean isOffScreen(int height) {
        return y > height;
    }

    // Return bounding box for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, WIDTH, HEIGHT);
    }

    // Draw the obstacle (image if available, else red rectangle)
    public void draw(Graphics g) {
        if (obstacleImage != null) {
            g.drawImage(obstacleImage, x, y, WIDTH, HEIGHT, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, WIDTH, HEIGHT);
        }
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}