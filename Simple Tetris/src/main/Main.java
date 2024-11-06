package main;

import javax.swing.JFrame;

public class Main {

    static JFrame window;
    static GamePanel gamePanel;
    static MenuPanel menuPanel;

    public static void main(String[] args) {
        window = new JFrame("Fantastic Tetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        
        gamePanel = new GamePanel();
        menuPanel = new MenuPanel();

        window.add(menuPanel); // Start with MenuPanel
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }

    public static void menuToGamePanel() {
        window.remove(menuPanel);
        gamePanel = new GamePanel(); // New instance for a fresh state
        window.add(gamePanel);
        window.revalidate();
        window.repaint();
        gamePanel.requestFocusInWindow();
        gamePanel.launchGame();
    }

    public static void gameToMenuPanel() {
        window.remove(gamePanel);
        window.add(menuPanel); 
        window.revalidate(); 
        window.repaint(); 
        menuPanel.requestFocusInWindow();

    }
}