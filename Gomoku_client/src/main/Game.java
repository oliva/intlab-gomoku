package main;

public class Game implements Runnable {
	public Menu menu;
	public static void main(String[] args) {
		Menu menu = new Menu();
	}

	@Override
	public void run() {
		Window window = new Window(menu);
	}
	public void addmenu(Menu menu) //gives the menu to the game so it can pass it to the window
	{
		this.menu=menu;
	}
}