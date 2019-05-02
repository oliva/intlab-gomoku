package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
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
    private boolean playerReady = false;
    private boolean opponentReady = false;
    private boolean gameStarted = false;
    private boolean playerWonGame = false;
    private Board board;
    private ClientSideConnection csc;

    public Window() {
        connectToServer();
        this.setTitle("Gomoku");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        board = new Board();
        this.setContentPane(board);

        Click click = new Click();
        this.addMouseListener(click);

        this.setVisible(true);
    }

    // append b to a
    public int[][] append(int[][] a, int[][] b) {
        int[][] result;
        if (a == null) {
            result = b;
        } else {
            result = new int[a.length + b.length][];
            System.arraycopy(a, 0, result, 0, a.length);
            System.arraycopy(b, 0, result, a.length, b.length);
        }
        return result;
    }

    public boolean matchFound(int[][] move, int[][] moves) {
        if (moves == null) {
            return false;
        }
        for (int i = 0; i < moves.length; i++) {
            if (Arrays.equals(moves[i], move[0])) {
                return true;
            }
        }
        return false;
    }

    public void setDisabled() {
        disabled = (playerID == 1) ^ (turnCount % 2 != 0);
    }

    public String getInstructions(boolean disabled) {
        if (disabled) {
            return "Wait for your turn";
        } else {
            return "Your move";
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
        csc = new ClientSideConnection();
        Thread t = new Thread(csc);
        t.start();
    }

    public void addToTheirMoves(int[][] move) {
        if (theirMoves == null) {
            theirMoves = move;
        } else {
            theirMoves = append(theirMoves, move);
        }
    }
    
    public class Board extends JPanel {

        void renderMenu(Graphics g) {
            setTitle("Menu");
            g.setColor(Color.WHITE);
            int xStart = (width - 200) / 2;
            int yStart = 100;
            g.drawRect(xStart, yStart, 200, 80);
            g.setFont(new Font("tahoma", Font.PLAIN, 30));
            g.drawString("Start", xStart + 65, yStart + 50);

            int xCredits = (width - 200) / 2;
            int yCredits = 250;
            g.drawRect(xCredits, yCredits, 200, 80);
            g.setFont(new Font("tahoma", Font.PLAIN, 30));
            g.drawString("Credits", xCredits + 50, yStart + 200);
        }

        void renderWaitingMessage(Graphics g) {
            
        }
        
        public void paintComponent(Graphics g) {
            
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, width, height);

            if (!gameStarted) {
                renderMenu(g);
            } else {
                int x, y;
                for (int i = 0; i < 15; i++) {
                    for (int j = 0; j < 15; j++) {
                        g.setColor(Color.LIGHT_GRAY);
                        int[] pos = { i, j };
                        x = spacing + i * (spacing + squareWidth);
                        y = spacing + j * (spacing + squareWidth);
                        if (myMoves != null) {
                            for (int n = 0; n < myMoves.length; n++) {
                                if (Arrays.equals(myMoves[n], pos)) {
                                    g.setColor(myColor);
                                }
                            }
                        }
                        if (theirMoves != null) {
                            for (int n = 0; n < theirMoves.length; n++) {
                                if (Arrays.equals(theirMoves[n], pos)) {
                                    g.setColor(theirColor);
                                }
                            }
                        }
                        g.fillRect(x, y + infoHeight, squareWidth, squareWidth);
                    }
                }
                g.setColor(Color.WHITE);
                setDisabled();
                instructions = getInstructions(disabled);
                g.setFont(new Font("tahoma", Font.PLAIN, 30));
                g.drawString(instructions, 20, 50);
            }
        }
    }

    public class ClientSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection() {
            System.out.println("----Client----");
            try {
                socket = new Socket("localhost", 2222);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                turnCount = dataIn.readInt();
                System.out.println("You are connected to the server as Player " + playerID);
                setColors(playerID);
            } catch (Exception e) {
                System.out.println("IOException from CSC constructor");
            }
        }

        public void receiveData() {
            // System.out.println(gameStarted + " (receiveData)");
            try {
                if (opponentReady) {
                    System.out.println("\n\nreceiveData() block (opponent not ready branch):");
                    if (!playerReady){
                        int numPlayersReady = dataIn.readInt();
                        System.out.println("numPlayersReady = " + numPlayersReady);
                        gameStarted = numPlayersReady == 2;
                        System.out.println("playerReady = " + playerReady);
                        playerReady = gameStarted;
                        System.out.println("gameStarted = " + gameStarted);
                    } else {
    
                        int x = dataIn.readInt();
                        int y = dataIn.readInt();
                        int[][] move = { { x, y } };
                        addToTheirMoves(move);
                        turnCount = dataIn.readInt();
                    }
                } else {
                    System.out.println("\n\nreceiveData() block (opponent not ready branch):");
                    System.out.println("playerReady = " + playerReady);
                    int numPlayersReady = dataIn.readInt();
                    System.out.println("numPlayersReady = " + numPlayersReady);
                    opponentReady = true;
                    if (numPlayersReady == 2) {
                        playerReady = true;
                    }
                    System.out.println("opponentReady = " + opponentReady);
                    System.out.println("playerReady = " + playerReady);
                    gameStarted = (numPlayersReady == 2);
                    System.out.println("gameStarted = " + gameStarted);
                }
                board.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                receiveData();
            }
        }
    }

    public class Click implements MouseListener {

        public int[][] getMove(MouseEvent e) {
            int x = e.getX() / (spacing + squareWidth);
            int y = (e.getY() - infoHeight - windowTop) / (spacing + squareWidth);
            int[][] move = { { x, y } };
            return move;
        }

        public void sendMoveData(int x, int y) {
            try {
                csc.dataOut.writeInt(x);
                csc.dataOut.writeInt(y);
                csc.dataOut.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void addToMyMoves(int[][] move) {
            boolean noConflict = !matchFound(move, myMoves) && !matchFound(move, theirMoves);
            if (noConflict) {
                myMoves = append(myMoves, move);
                sendMoveData(move[0][0], move[0][1]);
                turnCount += 1;
            }
        }

        public boolean startButtonClicked(MouseEvent e) {
            boolean inXRange = e.getX() >= (width - 200) / 2 && e.getX() <= (width - 200) / 2 + 200;
            boolean inYRange = e.getY() >= 100 + windowTop && e.getY() <= 180 + windowTop;
            if (inXRange && inYRange) {
                return true;
            } else {
                return false;
            }
        }

        public boolean creditsButtonClicked(MouseEvent e) {
            boolean inXRange = e.getX() >= (width - 200) / 2 && e.getX() <= (width - 200) / 2 + 200;
            boolean inYRange = e.getY() >= 250 + windowTop && e.getY() <= 330 + windowTop;
            if (inXRange && inYRange) {
                return true;
            } else {
                return false;
            }
        }

        public void addPlayer() {
            try {
                System.out.println("player" + playerID + "sending a 1 to the server");
                csc.dataOut.writeInt(1);
                csc.dataOut.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // System.out.println(gameStarted + " (mouseClicked)");
            if (gameStarted) {
                if (!disabled) {
                    int[][] move = getMove(e);
                    addToMyMoves(move); // also sends to server with sendMoveData() method
                    board.repaint();
                }
            } else {
                if (startButtonClicked(e)) {
                    System.out.println("\n\nmouseClicked() block:");
                    playerReady = true;
                    System.out.println("playerReady = " + playerReady);
                    // System.out.println("player " + playerID + " is ready");
                    gameStarted = opponentReady && playerReady;
                    System.out.println("gameStarted = " + gameStarted);
                    addPlayer();
                } else if (creditsButtonClicked(e)) {
                    
                }
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
