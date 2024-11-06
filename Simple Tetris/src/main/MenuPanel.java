package main;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class MenuPanel extends JPanel implements ActionListener {

    private final JButton startButton;
    private final JButton exitButton;
    private Image backgroundImage;

    public MenuPanel() {
        initializePanel();
        
        // Load background image
        loadBackgroundImage();

        // Create and add buttons
        startButton = createButton("Start Game", new Color(30, 144, 255), 40);
        exitButton = createButton("Exit", new Color(255, 69, 0), 110);
    }
    
    private void initializePanel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(GamePanel.WIDTH, GamePanel.HEIGHT));
        setLayout(null); // Use absolute positioning for full control
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(getClass().getResource("/res/menu.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JButton createButton(String text, Color color, int offsetY) {
        JButton button = new JButton(text);
        button.setBounds((GamePanel.WIDTH - 200) / 2, (GamePanel.HEIGHT - 100) / 2 + offsetY, 200, 50);
        button.setForeground(Color.WHITE); // Set text color to white
        button.setBackground(color); // Set button color
        button.setFont(new Font("Arial", Font.BOLD, 25)); // Set font
        button.setBorder(null); // Remove the border
        button.setFocusPainted(false); // Remove the focus border
        button.addActionListener(this);
        add(button);
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background image if loaded
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        // Draw title text
        g.setColor(Color.WHITE); // Set text color to white
        g.setFont(new Font("Arial", Font.BOLD, 45)); // Set font and size
        String title = "Fantastic Tetris";
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int x = 480; // X position
        int y = 290; // Y position for the text
        g.drawString(title, x, y); // Draw the title
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            Main.menuToGamePanel(); // Start the game
        } else if (e.getSource() == exitButton) {
            System.exit(0); // Exit the game
        }
    }
}