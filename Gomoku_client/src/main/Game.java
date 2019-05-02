package main;

public class Game implements Runnable {

    Window window = new Window();

    public static void main(String[] args) {
    	Menu menu=new Menu(1); // main menu
    	//new Thread(new Game()).start();
    }

    @Override
    public void run() {
        while (true) {
            window.repaint();
        }
    }
}