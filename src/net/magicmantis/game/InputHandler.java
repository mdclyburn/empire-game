package net.magicmantis.game;

/*
Class: InputHandler

Handles all input. Given an instance of Game so it can affect gameplay.
 */

import java.awt.event.*;
import java.util.ArrayList;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener {

    //array of keys (true = pressed; false = not pressed)
    public static boolean[] keys = new boolean[100];
    private Game game;

    public InputHandler(Game game) {
        this.game = game;
        game.addKeyListener(this);
        game.addMouseListener(this);
        game.addMouseMotionListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    //returns weather the key associated with keyCode is currently pressed
    public static boolean getKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        game.pressMouse(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        game.releaseMouse(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        game.updateMouse(e);
    }

}
