package net.magicmantis.game.entity;

/*
Class: Entity

This class is the master class for all the Game Objects that will be in the game world.
 */

import net.magicmantis.game.level.Level;
import net.magicmantis.game.render.*;

public abstract class Entity {

    private int x, y;
    private int width, height;
    private int spriteIndex;
    private boolean solid;
    private Level level;

    public Entity(int x, int y, int width, int height, int spriteIndex, Level level) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.spriteIndex = spriteIndex;
        this.level = level;
        this.solid = false;
    }

    public Entity(int x, int y, int width, int height, int spriteIndex, Level level, boolean solid) {
        this(x, y, width, height, spriteIndex, level);
        this.solid = solid;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    //moves the Entity to absolute position
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //moves the Entity relative to current position
    public void move(int xMove, int yMove) {
        this.x += xMove;
        this.y += yMove;
    }

    //move relative with collision checking
    public boolean moveCollision(int xMove, int yMove) {
        for (int i = 0; i < level.getEntityList().size(); i++) {
            Entity e = level.getEntityList().get(i);
            if (!e.getSolid() || e == this) continue;
            if (checkCollision(e, x+xMove, y))
                xMove = 0;
            if (checkCollision(e, x, y+yMove))
                yMove = 0;
        }
        move(xMove, yMove);
        if (xMove+yMove != 0) return true;
        return false;
    }

    public boolean checkCollision(Entity e, int x, int y) {
        if (x+width > e.x && x < e.x+e.width && y+height > e.y && y < e.y+e.height)
            return true;
        return false;
    }

    public boolean checkCollision(Entity e) {
        return checkCollision(e, x, y);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSpriteIndex() {
        return spriteIndex;
    }

    public boolean getSolid() {
        return solid;
    }

    public abstract void update();
    public abstract void draw(Screen screen);
}
