package spacegame;

import java.awt.*;
import java.util.Random;

class Star {
    int x, y, brightness, delta, speed;
    public Star(int x, int y) {
        this.x = x;
        this.y = y;
        this.brightness = new Random().nextInt(156) + 100;
        this.delta = new Random().nextBoolean() ? 1 : -1;
        this.speed = new Random().nextInt(2) + 1;
    }

    public void twinkle() {
        brightness += delta;
        if (brightness > 255) {
            brightness = 255;
            delta = -1;
        } else if (brightness < 100) {
            brightness = 100;
            delta = 1;
        }
        y += speed;
        if (y > SpaceGame.HEIGHT) {
            y = 0;
        }
    }
    public Color getColor() {
        return new Color(brightness, brightness, brightness);
    }
}