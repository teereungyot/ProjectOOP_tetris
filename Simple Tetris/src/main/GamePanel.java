package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    final int FPS = 60;
    Thread gameThread;
    PlayManager pm;
    public static Sound music = new Sound();
    public static Sound se = new Sound();

    public GamePanel() {

        // panel settings
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setLayout(null);

        // Implement KeyListener
        this.addKeyListener(new KeyHandler());
        this.setFocusable(true);

        // Add MouseListener for handling mouse clicks
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                pm.handleMouseClick(mouseX, mouseY);  // Forward the click to PlayManager
            }
        });

        pm = new PlayManager();

    }

    public void launchGame() {
        pm.resetGame();
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {

        // game loop
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {

            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {

        if (!KeyHandler.pausePressed && !pm.gameOver) {
            pm.update();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        pm.draw(g2);
    }
}
