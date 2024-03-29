package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Window extends JFrame {
	// graphics
	private int width = 648;
	private int infoHeight = 80;
	private int windowTop = 22;
	private int height = width + infoHeight - windowTop;
	private int spacing = 3;
	private int squareWidth = 40;

	// game
	private int playerID;
	private int turnCount;
	private boolean disabled;
	private int[][] myMoves;
	private int[][] theirMoves;
	private Color myColor;
	private Color theirColor;
	private String instructions;
	private Board board;
	private int numPlayers;
	private boolean gameStarted = false;
	private ClientSideConnection csc;
	public Menu menu;

	public Window(Menu menu) {
		connectToServer();
		setColors(playerID);
		jFrameSetup();
		this.setVisible(true);
		this.menu=menu;
	}

	public void jFrameSetup() {
		this.setTitle("Gomoku");
		this.setSize(width, height);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

		board = new Board();
		this.setContentPane(board);

		Click click = new Click();
		this.addMouseListener(click);
	}

	public void setDisabled() {
		// System.out.println("playerID = " + playerID + ", turnCount = " + turnCount);
		disabled = playerID == 1 ^ turnCount % 2 != 0;
	}

	public String getInstructions(boolean disabled) {
		if (!gameStarted) {
			return "Waiting for an opponent";
		} else {
			if (disabled) {
				return "Wait for your turn";
			} else {
				return "Your move";
			}
		}
	}

	public static boolean matchFound(int[][] move, int[][] moves) {
		if (moves == null) {
			return false;
		} else {
			for (int i = 0; i < moves.length; i++) {
				if (Arrays.equals(moves[i], move[0])) {
					return true;
				}
			}
		}
		return false;
	}

	// append b to a
	public static int[][] append(int[][] a, int[][] b) {
		if (a == null) {
			return b;
		} else {
			int[][] result = new int[a.length + b.length][];
			System.arraycopy(a, 0, result, 0, a.length);
			System.arraycopy(b, 0, result, a.length, b.length);
			return result;
		}
	}

	public int[][] make2D(int[] a) {
		int[][] result = {a};
		return result;
	}

	public class Board extends JPanel {

		public void fillBackground(Graphics g) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(0, 0, width, height); // Size of upper part of the window is 22
		}

		public void printInstructions(Graphics g) {
			setDisabled();
			g.setColor(Color.WHITE);
			instructions = getInstructions(disabled);
			g.setFont(new Font("tahoma", Font.PLAIN, 30));
			g.drawString(instructions, 20, 50);
		}

		public void checkAndSetColor(Graphics g, int[] positionOnGrid) {
			int[][] move = make2D(positionOnGrid);
			if (matchFound(move, myMoves)) {
				g.setColor(myColor);
			} else if (matchFound(move, theirMoves)) {
				g.setColor(theirColor);
			} else {
				g.setColor(Color.LIGHT_GRAY);
			}
		}

		public void drawSquare(Graphics g, int i, int j) {
			int x, y;
			int[] positionOnGrid = { i, j };
			x = spacing + i * (spacing + squareWidth);
			y = spacing + j * (spacing + squareWidth);
			checkAndSetColor(g, positionOnGrid);
			g.fillRect(x, y + infoHeight, squareWidth, squareWidth);
		}

		public void drawGrid(Graphics g) {
			for (int i = 0; i < 15; i++) {
				for (int j = 0; j < 15; j++) {
					drawSquare(g, i, j);
				}
			}
		}

		// this gets called on window.repaint()
		public void paintComponent(Graphics g) {
			fillBackground(g);
			printInstructions(g);
			drawGrid(g);
		}
	}

	public void setColors(int playerID) {
		if (playerID == 1) {
			myColor = Color.RED;
			theirColor = Color.BLUE;
		} else {
			myColor = Color.BLUE;
			theirColor = Color.RED;
		}
	}

	public void connectToServer() {
		csc = new ClientSideConnection(this);
		Thread t = new Thread(csc);
		t.start();
	}

	// Client Connection
	private class ClientSideConnection implements Runnable {
		private Socket socket;
		private int port;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		private Window window;

		public ClientSideConnection(Window window) {
			this.window = window;
			System.out.println("----Client----");
			//read the port number, from the config text
			try (BufferedReader br = Files.newBufferedReader(Paths.get("config/clientconf.txt")))
			{
				port=Integer.valueOf(br.readLine());
			}
			catch (IOException e)
			{
				System.err.format("File not found", e);
			}
			try {
				socket = new Socket("localhost", port);
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());
				playerID = dataIn.readInt();
				turnCount = dataIn.readInt();
				if (playerID == 2) {
					gameStarted = true;
				}
				System.out.println("You are connected to the server as Player " + playerID);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		public void listen() {
			while (true) {
				try {
					int x = dataIn.readInt();
					//handle win/loss messages: -1 == win, -2 == loss
					if(x<0) {
						switch (x) {
							case -1:
								menu.win();
								window.setVisible(false);
								break;
							case -2:
								menu.lose();
								window.setVisible(false);
								break;
						}
					}
					int y = dataIn.readInt();
					turnCount = dataIn.readInt();
					int[][] move = {{x, y}};
					theirMoves = append(theirMoves, move);
					board.repaint();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		public void waitForOpponent() {
			try {
				numPlayers = dataIn.readInt();
				if (numPlayers == 2) {
					gameStarted = true;
					board.repaint();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		@Override
		public void run() {
			if (playerID == 1 && !gameStarted) {
				waitForOpponent();
			}
			listen();
		}
	}

	public class Click implements MouseListener {

		public int[][] getMove(MouseEvent e) {
			int x = e.getX() / (spacing + squareWidth);
			int y = (e.getY() - infoHeight - windowTop) / (spacing + squareWidth);
			int[][] move = { { x, y } };
			return move;
		}

		public void sendMoveData(int[][] move) {
			try {
				csc.dataOut.writeInt(move[0][0]);
				csc.dataOut.writeInt(move[0][1]);
				csc.dataOut.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		public void addMove(int[][] move) {
			if (!matchFound(move, myMoves) && !matchFound(move, theirMoves)) {
				myMoves = append(myMoves, move);
				sendMoveData(move);
				turnCount += 1;
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (!disabled && gameStarted) {
				int[][] move = getMove(e);
				addMove(move);
				board.repaint();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}
}