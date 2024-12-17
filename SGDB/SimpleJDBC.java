public interface SimpleJDBC {
    // Méthode pour se connecter à la base de données
    void connect(String dbName);

    // Méthode pour exécuter une requête (peut être SELECT, INSERT, etc.)
    String executeQuery(String query);

    // Méthode pour fermer la connexion
    void close();
}
