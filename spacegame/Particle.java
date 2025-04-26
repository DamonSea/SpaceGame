package spacegame;

import java.awt.*;

class Particle {
    // Position and velocity
    double x, y, dx, dy;
    
    // Remaining life frames
    int life;
    
    // Opacity for fade-out effect
    float alpha;
    
    // Particle color
    Color color;

    // Create a particle with random direction and speed
    public Particle(int x, int y) {
        this.x = x;
        this.y = y;
        double angle = Math.random() * 2 * Math.PI;
        double speed = Math.random() * 3 + 1;
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;
        life = 30; // Lifetime in frames
        alpha = 1.0f; // Start fully opaque
        color = new Color(255, (int)(Math.random() * 155), 0); // Random orange shade
    }

    // Update position, life, and opacity
    public void update() {
        x += dx;
        y += dy;
        life--;
        alpha = Math.max(0, life / 30.0f); // Fade out as life decreases
    }

    // Draw the particle with fading effect
    public void draw(Graphics g) {
        if (life > 0) {
            Graphics2D g2d = (Graphics2D) g;
            Composite original = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2d.setColor(color);
            g2d.fillRect((int)x, (int)y, 3, 3);
            g2d.setComposite(original); // Restore original transparency
        }
    }

    // Check if particle is still alive
    public boolean isAlive() {
        return life > 0;
    }
}
