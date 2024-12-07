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

            String clientResponse;
            do {
                // Odebranie wiadomości z serwera i jej wyświetlenie
                clientResponse = in.readLine();
                // Jeśli response jest null to znaczy że serwer został zamknięty
                if (clientResponse == null) {
                    System.out.println("player:" + playerNumber + " DISCONNECTED ");
                    break;
                }
                System.out.println("player:" + playerNumber + " > " + clientResponse);
                
                // Wysylanie do klienta
                out.println("I got your message: '" + clientResponse + "'");

            } while (!clientResponse.equals("bye"));

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

    /**
     * Metoda aktualizująca numer gracza
     * @param newPlayerNumber
     */
    public void changePlayerNumber(int newPlayerNumber) {
        playerNumber = newPlayerNumber;
    }
}
