package net.magicmantis.game.menu;

/*
Class: Menu

This class handles all menu screens.
 */

import net.magicmantis.game.render.Screen;

import java.util.ArrayList;

public class Menu {

    private String title;
    private ArrayList<ButtonImpl> buttons;

    public Menu(String title) {
        this.title = title;
        buttons = new ArrayList<ButtonImpl>();
    }

    public void update() {
        for (int i = 0; i < buttons.size(); i++) {
            ButtonImpl b = buttons.get(i);
            b.update();
        }
    }

    public void draw(Screen screen) {
        for (int i = 0; i < buttons.size(); i++) {
            ButtonImpl b = buttons.get(i);
            b.draw(screen);
        }
    }

    public void click() {
        for (int i = 0; i < buttons.size(); i++) {
            ButtonImpl b = buttons.get(i);
            if (b.getFocus())
                b.onClicked();
        }
    }

    public void addButton(ButtonImpl b) {
        buttons.add(b);
    }

    public String getTitle() {
        return title;
    }


}
