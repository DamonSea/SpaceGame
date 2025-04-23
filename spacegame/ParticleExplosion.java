package spacegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class ParticleExplosion {
    private List<Particle> particles = new ArrayList<>();

    public ParticleExplosion(int x, int y) {
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(x, y));
        }
    }

    public void update() {
        for (Particle p : particles) {
            p.update();
        }
        particles.removeIf(p -> !p.isAlive());
    }

    public void draw(Graphics g) {
        for (Particle p : particles) {
            p.draw(g);
        }
    }

    public boolean isActive() {
        return !particles.isEmpty();
    }
}