package client.chinesecheckers;

import java.net.*;
import java.io.*;

/**
 * Klasa wątku obsługującego output do serwera
 * Dzięki niej gracz cały czas może wpisywać i wysyłać wiadomości do serwera
 */
public class ServerOutputHandlerThread extends Thread {

    private ClientApp clientApp;

    ServerOutputHandlerThread(ClientApp clientApp) {
        this.clientApp = clientApp;
    }

    @Override
    public void run() {
        try {
            // Inicjalizacja outputu przez strumień do serwera
            PrintWriter out = new PrintWriter(clientApp.socket.getOutputStream(), true);

            // Pierwsza wiadomość 'handshake' podająca nickname
            out.println("Hello " + clientApp.playerNickname);

            String messageToServer;
            do {
                // System.out.print(clientApp.serverPort + ":" + clientApp.playerNickname + " << ");

                // Sformatuj wpisany input gracza
                messageToServer = UserInputInterpreter.interpretUserInput(clientApp.consoleBufferReader.readLine());

                if (messageToServer.equals("exit")) {
                    // Jeśli messageToServer to "exit" to znaczy że gracz chce wyłączyć aplikację
                    clientApp.socket.close();
                    System.exit(0);

                } else if (messageToServer.startsWith("HELP") || messageToServer.startsWith("ERROR")) {
                    System.out.println(messageToServer);

                } else {
                    out.println(messageToServer);
                }

            } while (true);

        } catch (Exception IOError) { 
            System.out.println("ERROR (and program didn't close the socket): " + IOError);
        }
    }
}
