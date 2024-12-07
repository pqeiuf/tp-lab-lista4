package server.chinesecheckers;

import java.net.*;

public class ServerApp {

    public static final int DEFAULT_PORT = 4444;
    public static final int MAX_PLAYERS = 6;

    public static void main( String[] args ) {
        int port = DEFAULT_PORT;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("info: Server listening on port " + port);

            ClientThread[] players = new ClientThread[MAX_PLAYERS + 1];
            int clientCount = 0;

            while (true) {
                Socket socket = serverSocket.accept();

                if (clientCount < MAX_PLAYERS) {
                    players = updatePlayers(players, socket);
                    clientCount = updateClientCount(players);
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

    public static ClientThread[] updatePlayers(ClientThread[] players, Socket socket) {
        ClientThread[] result = new ClientThread[MAX_PLAYERS + 1];
        int index = 1;

        for (int i = 1; i <= MAX_PLAYERS; i++) {
            if (players[i] != null && players[i].getState() != Thread.State.TERMINATED) {
                result[index] = players[i];
                result[index].changePlayerNumber(index);
                index++;
            }
        }

        result[index] = new ClientThread(socket, index);
        result[index].start();

        System.out.println("info: New player joined with number: " + index);

        return result;
    }

    public static int updateClientCount(ClientThread[] players) {
        int result = 0;
        
        for (int i = 1; i <= MAX_PLAYERS; i++) {
            if (players[i] != null) {
                result++;
            }
        }

        return result;
    }
}
