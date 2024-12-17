import java.io.*;
import java.net.*;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedConnection implements AutoCloseable {
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final String encryptionKey = "1234567890123456";  // Clé AES de 16 octets pour AES-128

    public EncryptedConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        try {
            String encryptedMessage = encryptMessage(message);
            //System.out.println("Message avant chiffrement : " + message);
            //System.out.println("Message chiffré : " + encryptedMessage);
            writer.println(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() throws IOException {
        String encryptedMessage = reader.readLine();
        if (encryptedMessage == null || encryptedMessage.isEmpty()) {
            System.out.println("Message reçu est vide ou nul");
            return null;
        }

        //System.out.println("Message reçu chiffré : " + encryptedMessage);
        try {
            String decryptedMessage = decryptMessage(encryptedMessage);
            //System.out.println("Message déchiffré : " + decryptedMessage);
            return decryptedMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String encryptMessage(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private String decryptMessage(String encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(encryptionKey.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }

    @Override
    public void close() throws Exception {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
