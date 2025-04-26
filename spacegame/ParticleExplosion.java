package spacegame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

class ParticleExplosion {
    // Collection of particles forming the explosion
    private List<Particle> particles = new ArrayList<>();

    // Create a burst of particles at (x, y)
    public ParticleExplosion(int x, int y) {
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(x, y));
        }
    }

    // Update all particles and remove dead ones
    public void update() {
        for (Particle p : particles) {
            p.update();
        }
        particles.removeIf(p -> !p.isAlive());
    }

    // Draw all particles
    public void draw(Graphics g) {
        for (Particle p : particles) {
            p.draw(g);
        }
    }

    // Check if explosion is still active (has live particles)
    public boolean isActive() {
        return !particles.isEmpty();
    }
}
