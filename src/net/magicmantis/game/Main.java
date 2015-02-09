package net.magicmantis.game;

/*
Class: Main

This class creates the Screen and puts a new instance of Game in it.
Used primarily as an entry point with editable HEIGHT and WIDTH values;
 */

public class Main {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    public static void main(String args[]) {

        Game game = new Game(WIDTH, HEIGHT);
        game.start();

    }

}