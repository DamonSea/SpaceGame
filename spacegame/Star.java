package spacegame;

import java.awt.*;
import java.util.Random;

class Star {
    int x, y, brightness, delta, speed;

    // Create a star with random brightness, twinkle direction, and fall speed
    public Star(int x, int y) {
        this.x = x;
        this.y = y;
        this.brightness = new Random().nextInt(156) + 100; // Brightness between 100-255
        this.delta = new Random().nextBoolean() ? 1 : -1; // Twinkle direction: increase or decrease
        this.speed = new Random().nextInt(2) + 1; // Speed: 1 or 2 pixels per frame
    }

    // Update star brightness and vertical position
    public void twinkle() {
        brightness += delta;
        if (brightness > 255) {
            brightness = 255;
            delta = -1; // Reverse to start dimming
        } else if (brightness < 100) {
            brightness = 100;
            delta = 1; // Reverse to start brightening
        }
        y += speed;
        if (y > SpaceGame.HEIGHT) {
            y = 0; // Reset star to top when it moves off screen
        }
    }

    // Return the star's color based on brightness
    public Color getColor() {
        return new Color(brightness, brightness, brightness);
    }
}