package main;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Server {

    private ServerSocket serverSocket;
    private int numPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private int[][] player1Moves;
    private int[][] player2Moves;
    private Gamestate gamestate;
    private int port;
    public Server() {
        System.out.println("----Game Server----");
        gamestate = new Gamestate();
        numPlayers = 0;
    	
        try (BufferedReader br = Files.newBufferedReader(Paths.get("config/serverconf.txt"))) //read the port number, from the config text
        {
        	port=Integer.valueOf(br.readLine());
        } 
        catch (IOException e) 
        {
            System.err.format("File not found", e);
        }

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            ex.printStackTrace();
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
            ex.printStackTrace();
        }
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
                ex.printStackTrace();
            }
        }

        public int[][] addMove(int[][] move, int[][] moves) {
            moves = append(moves, move);
            //TODO
            gamestate.turn++;
            //gamestate.place();
            return moves;
        }

        public void sendData(int[][] move, ServerSideConnection player) {
            if (player != null) {
                try {
                    player.dataOut.writeInt(move[0][0]);
                    player.dataOut.writeInt(move[0][1]);
                    player.dataOut.writeInt(gamestate.turn+1);
                    player.dataOut.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void sendNumPlayers(int numPlayers, ServerSideConnection player) {
            try {
                player.dataOut.writeInt(numPlayers);
                player.dataOut.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void sendInitData() {
            try {
                dataOut.writeInt(playerID);
                dataOut.writeInt(gamestate.turn+1);
                dataOut.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void addOpponentMoves(int[][] move) {
            if (playerID == 1) {
                player1Moves = addMove(move, player1Moves);
                sendData(move, player2);
            } else {
                player2Moves = addMove(move, player2Moves);
                sendData(move, player1);
            }
        }

        public void listen() {
            while (true) {
                try {
                    int x = dataIn.readInt();
                    int y = dataIn.readInt();
                    int[][] move = { { x, y } };
                    addOpponentMoves(move);
                } catch (IOException ex) {
                    System.out.println("Player " + playerID + " disconnected");
                    break;
                }
            }
        }

        public void run() {
            sendInitData();
            if (playerID == 2) {
                sendNumPlayers(numPlayers, player1);
            }
            listen();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.acceptConnections();
    }
}
