package server.chinesecheckers;

import java.net.*;

public class ServerApp {

    public static final int MAX_PLAYERS = 6;

    public static void main( String[] args ) {
        int port = 4444;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " + port);

            int clientCount = 0;

            while (true) {
                Socket socket = serverSocket.accept();
                if (clientCount < MAX_PLAYERS) {
                    System.out.println("New player joined.");
                    new ClientThread(socket).start();
                    clientCount++;
                }
                else {
                    //komunikat do klienta o rozlaczeniu z powodu limitu
                    socket.close();
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }
}
