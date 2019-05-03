package main;

public class Game implements Runnable {
    public static void main(String[] args) {
        Menu menu = new Menu(1);
    }

    @Override
    public void run() {
        Window window = new Window();
    }
}