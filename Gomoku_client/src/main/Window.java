package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
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

    public class Board extends JPanel {

        public void paintComponent(Graphics g) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, width, height); // Size of upper part of the window is 22
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

    public void setColors(int playerID) {
        if (playerID == 1) {
            myColor = Color.RED;
            theirColor = Color.BLUE;
        } else {
            myColor = Color.BLUE;
            theirColor = Color.RED;
        }
    }

    public static int[][] append(int[][] a, int[][] b) {
        int[][] result = new int[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    public static boolean matchFound(int[][] move, int[][] myMoves) {
        for (int i = 0; i < myMoves.length; i++) {
            if (Arrays.equals(myMoves[i], move[0])) {
                return true;
            }
        }
        return false;
    }

    public void connectToServer() {
        // System.out.println("started connection to server");
        csc = new ClientSideConnection();
        Thread t = new Thread(csc);
        t.start();
        // System.out.println("ended connection to server");
    }

    // Client Connection
    private class ClientSideConnection implements Runnable {
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
            } catch (IOException ex) {
                System.out.println("IOException from CSC constructor");
            }
        }

        public void receiveData() {
            try {
                int x = dataIn.readInt();
                int y = dataIn.readInt();
                turnCount = dataIn.readInt();
                int[][] move = { { x, y } };
                if (theirMoves == null) {
                    theirMoves = move;
                } else {
                    theirMoves = append(theirMoves, move);
                }
                board.repaint();
            } catch (Exception e) {
                // TODO: handle exception
                System.out.println("IOException from CSC: receiveData() method");
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

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!disabled) {
                int x = e.getX() / (spacing + squareWidth);
                int y = (e.getY() - infoHeight - windowTop) / (spacing + squareWidth);
                int[][] move = { { x, y } };
                if (myMoves == null) {
                    try {
                        csc.dataOut.writeInt(x);
                        csc.dataOut.writeInt(y);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    myMoves = move;
                    turnCount += 1;
                } else {
                    if (!matchFound(move, myMoves)) {
                        myMoves = append(myMoves, move);
                        try {
                            csc.dataOut.writeInt(x);
                            csc.dataOut.writeInt(y);
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        turnCount += 1;
                    }
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
