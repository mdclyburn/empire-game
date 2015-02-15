package net.magicmantis.game.render;

/*
Class: Screen

This class is the actually window that users will interact with.
Holds an instance of Game, and uses this instance's rendering methods to paint.
 */

public class Screen {

    private int width, height, tileWidth, tileHeight;
    private SpriteSheet spriteSheet;

    private int xOffset = 0;
    private int yOffset = 0;

    public int[] pixels;

    public Screen(int width, int height, SpriteSheet spriteSheet) {
        this.width = width;
        this.height = height;
        this.spriteSheet = spriteSheet;
        this.tileWidth = spriteSheet.getTileWidth();
        this.tileHeight = spriteSheet.getTileHeight();

        pixels = new int[width*height];
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0;
        }
    }

    public void render(int xPos, int yPos, int tile) {
        //modify position by offset (in game view)
        xPos += xOffset;
        yPos += yOffset;

        //find the first x and y values for the tile
        int xTile = tile % (spriteSheet.getImageWidth()/tileWidth);
        int yTile = tile / (spriteSheet.getImageHeight()/tileHeight);
        //shift left, number based on tile size (3 = 8x8, 4 = 16x16, 5 = 32x32)
        int tileOffset = (xTile << 5) + (yTile << 5) * spriteSheet.getImageWidth();

        //write each pixel from the tile to the screen
        for (int y = 0; y < tileHeight; y++) {
            if (y+yPos < 0 || y+yPos >= height) continue;
            for (int x = 0; x < tileHeight; x++) {
                if (x+xPos < 0 || x+xPos >= width) continue;
                int pix = spriteSheet.getPixels()[x + y * spriteSheet.getImageWidth() + tileOffset];
                if ((pix >> 24 & 0xFF) != 0)
                    pixels[(x+xPos) + (y+yPos) * width] = pix;
            }
        }
    }

    //for sprites of different size than basic tile
    public void render(int xPos, int yPos, int tile, int w, int h) {
        //modify position by offset (in game view)
        xPos += xOffset;
        yPos += yOffset;

        //find the first x and y values for the tile
        int xTile = tile % (spriteSheet.getImageWidth()/tileWidth);
        int yTile = tile / (spriteSheet.getImageHeight()/tileHeight);
        //shift left, number based on tile size (3 = 8x8, 4 = 16x16, 5 = 32x32)
        int tileOffset = (xTile << 5) + (yTile << 5) * spriteSheet.getImageWidth();

        //write each pixel from the tile to the screen
        for (int y = 0; y < h; y++) {
            if (y+yPos < 0 || y+yPos >= height) continue;
            for (int x = 0; x < w; x++) {
                if (x+xPos < 0 || x+xPos >= width) continue;
                int pix = spriteSheet.getPixels()[x + y * spriteSheet.getImageWidth() + tileOffset];
                if ((pix >> 24 & 0xFF) != 0)
                    pixels[(x+xPos) + (y+yPos) * width] = pix;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getXOffset() {
        return xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setOffset(int xOffset, int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public void moveOffset(int xOffset, int yOffset) {
        this.xOffset += xOffset;
        this.yOffset += yOffset;
    }

    public void clear() {
        for (int i = 0; i < getWidth(); i++) {
            for (int j = 0; j < getHeight(); j++) {
                pixels[i + j * getWidth()] = 0xFF00FFFF;
            }
        }
    }

}
