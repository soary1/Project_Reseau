import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.X509Certificate;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import java.util.Base64;

public class SecureServer {
    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private static final int RSA_KEY_SIZE = 2048;
    private static final int AES_KEY_SIZE = 128;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public static void main(String[] args) {
        SecureServer server = new SecureServer();
        server.startServer(1234);
    }

    public SecureServer() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
            keyPairGenerator.initialize(RSA_KEY_SIZE);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.publicKey = keyPair.getPublic();
            this.privateKey = keyPair.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Serveur en écoute sur le port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connecté");
                
                // Lecture des données du client
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter outputWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                
                // Envoi de la clé publique au client pour qu'il puisse envoyer la clé AES
                outputWriter.println(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
                
                // Réception de la clé AES cryptée par le client
                String encryptedAESKey = inputReader.readLine();
                SecretKey aesKey = decryptAESKey(encryptedAESKey);
                System.out.println("Clé AES décryptée : " + Base64.getEncoder().encodeToString(aesKey.getEncoded()));
                
                // Déchiffrer le message
                String encryptedMessage = inputReader.readLine();
                String decryptedMessage = decryptMessage(encryptedMessage, aesKey);
                System.out.println("Message décrypté : " + decryptedMessage);
                
                // Réponse au client (chiffrée)
                String response = "Message reçu et décrypté : " + decryptedMessage;
                String encryptedResponse = encryptMessage(response, aesKey);
                outputWriter.println(encryptedResponse);

                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SecretKey decryptAESKey(String encryptedAESKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKey = cipher.doFinal(Base64.getDecoder().decode(encryptedAESKey));
        return new SecretKeySpec(decryptedKey, AES_ALGORITHM);
    }

    private String decryptMessage(String encryptedMessage, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }

    private String encryptMessage(String message, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
}
