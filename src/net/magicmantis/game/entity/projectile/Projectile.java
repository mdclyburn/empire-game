package net.magicmantis.game.entity.projectile;

import net.magicmantis.game.entity.Entity;
import net.magicmantis.game.level.Level;

/*
Class: Projectile

This class parents all projectile objects.
 */
public abstract class Projectile extends Entity {

    private double direction, speed;

    //default 1 pixel
    public Projectile(int x, int y, int spriteIndex, double direction, double speed, Level level) {
        super(x, y, 1, 1, spriteIndex, level, false);
        this.direction = direction;
        this.speed = speed;
    }

    //custom width projectile
    public Projectile(int x, int y, int width, int height, int spriteIndex, Level level) {
        super(x, y, width, height, spriteIndex, level, false);
    }

    public double getDirection() {
        return direction;
    }

    public void setDirection (double direction) {
        this.direction = direction;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
