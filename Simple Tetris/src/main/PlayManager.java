package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import mino.Block;
import mino.Mino;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Square;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;

public class PlayManager implements ImageObserver {

    // main play area
    final int WIDTH = 360;
    final int HEIGHT = 600;
    public static int left_x;
    public static int right_x;
    public static int top_y;
    public static int bottom_y;

    // Mino
    Mino currentMino;
    final int MINO_START_X;
    final int MINO_START_Y;

    Mino nextMino;
    final int NEXTMINO_X;
    final int NEXTMINO_Y;
    public static ArrayList<Block> staticBlocks = new ArrayList<>();

    // Others
    public static int dropInterval ; // mino drops in every 60 frames
    boolean gameOver;

    // Effect
    boolean effectCounterOn;
    int effectCounter;
    ArrayList<Integer> effectY = new ArrayList<>();

    // Score
    int level = 1;
    int lines;
    int score;

    int img_select;
    private Image[] images = new Image[5];

    // "Play Again" Button
    Rectangle playAgainButton = new Rectangle(left_x + 100, top_y + 280, 160, 55); 

    Rectangle MenuButton = new Rectangle(left_x + 100, top_y + 360, 160, 55); 

    private void loadBackgroundImage() {
        try {
            images[0] = ImageIO.read(getClass().getResource("/res/bg1.jpg"));
            images[1] = ImageIO.read(getClass().getResource("/res/bg2.jpg"));
            images[2] = ImageIO.read(getClass().getResource("/res/bg3.jpg"));
            images[3] = ImageIO.read(getClass().getResource("/res/bg4.jpg"));
            images[4] = ImageIO.read(getClass().getResource("/res/bg5.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayManager() {
        // Main play area Frame
        loadBackgroundImage();
        left_x = (GamePanel.WIDTH / 2) - (WIDTH / 2); // 1280/2 - 360/2 = 460
        right_x = left_x + WIDTH;
        top_y = 50;
        bottom_y = top_y + HEIGHT;

        MINO_START_X = left_x + (WIDTH / 2) - Block.SIZE;
        MINO_START_Y = top_y + Block.SIZE;

        NEXTMINO_X = right_x + 175;
        NEXTMINO_Y = top_y + 500;

        // Set starting mino
        
        currentMino = pickMino();
        currentMino.setXY(MINO_START_X, MINO_START_Y);
        nextMino = pickMino();
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
    }

    private Mino pickMino() {
        // Pick a random Mino
        Mino mino = null;
        int i = new Random().nextInt(7);

        switch (i) {
            case 0:
                mino = new Mino_L1();
                break;
            case 1:
                mino = new Mino_L2();
                break;
            case 2:
                mino = new Mino_Square();
                break;
            case 3:
                mino = new Mino_Bar();
                break;
            case 4:
                mino = new Mino_T();
                break;
            case 5:
                mino = new Mino_Z1();
                break;
            case 6:
                mino = new Mino_Z2();
                break;
        }
        return mino;
    }

    public void update() {
        // Check if the currentMino is active
        if (currentMino.active == false) {
            // If the mino is not active, put it into staticBlocks
            staticBlocks.add(currentMino.b[0]);
            staticBlocks.add(currentMino.b[1]);
            staticBlocks.add(currentMino.b[2]);
            staticBlocks.add(currentMino.b[3]);

            // Check if the game is over
            if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
                // If the currentMino immediately collided and couldn't move at all
                gameOver = true;
                GamePanel.music.stop();
                GamePanel.se.play(2, false);
            }

            currentMino.deactivating = false;

            // Replace the currentMino with the nextMino
            currentMino = nextMino;
            currentMino.setXY(MINO_START_X, MINO_START_Y);
            nextMino = pickMino();
            nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

            // When a mino becomes inactive, check if line(s) can be deleted
            checkDelete();
        } else {
            currentMino.update();
        }
    }

    private void checkDelete() {
        int x = left_x;
        int y = top_y;
        int blockCount = 0;
        int lineCount = 0;

        while (x < right_x && y < bottom_y) {
            for (int i = 0; i < staticBlocks.size(); i++) {
                if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
                    blockCount++;
                }
            }

            x += Block.SIZE;

            if (x == right_x) {
                if (blockCount == 12) { //ครบ 12 บล็อกใน 1 แถว ลบ
                    effectCounterOn = true;
                    effectY.add(y);

                    for (int i = staticBlocks.size() - 1; i > -1; i--) {
                        if (staticBlocks.get(i).y == y) {
                            staticBlocks.remove(i);
                        }
                    }

                    lineCount++;
                    lines++;

                    // Drop speed
                    if (lines % 5 == 0 && dropInterval > 1) { // ต่ำกว่า 1 จะไม่ลบความเร็ว
                        level++;
                        if (dropInterval > 12) { // เริ่มนับที่ 60 ลดทีละ 12 >48>36>24>12 พอเหลือ 12 จะลดทีละ 3
                            dropInterval -= 12;
                            img_select += 1 ;
                        } else {
                            dropInterval -= 3;
                        }
                    }

                    // Slide down blocks that are above the deleted line
                    for (int i = 0; i < staticBlocks.size(); i++) {
                        if (staticBlocks.get(i).y < y) {
                            staticBlocks.get(i).y += Block.SIZE;
                        }
                    }
                }

                blockCount = 0;
                x = left_x;
                y += Block.SIZE;
            }
        }

        // Add score
        if (lineCount > 0) {
            GamePanel.se.play(1, false);
            int singleLineScore = 45 * level;
            score += singleLineScore * lineCount;
        }
    }

    public void draw(Graphics2D g2) {

    
        if (images != null) {
            g2.drawImage(images[img_select], 0, 0, 1280, 720, this);
        }
    
        // Draw Play Area Frame
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(4f));
        g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);

