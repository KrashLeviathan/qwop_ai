package qwop_ai;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageParser {

    // SUBIMAGE DIMENSIONS:

    /// DISTANCE TICKER:

    public static final Integer DIST_X = 370;
    public static final Integer DIST_Y = 39;
    public static final Integer WIDTH = 240;
    public static final Integer HEIGHT = 35;

    public static BufferedImage getBufferedImage(File image){
        BufferedImage img = null;

        try {
            img = ImageIO.read(image);
        } catch (IOException e){
            e.printStackTrace();
        }

        return img;
    }

    //image must be 1024 x 720 image of QWOP game for accurate readings
    public static void getDistanceSubImage(BufferedImage img){
        BufferedImage sub = img.getSubimage(DIST_X, DIST_Y, WIDTH, HEIGHT);
        try{
            ImageIO.write(sub, "png", new File("clipped.png"));
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
