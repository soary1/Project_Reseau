import java.io.*;
import java.net.*;

public class ClientSocketHandler {
    @SuppressWarnings("unused")
    private final String serverAddress;
    @SuppressWarnings("unused")
    private final int port;
    @SuppressWarnings("unused")
    private final String encryptionKey;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientSocketHandler(String serverAddress, int port, String encryptionKey) throws IOException {
        this.serverAddress = serverAddress;
        this.port = port;
        this.encryptionKey = encryptionKey;
        this.socket = new Socket(serverAddress, port);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    // Méthode pour envoyer une requête au serveur
    public void sendQuery(String query) {
        try {
            // Envoi de la requête au serveur (chiffrement simulé)
            writer.println(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour recevoir la réponse du serveur
    public String receiveResponse() {
        try {
            return reader.readLine();  // Lecture de la réponse du serveur
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fermer la connexion avec le serveur
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
