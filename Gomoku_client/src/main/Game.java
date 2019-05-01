package main;

public class Game implements Runnable {

    Window window = new Window();

    public static void main(String[] args) {
        new Thread(new Game()).start();
    }

    @Override
    public void run() {
        while (true) {
            window.repaint();
        }
    }
}