package client.chinesecheckers;

import java.net.*;
import java.io.*;

/**
 * Klasa aplikacji klienta
 */
public class ClientApp {

    public static final int DEFAULT_PORT = 4444;

    private String playerNickname;
    private int serverPort;

    private boolean inGame;

    /**
     * Punkt wejścia do aplikacji klienta
     * @param args muszą zawierać kolejno playerNickname oraz opcjonalnie serverPort
     */
    public static void main(String[] args) {
        new ClientApp(args);
    }

    /**
     * Konstruktor ustawiający playerNickname oraz serverPort
     * @param args lista, która powinna zawierać playerNickname oraz opcjonalnie serverPort
     */
    public ClientApp(String[] args) {
        try {
            if (args.length < 1) {
                throw new IllegalArgumentException("Player nickname is required.");
            }
            playerNickname = args[0];

            // Ustaw port, jeśli nie podano ustaw defaultowy
            serverPort = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_PORT;

        } catch (Exception argumentsError) {
            System.out.println("Arguments error: " + argumentsError);
            System.exit(1);
        }

        runClient();
    }

    /**
     * Funckja obsługująca komunikację z serwerem
     */
    private void runClient() {
        try  {
            Socket socket = new Socket("localhost", serverPort);

            // Inicjalizacja komunikacji przez strumienie z serwerem
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("handshake:" + playerNickname);

            // Utworzenie bufora na tekst wpisywany w konsoli
            BufferedReader consoleBufferRead = new BufferedReader(new InputStreamReader(System.in));

            inGame = false;
            boolean expectingResponse = true;

            String serverResponse;
            String readLine;
            String messageToServer;
            do {
                if (expectingResponse) {    
                    serverResponse = in.readLine();
                    // Jeśli response jest null to znaczy że serwer został zamknięty
                    if (serverResponse == null) {
                        break;
                    }
                    System.out.println(serverPort + ":" + playerNickname + " > " + serverResponse);
                }

                System.out.print(serverPort + ":" + playerNickname + " < ");
                readLine = consoleBufferRead.readLine();

                messageToServer = formatMessage(readLine);

                if (!messageToServer.equals("")) {
                    out.println(messageToServer);
                    expectingResponse = true;
                } else {
                    expectingResponse = false;
                }
            } while (!messageToServer.equals("bye"));
            socket.close();

        } catch (UnknownHostException severNotFoundException) {
            System.out.println("ERROR: " + severNotFoundException);

        } catch (IOException IOError) {
            System.out.println("ERROR: " + IOError);
        }
    }

    private String formatMessage(String readLine) {
        readLine = readLine.trim();

        String command;
        String[] args;

        int firstSpaceIndex = readLine.indexOf(' ');
        if (firstSpaceIndex == -1) {
            command = readLine;
            args = new String[0];
        } else {
            command = readLine.substring(0, firstSpaceIndex);
            args = readLine.substring(firstSpaceIndex + 1).trim().split("[\\s,-]");
        }

        String formattedMessage;
        switch (command) {
            case "start":
                if (!inGame) {
                    try {
                        Integer.parseInt(args[0]);
                        formattedMessage = command + " " + args[0];
                    } catch(NumberFormatException e) {
                        System.out.println("Argument error: argument has to be integer");
                        formattedMessage = "";
                    } catch(IndexOutOfBoundsException e) {
                        System.out.println("Argument error: expected start [arg]");
                        formattedMessage = "";
                    }
                } else {
                    System.out.println("You are already in game!");
                    formattedMessage = "";
                }
                break;
                
            case "move":
                if (inGame) {
                    try {
                        Integer.parseInt(args[0]);
                        Integer.parseInt(args[1]);
                        Integer.parseInt(args[2]);
                        Integer.parseInt(args[3]);
                        formattedMessage = command + " " + args[0] + "," + args[1] + "-" + args[2] + "," + args[3];
                    } catch(NumberFormatException e) {
                        System.out.println("Argument error: argument has to be integer");
                        formattedMessage = "";
                    } catch(IndexOutOfBoundsException e) {
                        System.out.println("Argument error: expected move [arg],[arg]-[arg],[arg]");
                        formattedMessage = "";
                    }
                } else {
                    System.out.println("You can't perform this action while not in game!");
                    formattedMessage = "";
                }
                break;

            case "skip":
            case "draw":
                if (inGame) {
                    formattedMessage = command;
                } else {
                    System.out.println("You can't perform this action while not in game!");
                    formattedMessage = "";
                }
                break;

            case "bye":
            case "exit":
            case "quit":
                formattedMessage = "bye";
                break;

            case "help":
                if (inGame) {
                    System.out.println("move [arg],[arg]-[arg][arg] - perform move");
                    System.out.println("draw - draw a board");
                    System.out.println("skip - skip your move");
                } else {
                    System.out.println("start [arg] - start new game for chosen number of players");
                }

            default:
                System.out.println("Unknown command. Try help for list of available commands");
                formattedMessage = "";
                break;
        }

        return formattedMessage;
    }

    public void gameStatus(boolean newStatus) {
        inGame = newStatus;
    }
}