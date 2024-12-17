import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;  // Ajout de l'import
import java.util.Base64;


public class SecureClient {
    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private static final int AES_KEY_SIZE = 128;

    public static void main(String[] args) {
        SecureClient client = new SecureClient();
        client.connectToServer("localhost", 1234);
    }

    public void connectToServer(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter outputWriter = new PrintWriter(socket.getOutputStream(), true);

            // Lire la clé publique du serveur
            String publicKeyString = inputReader.readLine();
            PublicKey serverPublicKey = loadPublicKey(publicKeyString);

            // Générer une clé AES
            SecretKey aesKey = generateAESKey();
            System.out.println("Clé AES générée : " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));

            // Chiffrer la clé AES avec la clé publique du serveur
            String encryptedAESKey = encryptAESKey(aesKey, serverPublicKey);
            outputWriter.println(encryptedAESKey);

            // Chiffrer le message avec la clé AES
            String message = "Bonjour Serveur!";
            String encryptedMessage = encryptMessage(message, aesKey);
            outputWriter.println(encryptedMessage);

            // Lire la réponse chiffrée du serveur
            String encryptedResponse = inputReader.readLine();
            String decryptedResponse = decryptMessage(encryptedResponse, aesKey);
            System.out.println("Réponse du serveur : " + decryptedResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PublicKey loadPublicKey(String keyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    private SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGen.init(AES_KEY_SIZE);
        return keyGen.generateKey();
    }

    private String encryptAESKey(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    private String encryptMessage(String message, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decryptMessage(String encryptedMessage, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }
}
