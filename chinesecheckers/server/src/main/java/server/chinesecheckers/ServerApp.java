package server.chinesecheckers;

import java.net.*;

/**
 * Główna klasa aplikacji serwera
 */
public class ServerApp {

    public static final int DEFAULT_PORT = 4444;
    public static final int MAX_PLAYERS = 6;

    public int clientCount;
    public ClientThread[] players;
    public GameEngine game;

    public static void main( String[] args ) {
        new ServerApp();
    }

    private ServerApp() {
        runServer();
    }

    private void runServer() {
        int port = DEFAULT_PORT;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Info: Server listening on port " + port);

            players = new ClientThread[MAX_PLAYERS + 1];
            clientCount = 0;
            game = new GameEngine(this);

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

    public ClientThread[] updatePlayers(Socket socket, boolean add) {
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
            result[index] = new ClientThread(socket, index, this);
            result[index].start();
            System.out.println("Info: New player joined with number: " + index);
        }

        return result;
    }

    public int updateClientCount() {
        int result = 0;
        
        for (int i = 1; i <= MAX_PLAYERS; i++) {
            if (players[i] != null) {
                result++;
            }
        }

        return result;
    }

    /**
     * Metoda do printowania wszystkim klientom tego co wysłał jeden z nich
     * Pierwotny wysyłacz nie otrzymuje tutaj wiadomości ponieważ dla niego jest generowana inna wiadomość
     */
    public void printForAllExcept(String message, ClientThread excludedSender) {
        for (int i = 1; i <= clientCount; i++) {
            if (players[i] != null && players[i].getState() != Thread.State.TERMINATED && players[i] != excludedSender) {
                players[i].printMessage(message);
            }
        }
    }
}
