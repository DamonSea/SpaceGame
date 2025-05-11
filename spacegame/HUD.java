package spacegame;

import javax.swing.*;
import java.awt.*;

public class HUD {
    private final JLabel label;

    public HUD() {
        label = new JLabel();
        label.setForeground(Color.GREEN);
        label.setOpaque(true);
        label.setBackground(Color.BLACK);
        label.setFont(new Font("Dialog", Font.PLAIN, 14));
        label.setBounds(10, 10, 300, 20);
    }

    public JLabel getLabel() {
        return label;
    }

    public void update(int score, int health, long elapsedTimeMillis) {
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < health; i++) hearts.append("\u2665");

        long seconds = elapsedTimeMillis / 1000;
        label.setText("Score: " + score + "    Health: " + hearts + "    Time: " + seconds + "s");
    }
}
