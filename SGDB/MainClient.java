import java.util.Properties;
import java.util.Scanner;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class MainClient {
    public static void main(String[] args) throws Exception {
        // Charger les configurations depuis le fichier
        Properties config = ConfigLoader.loadConfig();
        
        // Lire l'adresse du serveur, le port et la clé de chiffrement depuis le fichier de configuration
        String serverAddress = config.getProperty("server_address");
        int port = Integer.parseInt(config.getProperty("server_port"));
        String encryptionKey = "1234567890123456"; // Clé AES de 16 octets
        
        // Créer une instance du client et se connecter au serveur
        ClientSocketHandler clientSocketHandler = new ClientSocketHandler(serverAddress, port, encryptionKey);
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Enter SQL query (e.g., CREATE DATABASE, CREATE TABLE, SELECT, etc.):");

            while (true) {
                String query = scanner.nextLine();

                // Chiffrement de la requête avant l'envoi
                String encryptedQuery = encryptMessage(query, encryptionKey);

                // Envoyer la requête chiffrée au serveur
                if (query.equalsIgnoreCase("exit")) {
                    clientSocketHandler.sendQuery(encryptedQuery); // Envoyer 'exit' pour fermer proprement la connexion
                    break;
                }

                clientSocketHandler.sendQuery(encryptedQuery);

                // Recevoir la réponse du serveur (chiffrée)
                String encryptedResponse = clientSocketHandler.receiveResponse();

                // Déchiffrement de la réponse avant de l'afficher
                String response = decryptMessage(encryptedResponse, encryptionKey);
                System.out.println("Server response: " + response);

                System.out.println("Enter another SQL query or type 'exit' to quit:");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            clientSocketHandler.close();
            scanner.close();
        }
    }

    // Méthode pour chiffrer un message en utilisant AES
    public static String encryptMessage(String message, String encryptionKey) throws Exception {
        Key key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Conversion en Base64 pour le transport
    }

    // Méthode pour déchiffrer un message en utilisant AES
    public static String decryptMessage(String encryptedMessage, String encryptionKey) throws Exception {
        Key key = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage); // Décodage Base64
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes); // Convertir en chaîne de caractères
    }
}
