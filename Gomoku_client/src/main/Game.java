package main;

public class Game implements Runnable {
    public static void main(String[] args) {
        Menu menu = new Menu();
    }

    @Override
    public void run() {
        Window window = new Window();
    }
}