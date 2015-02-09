package net.magicmantis.game.entity;

/*
Class: Player

This class is handles the player Entity.
 */

import jdk.internal.util.xml.impl.Input;
import net.magicmantis.game.InputHandler;
import net.magicmantis.game.level.*;
import net.magicmantis.game.render.*;

import java.awt.event.KeyEvent;

public class Player extends LivingEntity {

    public static enum PlayerClass {Rouge, Warrior, Mage, Dungeoneer, Ranger}

    public Player(int x, int y, int spriteIndex, Level level) {
        super(x, y, 32, 32, spriteIndex, level, true, 100, 2);
    }

    public void update() {
        if (InputHandler.getKeyPressed(KeyEvent.VK_A) || InputHandler.getKeyPressed(KeyEvent.VK_LEFT))
            moveCollision(-2, 0);
        if (InputHandler.getKeyPressed(KeyEvent.VK_D) || InputHandler.getKeyPressed(KeyEvent.VK_RIGHT))
            moveCollision(2, 0);
        if (InputHandler.getKeyPressed(KeyEvent.VK_W) || InputHandler.getKeyPressed(KeyEvent.VK_UP))
            moveCollision(0, -2);
        if (InputHandler.getKeyPressed(KeyEvent.VK_S) || InputHandler.getKeyPressed(KeyEvent.VK_DOWN))
            moveCollision(0, 2);
    }

    public void draw(Screen screen) {
        screen.render(getX(), getY(), getSpriteIndex());
    }
}
