package spacegame;

import java.awt.*;
import java.util.Random;

public class Star {
    private int x, y, brightness, delta, speed;

    // Create a star with random brightness, twinkle direction, and fall speed
    public Star(int x, int y) {
        this.x = x;
        this.y = y;
        this.brightness = new Random().nextInt(156) + 100; // Brightness between 100–255
        this.delta = new Random().nextBoolean() ? 1 : -1;  // Twinkle direction
        this.speed = new Random().nextInt(2) + 1;           // Speed: 1 or 2 pixels/frame
    }

    // Update star brightness and vertical position (requires screen height)
    public void twinkle(int screenHeight) {
        brightness += delta;
        if (brightness > 255) {
            brightness = 255;
            delta = -1;
        } else if (brightness < 100) {
            brightness = 100;
            delta = 1;
        }

        y += speed;
        if (y > screenHeight) {
            y = 0; // Wrap to top
        }
    }

    // Return the star's color based on brightness
    public Color getColor() {
        return new Color(brightness, brightness, brightness);
    }

    // Accessors for position
    public int getX() { return x; }
    public int getY() { return y; }
}
