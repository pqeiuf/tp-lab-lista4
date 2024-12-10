package client.chinesecheckers;

import java.net.*;
import java.io.*;

/**
 * Klasa wątku obsługującego input z serwera
 * Dzięki niej gracz cały czas otrzymuje wiadomości od serwera
 */
public class ServerInputHandlerThread extends Thread {

    private ClientApp clientApp;
    private IOThreadsFlag threadsFlag;

    ServerInputHandlerThread(ClientApp clientApp, IOThreadsFlag threadsFlag) {
        this.clientApp = clientApp;
        this.threadsFlag = threadsFlag;
    }

    @Override
    public void run() {
        try {
            // Inicjalizacja inputu przez strumień z serwera
            BufferedReader in = new BufferedReader(new InputStreamReader(clientApp.socket.getInputStream()));

            String serverResponse;
            do {
                serverResponse = in.readLine();

                // Jeśli response jest null to znaczy że serwer został zamknięty wówczas zamykamy aplikację
                if (serverResponse == null) {
                    System.out.println("\n" + clientApp.serverPort + ":" + clientApp.playerNickname + " >> Server has been shut down");
                    threadsFlag.threadsFlag = false;
                    clientApp.socket.close();
                    break;
                }
                
                System.out.println("\n" + clientApp.serverPort + ":" + clientApp.playerNickname + " >> " + serverResponse);
                // Ponowne wydrukowanie wstępu linijki inputu
                System.out.print(clientApp.serverPort + ":" + clientApp.playerNickname + " << ");

                // Wyczyść bufor wejścia by nie kontynuować wpisanej komendy
                while (clientApp.consoleBufferReader.ready()) {
                    clientApp.consoleBufferReader.readLine(); // Odczytaj i odrzuć każdą linię
                }

            } while (threadsFlag.threadsFlag);

        } catch (Exception IOError) { }

        System.exit(1);
    }
}
