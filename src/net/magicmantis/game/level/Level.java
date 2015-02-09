package net.magicmantis.game.level;

/*
Class: Level

This class holds the information about the current world and entities.
 */

import net.magicmantis.game.*;
import net.magicmantis.game.entity.*;
import net.magicmantis.game.entity.wall.*;
import net.magicmantis.game.render.*;

import java.util.ArrayList;
import java.util.List;

public class Level {

    private int width, height;
    private Game game;
    private List<Entity> entityList;
    private Player player;

    public Level(int width, int height, Game game) {
        this.width = width;
        this.height = height;
        this.game = game;

        entityList = new ArrayList<Entity>();

        generateLevel();
        generateEntities();
    }

    private void generateLevel() {
        for (int i = 0; i < 10; i ++) {
            addEntity(new Wall(i * 32, 128, 32, 32, 1, this));
        }
        addEntity(new MovingWall(400, 400, 32, 32, 0, MovingWall.Direction.UP, 100, 2, this));
    }

    private void generateEntities() {
        player = new Player(100, 32, 2, this);
        addEntity(player);
    }

    public void update() {
        for (int i = 0; i < entityList.size(); i++) {
            Entity e = entityList.get(i);
            e.update();
        }
    }

    public void draw(Screen screen) {
        for (int i = 0; i < 20; i++)
            for (int j = 0; j < 20; j++)
                screen.render(i << 5, j << 5, 5);
        for (int i = 0; i < entityList.size(); i++) {
            Entity e = entityList.get(i);
            e.draw(screen);
        }
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public Entity getEntity(int index) {
        return entityList.get(index);
    }

    public void addEntity(Entity e) {
        if (!entityList.contains(e))
        entityList.add(e);
    }

    public Player getPlayer() {
        return player;
    }

}