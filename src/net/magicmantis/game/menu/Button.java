package net.magicmantis.game.menu;

/*
Interface: Button

This interface provides the API for buttons used in menus.
 */

import net.magicmantis.game.render.Screen;

public interface Button {

    //action to be performed upon clicking the button
    public void onClicked();

    //any necessary updates made each tick
    public void update();

    //draw button image
    public void draw(Screen screen);

    //get fuctions
    public int getX();
    public int getY();
    public int getWidth();
    public int getHeight();
    public int getSpriteIndex();

}