        // Draw Next Mino Frame
        int x = right_x + 93;
        int y = bottom_y - 215;
        g2.drawRect(x, y, 200, 200);
        g2.setFont(new Font("Arial", Font.PLAIN, 30));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.drawString("NEXT : ", x + 60, y + 60);

        

        // Draw Score Frame
        g2.drawRect(x, top_y, 250, 300);
        x += 40;
        y = top_y + 90;
        g2.drawString("LEVEL:" + level, x, y);
        y += 70;
        g2.drawString("LINES:" + lines, x, y);
        y += 70;
        g2.drawString("SCORE:" + score, x, y);
        

  

        // Draw the CurrentMino
        if (currentMino != null) {
            currentMino.draw(g2);
        }

        // Draw the nextMino
        nextMino.draw(g2);

        // Draw Static blocks
        for (int i = 0; i < staticBlocks.size(); i++) {
            staticBlocks.get(i).draw(g2);
        }

        // Draw Effect
        if (effectCounterOn) {
            effectCounter++;

            g2.setColor(Color.white);
            for (int i = 0; i < effectY.size(); i++) {
                g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
            }

            if (effectCounter == 10) {
                effectCounterOn = false;
                effectCounter = 0;
                effectY.clear();
            }
        }

        // Draw pause and gameover
        g2.setColor(Color.yellow);
        g2.setFont(g2.getFont().deriveFont(50f));

        x = 35;
        y = top_y + 320;
        g2.setColor(Color.white);
        g2.setFont(new Font("times New Roman",Font.ITALIC,60));
        g2.drawString("Fantastic Tetris", x-5, y);

        if (gameOver) {


          g2.setColor(Color.black);
          g2.fillRect(390, 200, 500, 300);
          g2.setColor(Color.white);
          g2.drawRect(390, 200, 500, 300);

          g2.setFont(new Font("times New Roman",Font.BOLD,60));
          g2.setColor(Color.red);  
          g2.drawString("GAME OVER", 460, 270);

            g2.setColor(Color.red);
            g2.fillRect(playAgainButton.x, playAgainButton.y, playAgainButton.width, playAgainButton.height);
            g2.setColor(Color.white);
            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            g2.drawString("Play Again", playAgainButton.x + 25, playAgainButton.y + 35);

            g2.setColor(Color.green);
            g2.fillRect( MenuButton.x,  MenuButton.y,  MenuButton.width,  MenuButton.height);
            g2.setColor(Color.white);
            g2.setFont(new Font("Arial", Font.PLAIN, 24));
            g2.drawString("Menu",  MenuButton.x + 50,  MenuButton.y + 35);

            
        }

        if (KeyHandler.pausePressed && !gameOver) {
            x = left_x + 70;
            y = top_y + 320;
            g2.drawString("PAUSED", x, y);
        }

        
    }

    public void handleMouseClick(int mouseX, int mouseY) {
        if (gameOver && playAgainButton.contains(mouseX, mouseY)) {
            resetGame();
        }

        if (gameOver && MenuButton.contains(mouseX, mouseY)) {
          Main.gameToMenuPanel();
          
      }
    }



    public void resetGame() {
        // Reset all game variables to their initial state
        gameOver = false;          // Set gameOver to false to allow playing again
        effectCounterOn = false;   // Reset any ongoing effects
        effectCounter = 0;
        effectY.clear();           // Clear effect lines

        level = 1;                 // Reset level
        lines = 0;                 // Reset lines cleared
        score = 0;                 // Reset score
        dropInterval = 60;         // Reset the drop speed
        img_select = 0;
        staticBlocks.clear();      // Remove all static blocks on the playfield

        currentMino = pickMino();  // Generate a new current mino
        currentMino.setXY(MINO_START_X, MINO_START_Y);  // Place it at the starting position

        nextMino = pickMino();     // Generate a new next mino
        nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);         // Set its position in the next area

        // Optionally, restart the game music
        GamePanel.music.play(0,true);    
        GamePanel.music.loop();
    }

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}