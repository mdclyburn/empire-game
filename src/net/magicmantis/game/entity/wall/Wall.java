package net.magicmantis.game.entity.wall;

/*
Class: Wall

The superclass for all walls.
 */

import net.magicmantis.game.entity.Entity;
import net.magicmantis.game.level.*;
import net.magicmantis.game.render.*;

public class Wall extends Entity {

    public Wall(int x, int y, int width, int height, int spriteIndex, Level level) {
        super(x, y, width, height, spriteIndex, level, true);
    }

    public void update() {

    }

    public void draw(Screen screen) {
        screen.render(getX(), getY(), getSpriteIndex());
    }
}
