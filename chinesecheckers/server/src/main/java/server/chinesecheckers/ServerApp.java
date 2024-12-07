package server.chinesecheckers;

import java.net.*;

public class ServerApp {

    public static final int DEFAULT_PORT = 4444;
    public static final int MAX_PLAYERS = 6;

    public static int clientCount;
    public static ClientThread[] players;

    public static void main( String[] args ) {
        int port = DEFAULT_PORT;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Info: Server listening on port " + port);

            players = new ClientThread[MAX_PLAYERS + 1];
            clientCount = 0;

            while (true) {
                Socket socket = serverSocket.accept();

                if (clientCount < MAX_PLAYERS) {
                    players = updatePlayers(socket, true);
                    clientCount = updateClientCount();
                }
                else {
                    //komunikat do klienta o rozlaczeniu z powodu limitu
                    socket.close();
                    //zakonczenie programu klienta
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            players = updatePlayers(null, false);
            clientCount = updateClientCount();
        }
    }

    public static ClientThread[] updatePlayers(Socket socket, boolean add) {
        ClientThread[] result = new ClientThread[MAX_PLAYERS + 1];
        int index = 1;

        for (int i = 1; i <= MAX_PLAYERS; i++) {
            if (players[i] != null && players[i].getState() != Thread.State.TERMINATED) {
                result[index] = players[i];
                result[index].changePlayerNumber(index);
                index++;
            }
        }

        if (add) {
            result[index] = new ClientThread(socket, index);
            result[index].start();
            System.out.println("info: New player joined with number: " + index);
        }

        return result;
    }

    public static int updateClientCount() {
        int result = 0;
        
        for (int i = 1; i <= MAX_PLAYERS; i++) {
            if (players[i] != null) {
                result++;
            }
        }

        return result;
    }
}