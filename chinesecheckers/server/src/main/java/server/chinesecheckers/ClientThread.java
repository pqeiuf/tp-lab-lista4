package server.chinesecheckers;

import java.net.*;
import java.io.*;

/**
 * Klasa wątku obsługującego komunikację z klientem (graczem)
 */
public class ClientThread extends Thread {

    private Socket socket;
    private int playerNumber;
    private ServerApp server;

    /**
     * Konstruktor do ustawienia socketu komunikacji z klientem oraz przypisanego wstępnie numeru gracza
     * @param socket
     * @param playerNumber
     */
    public ClientThread(Socket socket, int playerNumber, ServerApp server) {
        this.socket = socket;
        this.playerNumber = playerNumber;
        this.server = server;
    }

    @Override
    public void run() {
        try  {
            // Inicjalizacja komunikacji przez strumienie z klientem
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String clientMessage;

            clientMessage = in.readLine();
            System.out.println("player:" + playerNumber + " > " + clientMessage);
            out.println(clientMessage);

            do {
                clientMessage = in.readLine();
                // Jeśli response jest null to znaczy że serwer został zamknięty
                if (clientMessage == null) {
                    System.out.println("player:" + playerNumber + " DISCONNECTED ");
                    break;
                }
                System.out.println("player:" + playerNumber + " > " + clientMessage);
                
                String response = response(clientMessage);
                out.println(response);

            } while (!clientMessage.equals("exit"));

            server.updatePlayers(socket, false);
            server.updateClientCount();
            socket.close();

        } catch (UnknownHostException severNotFoundException) {
            System.out.println("ERROR: " + severNotFoundException);
        }
        
        catch (IOException IOError) {
            System.out.println("ERROR: " + IOError);
        }
    }

    private String response(String message) {
        String command;
        String argument;

        int spaceIndex = message.indexOf(' ');
        if (spaceIndex == -1) {
            command = message;
            argument = "";
        } else {
            command = message.substring(0, spaceIndex);
            argument = message.substring(spaceIndex + 1);
        }

        String response;
        switch (command) {
            case "exit":
                response = command;
                break;
            case "start":
                int arg = Integer.parseInt(argument);
                if (server.game != null) {
                    response = "The game is already being played";
                }
                else if (arg == server.clientCount) {
                    server.game = new Game(1, arg);
                    response = "Started game for " + arg + " players";
                } else {
                    response = "Invalid number of players (currently " + server.clientCount + ")";
                }
                break;
            case "draw":
                if (server.game != null) {
                    response = server.game.execute(playerNumber + "." + command);
                } else {
                    response = "There is no game currently being played";
                }
                break;
            case "skip":
                if (server.game != null) {
                    response = "Skipped your turn";
                } else {
                    response = "There is no game currently being played";
                }
                break;
            case "move":
                if (server.game != null) {
                    response = server.game.execute(playerNumber + "." + command + " " + argument);
                } else {
                    response = "There is no game currently being played";
                }
                break;
            default:
                response = "Unknown command.";
                break;
        }

        return response;
    }

    /**
     * Metoda aktualizująca numer gracza
     * @param newPlayerNumber
     */
    public void changePlayerNumber(int newPlayerNumber) {
        playerNumber = newPlayerNumber;
    }
}
