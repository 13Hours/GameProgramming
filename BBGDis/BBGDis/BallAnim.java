 

import java.util.ArrayList;
import java.awt.Graphics;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.awt.geom.Rectangle2D;
import java.applet.Applet;
import java.applet.AudioClip;

    /**
        The AnimDisappear class is an Animation object that slowly
        disappears and then reappears and the effect is repeated.
    */

    public class BallAnim extends Animation {

        private ArrayList framesCopy;       // copy of frames for effects
        private int alpha, alphaChange;     // alpha value (for alpha transparency byte)
        private Random random;
        private Bat bat;
        AudioClip hitBatSound;
        AudioClip fallOffSound;
        boolean hitBat;
    /**
        Creates a new, empty Animation, loads the images, and
        adds them to the Animation. A copy of the Animation frames
        is also made for the disappearing effect.
    */

    public BallAnim (JFrame f, Bat b,
             int x, int y, int dx, int dy, 
             int xSize, int ySize, 
             String filename) {
        super(f, x, y, dx, dy, xSize, ySize, filename);

        framesCopy = new ArrayList();

        bat = b;

        // load images from image files

        BufferedImage player1 = loadImage("images/a2z_1.png");
        BufferedImage player2 = loadImage("images/a2z_2.png");
        BufferedImage player3 = loadImage("images/a2z_3.png");
    
        // insert frames

        addFrame(player1, 250);
        addFrame(player2, 150);
        addFrame(player1, 150);
        addFrame(player2, 150);
        addFrame(player3, 200);
        addFrame(player2, 150);

        copyAnimation (framesCopy, frames);
        alpha = 255;                // set to 255 (fully opaque)
        alphaChange = 5;            // set to 15

            start();

        random = new Random();

        }

    public void copyAnimation (ArrayList framesCopy, ArrayList frames) {
        framesCopy.clear();
        for (int i=0; i<frames.size(); i++) {
            AnimFrame frame = (AnimFrame)frames.get(i);
            BufferedImage copy = copyImage(frame.image);
            framesCopy.add(copy);
        }
    }

    /**
        Updates this animation's current image (frame), if
        neccesary and applies the disappearing effect to the image.
    */

    public synchronized void update() {
        super.update();
         alpha = alpha - alphaChange;

         if (alpha <= 50) {    // reset to opaque if completely transparent
             copyAnimation(framesCopy, frames);
             alpha = 255;
         }
        updatePosition();
    }


    public void updatePosition() {
        
        // x = x + dx; 

        // if (x < 0) {
        //     x = 0;
        //     dx = dx * -1;
        // }
        // else
        // if (x + xSize > dimension.width) {
        //     x = dimension.width - xSize;
        //     dx = dx * -1;

        // }

        // y = y + dy;
        // if (y < 0) {
        //     y = 0;
        //     dy = -dy;
        // }
        // else
        // if (y + ySize > dimension.height) {
        //     y = dimension.height - ySize;
        //     dy = -dy;
        // }

        if (!window.isVisible ()) return;
    
        x = x - dx;

        hitBat = batHitsBall();

        if (hitBat || isOffScreen()) {
            if (hitBat) {
                playClip (1);           // play clip if bat hits ball
            }
            else {                  // play clip if ball falls out at bottom
                playClip (2);
            }

            try {                   // take a rest if bat hits ball or
                Thread.sleep (100);        //   ball falls out of play.
            }
            catch (InterruptedException e) {};

            setPosition ();             // re-set position of ball
        }

    }
    public boolean hitbat(){
        return hitBat;
    }

    public void setPosition () {
        int y = random.nextInt(dimension.height - ySize-60);
        if(y<0)
            y=0;
        setX(dimension.width);                  // set initial position of ball
        setY(y);
    }

    public boolean batHitsBall () {

        Rectangle2D.Double rectBall = getBoundingRectangle();
        Rectangle2D.Double rectBat = bat.getBoundingRectangle();
        
        if (rectBall.intersects(rectBat))
            return true;
        else
            return false;
    }


    public boolean isOffScreen () {

        if (x < 10)
            return true;
        else
            return false;
    }

    public void loadClips() {

        try {

            hitBatSound = Applet.newAudioClip (
                    getClass().getResource("sounds/hitBat.au"));

            fallOffSound = Applet.newAudioClip (
                    getClass().getResource("sounds/fallOff.au"));

        }
        catch (Exception e) {
            System.out.println ("Error loading sound file: " + e);
        }

    }

    public void playClip (int index) {

        if (index == 1 && hitBatSound != null)
            hitBatSound.play();
        else
        if (index == 2 && fallOffSound != null)
            fallOffSound.play();

    }

    public void draw (Graphics g) {         // draw the current frame
        BufferedImage copy = getImage();

        int imWidth = copy.getWidth();
        int imHeight = copy.getHeight();

            int [] pixels = new int[imWidth * imHeight];
            copy.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

            int red, green, blue, newValue;
            int pixelAlpha;

        for (int i=0; i<pixels.length; i++) {
            pixelAlpha = (pixels[i] >> 24) & 255;
            red = (pixels[i] >> 16) & 255;
            green = (pixels[i] >> 8) & 255;
            blue = pixels[i] & 255;

            if (pixelAlpha != 0) {
                newValue = blue | (green << 8) | (red << 16) | (alpha << 24);
                pixels[i] = newValue;
            }
        }
            copy.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);   

        g.drawImage(copy, x, y, xSize, ySize, null);

    }


    /**
        Gets this Animation's current image. Returns null if this
        animation has no images.
    */

    public synchronized BufferedImage getImage() {
        if (framesCopy.size() == 0) {
            return null;
        }
        else {
            return (BufferedImage)framesCopy.get(currFrameIndex);
        }
    }


    // make a copy of the BufferedImage src

    public BufferedImage copyImage(BufferedImage src) {
        if (src == null)
            return null;

        BufferedImage copy = new BufferedImage (src.getWidth(), src.getHeight(),
                            BufferedImage.TYPE_INT_ARGB);

            Graphics g = copy.createGraphics();

            // copy image

            g.drawImage(src, 0, 0, null);
            g.dispose();

            return copy; 
    }
}