package net.magicmantis.game.entity;

/*
Class: Living

This interface should be implemented by any living Entity. Allows for health and damage.
 */

import net.magicmantis.game.level.*;
import net.magicmantis.game.render.*;

public abstract class LivingEntity extends Entity {

    private int health, maxHealth;
    private int speed;

    public LivingEntity(int x, int y, int width, int height, int spriteIndex, Level level, int maxHealth, int speed) {
        this(x, y, width, height, spriteIndex, level, false, maxHealth, speed);
    }

    public LivingEntity(int x, int y, int width, int height, int spriteIndex, Level level, boolean solid, int maxHealth, int speed) {
        super(x, y, width, height, spriteIndex, level, solid);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.speed = speed;
    }


    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void damage(int damageValue) {
        this.health -= damageValue;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
