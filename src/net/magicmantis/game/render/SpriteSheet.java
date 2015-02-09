package net.magicmantis.game.render;

/*
Class: SpriteSheet

This class accepts the file path of an image and turns it into a SpriteSheet
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SpriteSheet {

    private int tileWidth, tileHeight;
    private int imageWidth, imageHeight;

    private BufferedImage image = null;
    private int pixels[];

    public SpriteSheet(int tileWidth, int tileHeight, String imagePath) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        //read the SpriteSheet image
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath));
        } catch (IOException ioe) {
            System.out.println("Couldn't find image: "+imagePath);
            return;
        }

        imageWidth = image.getWidth();
        imageHeight = image.getHeight();

        //gets the RGB data from the SpriteSheet image
        pixels = image.getRGB(0, 0, imageWidth, imageHeight, pixels, 0, imageWidth);

    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int[] getPixels() {
        return pixels;
    }

}
