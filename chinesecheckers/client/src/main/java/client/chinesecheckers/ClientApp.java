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
        ClientApp clientApp = new ClientApp(args);
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

            String serverResponse;
            String messageToServer;
            do {
                // Odebranie wiadomości z serwera i jej wyświetlenie
                serverResponse = in.readLine();
                // Jeśli response jest null to znaczy że serwer został zamknięty
                if (serverResponse == null) {
                    break;
                }
                System.out.println(serverPort + ":" + playerNickname + " > " + serverResponse);

                // Wczytanie sygnału który ma być wysłany do serwera
                System.out.print(serverPort + ":" + playerNickname + " < ");
                messageToServer = consoleBufferRead.readLine();

                // Wysłanie do wiadomości serwera
                out.println(messageToServer);

            } while (!messageToServer.equals("bye"));

            socket.close();

        } catch (UnknownHostException severNotFoundException) {
            System.out.println("ERROR: " + severNotFoundException);

        } catch (IOException IOError) {
            System.out.println("ERROR: " + IOError);
        }
    }
}
