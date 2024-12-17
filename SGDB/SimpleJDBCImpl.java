public class SimpleJDBCImpl implements SimpleJDBC {
    private DatabaseManager databaseManager;
    private Database currentDatabase;

    public SimpleJDBCImpl() throws Exception {
        // Initialiser le gestionnaire de base de données
        this.databaseManager = new DatabaseManager();
    }

    @Override
    public void connect(String dbName) {
        currentDatabase = databaseManager.getDatabase(dbName);
        if (currentDatabase == null) {
            // Si la base de données n'existe pas, créer une nouvelle base
            try {
                databaseManager.createDatabase(dbName);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentDatabase = databaseManager.getDatabase(dbName);
            System.out.println("Database '" + dbName + "' created successfully.");
        } else {
            System.out.println("Connected to database: " + dbName);
        }
    }

    @Override
    public String executeQuery(String query) {
        // Exemple très simple d'analyse de la requête
        String[] queryParts = query.split(" ", 3);
        String command = queryParts[0].toUpperCase();

        switch (command) {
            case "CREATE":
                return handleCreate(queryParts[1], queryParts[2]);
            case "INSERT":
                return handleInsert(queryParts[1]);
            case "SELECT":
                return handleSelect(queryParts[1]);
            default:
                return "Unknown command.";
        }
    }

    @SuppressWarnings("unused")
    private String handleCreate(String tableName, String columns) {
        // Exemple très simplifié de création de table
        // Créez une table avec un seul type de colonne pour simplification
        String[] columnDefinitions = columns.split(",");
        // Table newTable = new Table(tableName, );
        // for (String colDef : columnDefinitions) {
        //     newTable.addColumn(new Column(colDef.trim(), "INT"));  // Pour simplification, tout est de type INT
        // }

        //currentDatabase.addTable(newTable);
        return "Table " + tableName + " created successfully.";
    }

    @SuppressWarnings("unused")
    private String handleInsert(String values) {
        // Exemple très simplifié d'insertion de données
        String[] valueParts = values.split(",");
        //Row newRow = new Row();

        // for (String value : valueParts) {
        //     newRow.addValue(value.trim());  // Ajouter les valeurs en tant que chaînes
        // }

        // Ajouter la ligne à la première table de la base de données
        //currentDatabase.getTables().values().iterator().next().addRow(newRow);
        return "Row inserted successfully.";
    }

    private String handleSelect(String tableName) {
        Table table = currentDatabase.getTable(tableName);
        if (table != null) {
            StringBuilder result = new StringBuilder();
            for (Row row : table.getRows()) {
                result.append(row.getValues()).append("\n");
            }
            return result.toString();
        }
        return "Table " + tableName + " not found.";
    }

    @Override
    public void close() {
        System.out.println("Closing connection.");
        // Aucune action nécessaire ici pour un SGBD en mémoire
    }




    public static void main(String[] args) throws Exception {
        // Créer l'instance JDBC
        SimpleJDBC jdbc = new SimpleJDBCImpl();

        // Connexion à une base de données
        jdbc.connect("MioDB");

        // Créer une table
        String createTableQuery = "CREATE TABLE users (id, name, age)";
        System.out.println(jdbc.executeQuery(createTableQuery));

        // Insérer une ligne
        String insertQuery = "INSERT INTO users VALUES (1, 'John', 30)";
        System.out.println(jdbc.executeQuery(insertQuery));

        // Sélectionner toutes les lignes
        String selectQuery = "SELECT * FROM users";
        System.out.println(jdbc.executeQuery(selectQuery));

        // Fermer la connexion
        jdbc.close();
    }
}
