package spacegame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputManager implements KeyListener {
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean firePressed = false;
    private boolean dashPressed = false;
    private boolean enterPressed = false;
    private boolean restartPressed = false;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> leftPressed = true;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rightPressed = true;
            case KeyEvent.VK_UP, KeyEvent.VK_W -> firePressed = true;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> dashPressed = true;
            case KeyEvent.VK_ENTER -> enterPressed = true;
            case KeyEvent.VK_R -> restartPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> leftPressed = false;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rightPressed = false;
            case KeyEvent.VK_UP, KeyEvent.VK_W -> firePressed = false;
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> dashPressed = false;
            case KeyEvent.VK_ENTER -> enterPressed = false;
            case KeyEvent.VK_R -> restartPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Getters for GamePanel to use
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isFirePressed() { return firePressed; }
    public boolean isDashPressed() { return dashPressed; }
    public boolean isEnterPressed() { return enterPressed; }
    public boolean isRestartPressed() { return restartPressed; }

    // Optional: call this each frame to "consume" one-time presses like ENTER or R
    public void resetOneTimeActions() {
        enterPressed = false;
        restartPressed = false;
    }
}
