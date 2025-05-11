package spacegame;

import javax.swing.*;

public class SpaceGame extends JFrame {
    public SpaceGame() {
        setTitle("Space Game");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SpaceGame::new);
    }
}
