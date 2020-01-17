/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ylabpuzzle;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Emre
 */
public class puzzle extends JPanel implements ActionListener {

    private JFrame frame;
    private JPanel panelImage;
    private JButton button_upload, button_shuffle;
    private JLabel highScoreText;
    private JLabel scoreText;
    private BufferedImage bimage;
    private puzzleFile pf;

    private final Icon imgs[][] = new Icon[4][4];
    private final JButton buttons[][] = new JButton[4][4];
    private final boolean checkPieces[][] = new boolean[4][4];

    private String imagePath;
    private final int pieceCount = 4;
    private int first_click_x = -1, first_click_y = -1;
    private int shuffle_click_count;
    private double Score;

    public puzzle() throws URISyntaxException, IOException {
        createAndShowGui();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

    }

    private void createAndShowGui() throws IOException {

        pf = new puzzleFile();
        button_upload = new JButton("Resim Yükle");
        button_shuffle = new JButton("Karýþtýr");
        highScoreText = new JLabel("High Score : " + pf.getHighScore());
        scoreText = new JLabel("Score : ");
        setLayout(null);
        scoreText.setBounds(320, 450, 200, 50);
        highScoreText.setBounds(100, 450, 200, 50);
        button_upload.setBounds(100, 500, 200, 50);
        button_shuffle.setBounds(320, 500, 200, 50);
        button_upload.addActionListener(this);
        button_shuffle.addActionListener(this);
        add(scoreText);
        add(highScoreText);
        add(button_upload);
        add(button_shuffle);

        panelImage = new JPanel();

        panelImage.setBounds(100, 20, 420, 420);

        for (int i = 0; i < pieceCount; i++) {
            for (int j = 0; j < pieceCount; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setBounds(100 + (j * 105), 20 + (i * 105), 105, 105);
                buttons[i][j].setContentAreaFilled(false);
                buttons[i][j].addActionListener(this);
                add(buttons[i][j]);
            }
        }
        setButtonsEnabled(false);

        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.pack();
        frame.setTitle("Yapboz Oyunu");
        frame.setSize(600, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(panelImage);
        frame.add(this);
        frame.setVisible(true);
    }

    private BufferedImage resizeImage(BufferedImage img) {
        Image tmp = img.getScaledInstance(420, 420, Image.SCALE_SMOOTH);
        BufferedImage rimg = new BufferedImage(420, 420, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = rimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return rimg;
    }

    public BufferedImage convertIconToImage(Icon icon) {
        BufferedImage bi = new BufferedImage(
                icon.getIconWidth(),
                icon.getIconHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        return bi;
    }

    public boolean compareImage(Icon icon, Icon icon1) {
        BufferedImage image_icon = convertIconToImage(icon);
        BufferedImage image_icon_1 = convertIconToImage(icon1);
        
            long difference = 0;
            for (int y = 0; y < image_icon.getHeight(); y++) {
                for (int x = 0; x < image_icon.getWidth(); x++) {
                    int rgbA = image_icon.getRGB(x, y);
                    int rgbB = image_icon_1.getRGB(x, y);
                    int redA = (rgbA >> 16) & 0xff;
                    int greenA = (rgbA >> 8) & 0xff;
                    int blueA = (rgbA) & 0xff;
                    int redB = (rgbB >> 16) & 0xff;
                    int greenB = (rgbB >> 8) & 0xff;
                    int blueB = (rgbB) & 0xff;
                    difference += Math.abs(redA - redB);
                    difference += Math.abs(greenA - greenB);
                    difference += Math.abs(blueA - blueB);
                }
            }
            double total_pixels = image_icon.getWidth() * image_icon.getHeight() * 3;
            double avg_different_pixels = difference
                    / total_pixels;
            double percentage = (avg_different_pixels
                    / 255) * 100;

            if (percentage == 0.0) {
                return true;
            }
        
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == button_upload) {

            JFileChooser file = new JFileChooser();
            file.setCurrentDirectory(new File(System.getProperty("user.home")));

            FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg", "gif", "png");
            file.addChoosableFileFilter(filter);
            int result = file.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = file.getSelectedFile();
                imagePath = selectedFile.getAbsolutePath();

                try {
                    bimage = ImageIO.read(new File(imagePath));
                    bimage = resizeImage(bimage);
                    getImages();

                } catch (IOException ex) {

                    Logger.getLogger(puzzle.class.getName()).log(Level.SEVERE, null, ex);
                }
                refreshButtons();
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("Hiçbir Dosya Seçilmedi");
            }
            try {
                highScoreText.setText("High Score : " + pf.getHighScore());
            } catch (IOException ex) {
                Logger.getLogger(puzzle.class.getName()).log(Level.SEVERE, null, ex);
            }
            Score = 0.0;
            shuffle_click_count = 0;
        } else if (ae.getSource() == button_shuffle) {
            Score = 0.0;
            scoreText.setText("Score : ");
            for (int i = 0; i < pieceCount; i++) {
                for (int j = 0; j < pieceCount; j++) {
                    checkPieces[i][j] = false;
                }
            }
            Icon ico;
            for (int i = 0; i < pieceCount; i++) {
                for (int j = 0; j < pieceCount; j++) {
                    Random rnd = new Random();
                    int sayi_x = rnd.nextInt(pieceCount);
                    int sayi_y = rnd.nextInt(pieceCount);

                    ico = buttons[i][j].getIcon();
                    buttons[i][j].setIcon(buttons[sayi_x][sayi_y].getIcon());
                    buttons[sayi_x][sayi_y].setIcon(ico);
                }
            }
            for (int i = 0; i < pieceCount; i++) {
                for (int j = 0; j < pieceCount; j++) {
                    if (compareImage(imgs[i][j], buttons[i][j].getIcon()) == true) {
                        checkPieces[i][j] = true;
                        if (checkAllPieces() == true && shuffle_click_count == 0) {
                            Score = 100;
                            scoreText.setText("Score : " + Score);
                            try {
                                pf = new puzzleFile("Score:" + Score + "\n");
                                highScoreText.setText("High Score : " + pf.getHighScore());
                            } catch (IOException ex) {
                                Logger.getLogger(puzzle.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }
                        setButtonsEnabled(true);
                    }
                }
            }
            shuffle_click_count++;
        } else {
            for (int i = 0; i < pieceCount; i++) {
                for (int j = 0; j < pieceCount; j++) {
                    if (ae.getSource() == buttons[i][j]) {
                        if ((first_click_x != -1 && first_click_y != -1) ) {
                            Icon ico = buttons[i][j].getIcon();
                            buttons[i][j].setIcon(buttons[first_click_x][first_click_y].getIcon());
                            buttons[first_click_x][first_click_y].setIcon(ico);
                            if (compareImage(imgs[i][j], buttons[i][j].getIcon()) == true && compareImage(imgs[first_click_x][first_click_y], buttons[first_click_x][first_click_y].getIcon()) == true) {
                                Score += 12.50;
                                scoreText.setText("Score : " + Score);
                                checkPieces[i][j] = true;
                                checkPieces[first_click_x][first_click_y] = true;
                            } else if (compareImage(imgs[i][j], buttons[i][j].getIcon()) == true) {
                                Score += 6.0;
                                scoreText.setText("Score : " + Score);
                                checkPieces[i][j] = true;
                            } else if (compareImage(imgs[first_click_x][first_click_y], buttons[first_click_x][first_click_y].getIcon()) == true) {
                                Score += 6.0;
                                scoreText.setText("Score : " + Score);
                                checkPieces[first_click_x][first_click_y] = true;
                            } else {
                                Score -= 1.50;
                                scoreText.setText("Score : " + Score);
                            }
                            if (checkAllPieces() == true) {
                                try {
                                    pf = new puzzleFile("High Score:" + Score + "\n");
                                    highScoreText.setText("High Score : " + pf.getHighScore());
                                } catch (IOException ex) {
                                    Logger.getLogger(puzzle.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                setButtonsEnabled(false);
                            }
                            first_click_x = -1;
                            first_click_y = -1;
                        } else {
                            first_click_x = i;
                            first_click_y = j;
                        }
                    }
                }
            }
        }
    }

    private boolean checkAllPieces() {
        for (int i = 0; i < pieceCount; i++) {
            for (int j = 0; j < pieceCount; j++) {
                if (checkPieces[i][j] == false) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setButtonsEnabled(boolean bool) {
        for (int i = 0; i < pieceCount; i++) {
            for (int j = 0; j < pieceCount; j++) {
                buttons[i][j].setEnabled(bool);
            }
        }
    }

    private void refreshButtons() {
        for (int i = 0; i < pieceCount; i++) {
            for (int j = 0; j < pieceCount; j++) {
                buttons[i][j].setBounds(100 + (j * 105), 20 + (i * 105), 105, 105);
            }
        }
    }

    private void getImages() {
        for (int i = 0; i < pieceCount; i++) {
            for (int j = 0; j < pieceCount; j++) {
                imgs[i][j] = new ImageIcon(bimage.getSubimage((j * 105), (i
                        * 105), 105, 105));
                buttons[i][j].setIcon(imgs[i][j]);
            }
        }
    }

}
