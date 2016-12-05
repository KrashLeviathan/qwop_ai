package qwop_ai;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageParser {

    // COLOR THRESHOLDS
    public static final int BACKGROUND = -10000000;
    public static final int OFF_WHITE_COLOR = -3090468;
    public static final int WHITE_COLOR = -1;

    public static final int OFF_WHITE_TOLERENCE = 10;
    public static final int WHITE_TOLERENCE = 13; //Please do not read more into this var name

    // NUMERICAL IDENTIFICATION

    public static final int ZERO_WIDTH = 7;
    public static final int ONE_WIDTH = 8;
    public static final int TWO_WIDTH = 23;
    public static final int THREE_WIDTH = 10;
    public static final int FOUR_WIDTH = 7;
    public static final int FIVE_WIDTH = 9;
    public static final int SIX_WIDTH = 8;
    public static final int SEVEN_WIDTH = 7;
    public static final int EIGHT_WIDTH = 11;
    public static final int NINE_WIDTH = 4;
    public static final int PERIOD_WIDTH = 5;


    //PRELOAD REFERENCE IMAGES

    public static final BufferedImage ONE = getBufferedImage(new File("ref/one.png"));
    public static final BufferedImage THREE = getBufferedImage(new File("ref/three.png"));
    public static final BufferedImage FIVE = getBufferedImage(new File("ref/five.png"));
    public static final BufferedImage SIX = getBufferedImage(new File("ref/six.png"));
    public static final BufferedImage FOUR = getBufferedImage(new File("ref/four.png"));
    public static final BufferedImage PERIOD = getBufferedImage(new File("ref/period.png"));

    /// JERSEY TRACKING INFORMATION:

    public static final Integer JERSEY_START_X = 450;
    public static final Integer JERSEY_START_Y = 310;

    /// GAME OVER MARKER PIXEL

    public static final Integer GAME_OVER_X = 760;
    public static final Integer GAME_OVER_Y = 200;
    public static final Integer GAME_OVER_COLOR = -256;

    /// DISTANCE TICKER:

    public static final Integer DIST_X = 340;
    public static final Integer DIST_Y = 40;
    public static final Integer WIDTH = 170;
    public static final Integer HEIGHT = 33;

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

    ///Find the x-coord of the 'm' in 'metres' of the Distance Subimage
    public static int findMReference(BufferedImage img){
        BufferedImage ref = getBufferedImage(new File("ref/m.png"));

        for (int i=0; i < img.getWidth() - 1; i++){
            if (img.getRGB(i, 0) == ref.getRGB(0, 0)){
                if (compareColumn(img, ref, i, 0)){
                    return i;
                }
            }
        }
        return -1;
    }

    public static boolean compareColumn(BufferedImage one, BufferedImage two, int x1, int x2){
        for (int j=one.getHeight()-1; j > 0; j--){
            if(!rgbInRange(one.getRGB(x1, j), two.getRGB(x2, j), 32)){
                return false;
            }
        }
        return true;
    }

    public static boolean rgbInRange(int rgb_one, int rgb_two, int radix){

        int red_one = (rgb_one >> 16) & 0x000000FF;
        int green_one = (rgb_one >> 8 ) & 0x000000FF;
        int blue_one = (rgb_one) & 0x000000FF;

        int red_two = (rgb_two >> 16) & 0x000000FF;
        int green_two = (rgb_two >> 8 ) & 0x000000FF;
        int blue_two = (rgb_two) & 0x000000FF;

        return (withinRange(red_one, red_two, radix) &&
                withinRange(green_one, green_two, radix) &&
                withinRange(blue_one, blue_two, radix));
    }


    public static boolean withinRange(int a, int b, int radix){
        return (a - radix <= b) && (a + radix >=b);
    }

    ///Get the distance readout as a double. Pass in the buffered subimage of the Distance readout and a
    /// starting index (for optimization purposes) typically the index of the leftmost edge of the 'm'
    /// in 'metres'
    public static double readDistance(BufferedImage img, int startingIndex){

        String result = "";

        int sequential_white_pixels =0;
        int sequential_offwhite_pixels =0; //used for ONES detection
        for (int i=startingIndex; i>0; i--){

            int bottom_rgb = img.getRGB(i, img.getHeight()-1);

            if (withinRange(bottom_rgb, WHITE_COLOR, WHITE_TOLERENCE)){
                sequential_white_pixels++;
                sequential_offwhite_pixels = 0;
            } else if (withinRange(bottom_rgb, OFF_WHITE_COLOR, OFF_WHITE_TOLERENCE)) {
                sequential_white_pixels = 0;
                sequential_offwhite_pixels++;
            } else {
                switch (sequential_white_pixels){
                    case (ZERO_WIDTH): // Zero or 6
                        if(compareColumn(img, SIX, i+1, 0)){
                            result = "6" + result;
                        } else {
                            result = "0" + result;
                        }

                        break;
                    //ONES and TWOS are off-white
                    case (THREE_WIDTH):
                        result = "3" + result;
                        break;
                    // FOUR off-white
                    case (FIVE_WIDTH): // FIVE OR EIGHT OR POSSIBLY 3
                        if(compareColumn(img, FIVE, i+1, 0)){
                            result = "5" + result;
                        } else if(compareColumn(img, THREE, i, 0)){ //NOTE THIS IS PURPOSEFULLY DIFFERENT
                            result = "3" + result;
                        } else {
                            result = "8" + result;
                        }
                        break;
                    case (SIX_WIDTH):
                        result = "6" + result;
                        break;
                    case (EIGHT_WIDTH):
                            result = "8" + result;
                        break;
                    case (NINE_WIDTH):
                        result = "9" + result;
                        break;
                    case (PERIOD_WIDTH): // PERIOD OR NINE
                        if(compareColumn(img, PERIOD, i+1, 0)){
                            result = "." + result;
                        } else {
                            result = "9" + result;
                        }
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("X WHITE: " + sequential_white_pixels);
                        break;
                }

                switch(sequential_offwhite_pixels){
                    case(ONE_WIDTH): //can be conflated with 4
                        if(compareColumn(img, ONE, i+1, 0)){
                            result = "1" + result;
                        } else {
                            result = "4" + result;
                        }
                        break;
                    case (TWO_WIDTH):
                        result = "2" + result;
                        break;
                    case (FOUR_WIDTH): // FOUR OR SEVEN
                        if(compareColumn(img, FOUR, i+1, 0)){
                            result = "4" + result;
                        } else {
                            result = "7" + result;
                        }
                        break;
                    case (1):
                    case (0):
                        break;
                    default:
                        System.out.println("X OFFW: " + sequential_offwhite_pixels);
                        break;
                }

                sequential_white_pixels = 0;
                sequential_offwhite_pixels = 0;
            }
        }
        if(result.equals("")) return 0;
        return Double.valueOf(result);
    }

    public static void getTorsoAngle(BufferedImage img, int lastCenterX, int lastCenterY){ //TODO: additional learning input

        if(rgbInRange(img.getRGB(lastCenterX, lastCenterY), -1, 50) ){

            int i=1;
            int x, topLX = lastCenterX, topRX = lastCenterX, botLX= lastCenterX, botRX = lastCenterX;
            int y, topLY = lastCenterX, topRY= lastCenterX, botLY= lastCenterX, botRY = lastCenterY;

            //Find corner 0 (top left)
            while(rgbInRange(img.getRGB(topLX--, topLY--), -1, 50)) {}

            //Find corner 1 (top right)


            //Find corner 2 (bottom left)
            //Find corner 3 (bottom right)

        }

    }

    public static boolean gameOver(BufferedImage img){
        return rgbInRange(GAME_OVER_COLOR, img.getRGB(GAME_OVER_X, GAME_OVER_Y), 1);
    }

}
