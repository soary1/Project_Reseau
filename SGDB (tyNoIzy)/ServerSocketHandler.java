import java.io.*;
import java.net.*;

public class ServerSocketHandler{
    private ServerSocket serverSocket;
    private final int port;
    private final DatabaseManager manage;
    @SuppressWarnings("unused")
    private final String encryptionKey;

    public ServerSocketHandler(int port, String encryptionKey) throws Exception {
        this.port = port;
        this.encryptionKey = encryptionKey;
        manage = new DatabaseManager();
    }

    public void startServer() throws Exception {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                // Créer une connexion chiffrée
                EncryptedConnection encryptedConnection = new EncryptedConnection(clientSocket);
                handleClient(encryptedConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        }
    }

    private void handleClient(EncryptedConnection encryptedConnection) throws Exception {
        try (encryptedConnection) { // Auto-fermeture
            QueryParser parser = new QueryParser(manage);

            while (true) {
                // Recevoir la requête chiffrée
                String decryptedRequest = encryptedConnection.receiveMessage();
                System.out.println("Received request: " + decryptedRequest);

                if (decryptedRequest.equalsIgnoreCase("exit")) {
                    System.out.println("Client disconnected.");
                    break;
                }

                // Analyser et exécuter la requête
                String result = parser.parse(decryptedRequest);
                System.out.println("Send Rsult: "+result);
                // Envoi de la réponse chiffrée au client
                encryptedConnection.sendMessage(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
