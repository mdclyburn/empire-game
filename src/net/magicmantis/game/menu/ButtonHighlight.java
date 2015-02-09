package net.magicmantis.game.menu;

/*
Class: ButtonHighLight

Wrapper for the Button class that adds a second sprite for when the mouse is hovering over it.
 */

import net.magicmantis.game.Game;
import net.magicmantis.game.render.Screen;

public class ButtonHighlight implements Button {

    private int spriteIndexLow, spriteIndexHigh;
    private ButtonImpl button;
    private boolean focus;

    public ButtonHighlight(ButtonImpl b, int spriteIndexHigh) {
        this.button = b;
        this.spriteIndexHigh = spriteIndexHigh;
        this.spriteIndexLow = b.getSpriteIndex();
        this.focus = false;
    }

    public void onClicked() {
        button.onClicked();
    }

    public void update() {
        button.update();
    }

    public void draw(Screen screen) {
        if (button.getFocus())
            button.setSpriteIndex(spriteIndexHigh);
        else
            button.setSpriteIndex(spriteIndexLow);
        button.draw(screen);
    }

    public int getX() {
        return button.getX();
    }

    public int getY() {
        return button.getY();
    }

    public int getWidth() {
        return button.getWidth();
    }

    public int getHeight() {
        return button.getHeight();
    }

    public int getSpriteIndex() {
        return button.getSpriteIndex();
    }

}
