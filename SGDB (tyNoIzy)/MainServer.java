import java.util.Properties;

public class MainServer {
    public static void main(String[] args) throws Exception {
        // Charger les configurations depuis le fichier
        Properties config = ConfigLoader.loadConfig();
        
        // Lire le port et la clé de chiffrement depuis le fichier de configuration
        int port = Integer.parseInt(config.getProperty("server_port"));
        String encryptionKey = "1234567890123456";

        // Créer une instance du serveur et démarrer le serveur
        ServerSocketHandler serverSocketHandler = new ServerSocketHandler(port, encryptionKey);
        serverSocketHandler.startServer();
    }
}

