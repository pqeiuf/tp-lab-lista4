package server.chinesecheckers;

import java.net.*;

public class ServerApp {
    public static void main( String[] args ) {
        int port = 4444;
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server listening on port: " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New player joined.");
                new ClientThread(socket).start();
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
        }
    }
}
