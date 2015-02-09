package net.magicmantis.game;

/*
Class: Game

This is the class that handles all game logic and rendering.
 */

import net.magicmantis.game.level.*;
import net.magicmantis.game.menu.Action;
import net.magicmantis.game.menu.ButtonImpl;
import net.magicmantis.game.menu.Menu;
import net.magicmantis.game.render.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class Game extends Canvas implements Runnable {

    private int width, height;
    private boolean running = false;
    private boolean paused = true;

    //rendering variables
    private BufferedImage buffer;
    private BufferStrategy bufferStrategy;
    private int[] pixels;
    private Screen screen;
    private Menu menu;

    //input handling variables
    private InputHandler input;
    private static int mouseX = 0, mouseY = 0;
    private static boolean mousePressed = false;

    //level
    private Level level;

    public Game(int width, int height) {
        this.width = width;
        this.height = height;

        setupSwing();
        setupRendering();
        setupInput();
        setupMenu();
        //setupLevel();
    }


    private void setupSwing() {
        this.setPreferredSize(new Dimension(getWidth(), getHeight()));
        this.setSize(new Dimension(getWidth(), getHeight()));

        JFrame frame = new JFrame("Game Name");

        frame.setResizable(true);
        frame.setPreferredSize(new Dimension(getWidth(), getHeight()));
        frame.setSize(new Dimension(getWidth(), getHeight()));

        frame.setFocusable(true);
        frame.setResizable(false);
        frame.setUndecorated(false);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(this, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        this.requestFocus();
    }

    private void setupRendering() {
        buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        pixels = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();

        screen = new Screen(width, height, new SpriteSheet(32, 32, "/main/resources/SpriteSheet.png"));
    }

    private void setupInput() {
        input = new InputHandler(this);
        mousePressed = false;
        mouseX = 0;
        mouseY = 0;
    }

    private void setupMenu() {
        menu = new Menu("Title");
        menu.addButton(new ButtonImpl(getWidth()/2, getHeight()/2, 64, 32, 0, new Action(){
            public void execute() {
                setupLevel();
                setPaused(false);
            }
        }));
    }

    private void setupLevel() {
        level = new Level(1000, 1000, this);
    }

    public synchronized void start() {
        this.setRunning(true);
        new Thread(this).start();
    }

    public synchronized void stop() {
        this.setRunning(false);
    }

    //handles performance, running, and calls to tick() and render()
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000d / 60d;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;

        while (running) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / nsPerTick;
            lastTime = currentTime;
            boolean shouldRender = true;

            while (delta >= 1) {
                ticks++;
                tick();
                delta -= 1;
                shouldRender = false;
            }

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldRender == true) {
                frames++;
                render();
            }

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                System.out.println(frames + " " + ticks);
                frames = 0;
                ticks = 0;
            }
        }
    }

    //handles game logic
    private void tick() {

        if (!paused) {
            level.update();
        }
        else {
            menu.update();
        }
    }

    //render the game to the screen
    private void render() {
        //ensure valid buffer strategy
        bufferStrategy = getBufferStrategy();
        if (bufferStrategy == null) {
            createBufferStrategy(3);
            return;
        }

        //clear screen
        screen.clear();

        //render logic
        if (!paused)
        level.draw(screen);
        else
        menu.draw(screen);

        //apply screen pixels to game pixels
        for (int y = 0; y < screen.getHeight(); y++) {
            for (int x = 0; x < screen.getWidth(); x++) {
                pixels[x+y*width] = screen.pixels[x+y*screen.getWidth()];
            }
        }

        //show buffer
        Graphics g = bufferStrategy.getDrawGraphics();
        g.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);

        //draw HUD
        if (level != null && level.getPlayer() != null) {
            g.fillRect(10, 10, level.getPlayer().getHealth(), 10);
        }

        g.dispose();
        bufferStrategy.show();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void updateMouse(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void pressMouse(MouseEvent e) {
        mousePressed = true;
        if (paused && menu != null) {
            menu.click();
        }
    }

    public void releaseMouse(MouseEvent e) {
        mousePressed = false;
    }

    public static int getMouseX() {
        return mouseX;
    }

    public static int getMouseY() {
        return mouseY;
    }
}
