package client.chinesecheckers;

import java.net.*;
import java.io.*;

/**
 * Klasa wątku obsługującego output do serwera
 * Dzięki niej gracz cały czas może wpisywać i wysyłać wiadomości do serwera
 */
public class ServerOutputHandlerThread extends Thread {

    private ClientApp clientApp;
    private IOThreadsFlag threadsFlag;

    ServerOutputHandlerThread(ClientApp clientApp, IOThreadsFlag threadsFlag) {
        this.clientApp = clientApp;
        this.threadsFlag = threadsFlag;
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
                System.out.print(clientApp.serverPort + ":" + clientApp.playerNickname + " << ");

                // Sformatuj wpisany input gracza
                messageToServer = UserInputInterpreter.interpretUserInput(clientApp.consoleBufferReader.readLine());

                if (messageToServer.equals("exit")) {
                    // Jeśli messageToServer to "exit" to znaczy że gracz chce wyłączyć aplikację
                    threadsFlag.threadsFlag = false;
                    clientApp.socket.close();
                    return;

                } else if (messageToServer.substring(0, 4).equals("HELP") || messageToServer.substring(0, 5).equals("ERROR")) {
                    System.out.println(messageToServer);

                } else {
                    out.println(messageToServer);
                }

            } while (threadsFlag.threadsFlag);

        } catch (Exception IOError) { }

        System.exit(1);
    }
}
