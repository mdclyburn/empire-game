package net.magicmantis.game.menu;

/*
Class: Button

Class handles code for creating, interacting with, and rendering buttons on menus.
 */

import net.magicmantis.game.Game;
import net.magicmantis.game.render.Screen;

public class ButtonImpl {

    private String text;
    private int x, y;
    private int width, height;
    private int spriteWidth, spriteHeight;
    private int spriteIndex;
    private boolean focus;

    private Action action;

    public ButtonImpl(int x, int y, int width, int height, int spriteIndex, Action action) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.action = action;
        this.focus = false;
    }

    public void onClicked() {
        action.execute();
    }

    public void update() {
        if (Game.getMouseX() >= getX() && Game.getMouseX() < getX()+getWidth()
                && Game.getMouseY() >= getY() && Game.getMouseY() < getY()+getHeight())
            focus = true;
        else
            focus = false;
    }

    public void draw(Screen screen) {
        screen.render(getX(), getY(), getSpriteIndex(), getWidth(), getHeight());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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

    public boolean getFocus() {
        return focus;
    }

    public void setSpriteIndex(int spriteIndex) {
        this.spriteIndex = spriteIndex;
    }

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getSpriteHeight() {
        return spriteHeight;
    }
}
