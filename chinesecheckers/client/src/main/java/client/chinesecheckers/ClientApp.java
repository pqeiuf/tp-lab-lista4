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

            out.println("Hello " + playerNickname);

            // Utworzenie bufora na tekst wpisywany w konsoli
            BufferedReader consoleBufferRead = new BufferedReader(new InputStreamReader(System.in));

            boolean skipResponse = false;

            String serverResponse;
            String messageToServer;
            do {
                if (!skipResponse) {
                    serverResponse = in.readLine();
                    // Jeśli response jest null to znaczy że serwer został zamknięty
                    if (serverResponse == null) {
                        break;
                    }
                    System.out.println(serverPort + ":" + playerNickname + " > " + serverResponse);
                }
                skipResponse = false;

                System.out.print(serverPort + ":" + playerNickname + " < ");
                messageToServer = formatMessage(consoleBufferRead.readLine().trim());

                if (messageToServer.substring(0, 4).equals("HELP") || messageToServer.substring(0, 4).equals("ERRO")) {
                    System.out.println(messageToServer);
                    skipResponse = true;
                } else {
                    out.println(messageToServer);
                }
            } while (!messageToServer.equals("exit"));
            socket.close();

        } catch (UnknownHostException severNotFoundException) {
            System.out.println("ERROR: " + severNotFoundException);

        } catch (IOException IOError) {
            System.out.println("ERROR: " + IOError);
        }
    }

    private String formatMessage(String message) {
        String formattedMessage;

        String command;
        String argument;

        int spaceIndex = message.indexOf(' ');
        if (spaceIndex == -1) {
            command = message;
            argument = "";
        } else {
            command = message.substring(0, spaceIndex);
            argument = message.substring(spaceIndex + 1).replaceAll("\\s+", "");
        }

        switch (command) {
            case "bye":
            case "exit":
            case "quit":
                formattedMessage = "exit";
                break;
            case "help": 
                formattedMessage = "HELP:\n";
                formattedMessage += "bye/exit/quit - leave the app\n";
                formattedMessage += "draw - shows current state of the board\n";
                formattedMessage += "help - shows this instruction\n";
                formattedMessage += "move [arg],[arg]-[arg],[arg] - moves piece between chosen points\n";
                formattedMessage += "skip = skips your move\n";
                formattedMessage += "start [arg] - starts new game for chosen number of people\n";
                break;
            case "start":
                try {
                    Integer.parseInt(argument);
                    formattedMessage = command + " " + argument;
                } catch (NumberFormatException e) {
                    formattedMessage = "ERROR: invalid argument. Try start [arg]";
                }
                break;
            case "draw":
                formattedMessage = command;
                break;
            case "skip":
                formattedMessage = command;
                break;
            case "move":
                try {
                    int[] args = new int[4];
                    args[0] = Integer.parseInt(argument.substring(0, argument.indexOf(',')));
                    args[1] = Integer.parseInt(argument.substring(argument.indexOf(',') + 1, argument.indexOf('-')));
                    args[2] = Integer.parseInt(argument.substring(argument.indexOf('-') + 1, argument.lastIndexOf(',')));
                    args[3] = Integer.parseInt(argument.substring(argument.lastIndexOf(',') + 1));
                    formattedMessage = command + " " + args[0] + "," + args[1] + "-" + args[2] + "," + args[3];
                } catch (Exception e) {
                    formattedMessage = "ERROR: invalid argument. Try move [arg],[arg]-[arg],[arg]";
                }
                break;
            default:
                formattedMessage = "ERROR: Unknown command. Try help to show all available commands.";
                break;
        }

        return formattedMessage;
    }
}