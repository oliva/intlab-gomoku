package main;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private ServerSocket serverSocket;
    private int numPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private int[][] player1Moves;
    private int[][] player2Moves;
    private int turnCount = 1;

    public Server() {
        System.out.println("----Game Server----");
        numPlayers = 0;
        try {
            serverSocket = new ServerSocket(2222);
        } catch (IOException ex) {
            System.out.println("IOException from Server Constructor");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
            while (numPlayers < 2) {
                Socket socket = serverSocket.accept();
                numPlayers += 1;
                System.out.println("Player #" + numPlayers + " has connected");
                ServerSideConnection ssc = new ServerSideConnection(socket, numPlayers);
                if (numPlayers == 1) {
                    player1 = ssc;
                } else {
                    player2 = ssc;
                }
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("2 players connected. No longer accepting connections");
        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections method");
        }
    }

    public static int[][] append(int[][] a, int[][] b) {
        int[][] result = new int[a.length + b.length][];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private class ServerSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;

        public ServerSideConnection(Socket s, int id) {
            socket = s;
            playerID = id;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                System.out.println("IOException from SSC constructor");
            }
        }

        public int[][] addMove(int[][] move, int[][] moves) {
            if (moves == null) {
                moves = move;
            } else {
                moves = append(moves, move);
            }
            turnCount += 1;
            return moves;
        }

        public void sendData(int[][] move, ServerSideConnection player) {
            if (player != null) {
                try {
                    player.dataOut.writeInt(move[0][0]);
                    player.dataOut.writeInt(move[0][1]);
                    player.dataOut.writeInt(turnCount);
                    player.dataOut.flush();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        public void run() {
            try {
                dataOut.writeInt(playerID);
                dataOut.writeInt(turnCount);
                dataOut.flush();
                while (true) {
                    int x = dataIn.readInt();
                    int y = dataIn.readInt();
                    int[][] move = { { x, y } };
                    if (playerID == 1) {
                        player1Moves = addMove(move, player1Moves);
                        sendData(move, player2);
                    } else {
                        player2Moves = addMove(move, player2Moves);
                        sendData(move, player1);
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException from run() SSC");
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.acceptConnections();
    }
}
