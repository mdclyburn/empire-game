package net.magicmantis.game.entity.wall;

import net.magicmantis.game.level.Level;

/*
Class: MovingWall

A subclass of wall that accepts a direction, distance, and speed. It moves between its starting point and a point
that is "distance" away from the start in "direction" at "speed".
 */

public class MovingWall extends Wall {

    private Direction direction;
    private int distance, speed, traveled = 0;

    public enum Direction {
        UP(0, -1), LEFT(-1, 0), RIGHT(1, 0), DOWN(0, 1);
        public final int xMove;
        public final int yMove;
        Direction(int xMove, int yMove) {this.xMove = xMove; this.yMove = yMove;}
        Direction flip() {
            Direction flipped = null;
            if (this == UP) flipped = DOWN;
            else if (this == DOWN) flipped = UP;
            else if (this == LEFT) flipped = RIGHT;
            else if (this == RIGHT) flipped = LEFT;
            return flipped;
        }
    }


    public MovingWall(int x, int y, int width, int height, int spriteIndex, Direction direction, int distance, int speed, Level level) {
        super(x, y, width, height, spriteIndex, level);
        this.direction = direction;
        this.distance = distance;
        this.speed = speed;
    }

    @Override
    public void update() {
        if (traveled < distance) {
            if (moveCollision(direction.xMove*speed, direction.yMove*speed))
            traveled += Math.abs(direction.xMove*speed+direction.yMove*speed);
        }
        else {
            setDirection(getDirection().flip());
            traveled = 0;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
