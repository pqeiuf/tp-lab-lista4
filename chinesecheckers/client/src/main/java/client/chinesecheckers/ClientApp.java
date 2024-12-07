package client.chinesecheckers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

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
            playerNickname = args[2];

            // Jeśli nie zostanie podany port dla serwera ustawiamy domyślny na 4444
            if (args.length > 2)
                serverPort = Integer.parseInt(args[3]);

        } catch (Exception argumentsError) {
            System.out.println("Arguments error: " + argumentsError);
            System.exit(1);
        }

        runClient(playerNickname, serverPort);
    }

    /**
     * Funckja obsługująca komunikację z serwerem
     */
    private void runClient() {
        try  {
            Socket socket = new Socket("localhost", serverPort);

            // Utworzenie endpointów komunikacji przez strumienie z serwerem
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("handshake:" + playerNickname);

            // Utworzenie bufora na tekst wpisywany w konsoli
            BufferedReader consoleBufferRead = new BufferedReader(new InputStreamReader(System.in));

            String text;
            do {
                // Odebranie wiadomości z serwera i jej wyświetlenie
                System.out.println(serverPort + "@" + playerNickname + "> " + in.readLine());

                // Wczytanie sygnału który ma być wysłany do serwera
                System.out.println(serverPort + "@" + playerNickname + "< ");
                text = consoleBufferRead.readLine();

                // Wysłanie do wiadomości serwera
                out.println(text);

            } while (!text.equals("bye"));

            socket.close();

        } catch (UnknownHostException severNotFoundException) {
            System.out.println("Server not found error: " + severNotFoundException);

        } catch (IOException IOError) {
            System.out.println("I/O error: " + IOError);
        }
    }
}
