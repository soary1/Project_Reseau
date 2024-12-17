import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private Map<String, Database> databases;

    public DatabaseManager() throws Exception {
        databases = new HashMap<>();
        loadDatabases();
    }

    // Charger toutes les bases de données existantes
    private void loadDatabases() throws Exception {
        File dbRoot = new File("databases");
        if (!dbRoot.exists()) {
            dbRoot.mkdir(); // Crée le répertoire principal s'il n'existe pas
        }
        for (File dbFolder : dbRoot.listFiles()) {
            if (dbFolder.isDirectory()) {
                Database db = new Database(dbFolder.getName());
                databases.put(db.getName(), db);
            }
        }
    }

    public void updateRows(String dbName, String tableName, String setClause, String whereClause) throws Exception {
        Table table = findTable(dbName, tableName); // Trouve la table
        if (table == null) {
            throw new IllegalArgumentException("Table '" + tableName + "' not found.");
        }
        table.updateRows(setClause, whereClause); // Délègue à la table
    }

    public void hum() {
        
    }

    // Décrire une table
    public String describeTable(String dbName, String tableName) {
        // Parcours des bases de données
        for (Map.Entry<String, Database> dbEntry : databases.entrySet()) {
            // Obtenir les tables de chaque base de données
            if (dbEntry.getKey().compareTo(dbName) == 0) {
                Database db = dbEntry.getValue();
                Map<String, Table> tables = db.getTables();
                return tables.get(tableName).getStructure(); // Récupère la structure de la table
            }

        }
        throw new IllegalArgumentException("Table '" + tableName + "' not found.");
    }

    // Ajouter une colonne
    public void addColumn(String dbName, String tableName, String columnName, String columnType) throws Exception {
        Table table = findTable(dbName, tableName);
        table.addColumn(new Column(columnName, DataType.valueOf(columnType.toUpperCase())));
    }

    // Supprimer une colonne
    public void dropColumn(String dbName, String tableName, String columnName) throws Exception {
        Table table = findTable(dbName, tableName);
        table.removeColumn(columnName);
    }

    // Modifier une colonne
    public void modifyColumn(String dbName, String tableName, String columnName, String newType) throws Exception {
        Table table = findTable(dbName, tableName);
        table.modifyColumn(columnName, DataType.valueOf(newType.toUpperCase()));
    }

    // Renommer une table
    public void renameTable(String dbName, String oldName, String newName) throws Exception {
        // Vérifier si la base de données existe
        Database db = databases.get(dbName);
        if (db == null) {
            throw new IllegalArgumentException("Database '" + dbName + "' not found.");
        }

        // Vérifier si la table à renommer existe
        Map<String, Table> tables = db.getTables();
        Table table = tables.get(oldName);
        if (table == null) {
            throw new IllegalArgumentException("Table '" + oldName + "' not found in database '" + dbName + "'.");
        }

        // Vérifier si une table avec le nouveau nom existe déjà
        if (tables.containsKey(newName)) {
            throw new IllegalArgumentException(
                    "A table with the name '" + newName + "' already exists in database '" + dbName + "'.");
        }

        // Renommer la table
        tables.remove(oldName); // Supprimer l'ancienne entrée
        table.setName(newName); // Mettre à jour le nom de la table
        tables.put(newName, table); // Ajouter la table avec le nouveau nom
    }

    // Vérifier une table
    public String checkTable(String dbName, String tableName) {
        @SuppressWarnings("unused")
        Table table = findTable(dbName, tableName);
        return "Table '" + tableName + "' is healthy.";
    }

    @SuppressWarnings("unused")

    // Optimiser une table
    public void optimizeTable(String dbName, String tableName) {
        // Pas de logique réelle, simplement pour simuler l'optimisation
        Table table = findTable(dbName, tableName);
        System.out.println("Optimized table '" + tableName + "'.");
    }

    // Analyser une table
    @SuppressWarnings("unused")

    public void analyzeTable(String dbName, String tableName) {
        // Pas de logique réelle, simplement pour simuler l'analyse
        Table table = findTable(dbName, tableName);
        System.out.println("Analyzed table '" + tableName + "'.");
    }

    // Méthodes utilitaires
    @SuppressWarnings("unused")
    private Table getTable(String dbName, String tableName) {
        Map<String, Table> tables = getDatabase(dbName).getTables();
        Table table = tables.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException(
                    "Table '" + tableName + "' does not exist in database '" + dbName + "'.");
        }
        return table;
    }

    private Table findTable(String dbName, String tableName) {
        for (String dbNames : databases.keySet()) { // Parcours des noms des bases de données
            if (dbNames.compareTo(dbName) == 0) {
                Map<String, Table> tables = databases.get(dbName).getTables(); // Obtenir les tables
                return tables.get(tableName); // Retourner la table si trouvée
            }
        }
        throw new IllegalArgumentException("Table '" + tableName + "' not found.");
    }

    // Sauvegarder une base de données
    @SuppressWarnings("unused")
    private void saveDatabase(Database db) {
        db.saveAllTables();
    }

    // Lister les bases de données
    public List<String> listDatabases() {
        File dbFolder = new File("databases");
        if (!dbFolder.exists() || !dbFolder.isDirectory()) {
            return new ArrayList<>(); // Aucune base de données trouvée
        }
        String[] databaseFolders = dbFolder.list((current, name) -> new File(current, name).isDirectory());
        return databaseFolders != null ? List.of(databaseFolders) : new ArrayList<>();
    }

    // Lister les tables dans une base de données
    public List<String> listTables(String dbName) {
        File dbFolder = new File("databases/" + dbName);
        if (!dbFolder.exists() || !dbFolder.isDirectory()) {
            return new ArrayList<>(); // Base de données introuvable
        }
        String[] tableFiles = dbFolder.list((current, name) -> name.endsWith(".table"));
        if (tableFiles != null) {
            List<String> tableNames = new ArrayList<>();
            for (String fileName : tableFiles) {
                tableNames.add(fileName.replace(".table", "")); // Supprimer l'extension
            }
            return tableNames;
        }
        return new ArrayList<>();
    }

    // Créer une nouvelle base de données
    public void createDatabase(String name) throws Exception {
        if (!databases.containsKey(name)) {
            Database db = new Database(name);
            databases.put(name, db);

            // Crée un dossier pour la base de données
            File dbFolder = new File("databases/" + name);
            if (!dbFolder.exists()) {
                dbFolder.mkdir();
            }
        } else {
            System.out.println("Database '" + name + "' already exists.");
        }
    }

    // Supprimer une base de données
    public void deleteDatabase(String name) throws Exception {
        if (databases.containsKey(name)) {
            // Obtenir le dossier correspondant à la base de données
            File dbFolder = new File("databases/" + name);
            if (dbFolder.exists() && dbFolder.isDirectory()) {
                // Supprimer récursivement tous les fichiers et dossiers
                deleteRecursively(dbFolder);
            }
            // Retirer la base de données de la liste en mémoire
            databases.remove(name);
            System.out.println("Database '" + name + "' has been deleted.");
        } else {
            System.out.println("Database '" + name + "' does not exist.");
        }
    }

    // Méthode utilitaire pour supprimer un dossier et son contenu récursivement
    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        file.delete(); // Supprime le fichier ou le dossier vide
    }

    // Obtenir une base de données existante
    public Database getDatabase(String name) {
        return databases.get(name);
    }

    // Créer une table dans une base de données
    public void createTable(String dbName, String tableName, List<Column> columns) throws Exception {
        Database db = getDatabase(dbName);
        if (db == null) {
            System.out.println("Database '" + dbName + "' not found.");
            return;
        }
        Table table = new Table(tableName, getDatabase(dbName));
        for (Column column : columns) {
            table.addColumn(column);
        }
        db.addTable(table);
        System.out.println("Table '" + tableName + "' created successfully.");
    }

    public void deleteTable(String dbName, String tableName) {
        // Obtenir la base de données
        Database db = getDatabase(dbName);
        if (db == null) {
            throw new IllegalArgumentException("Database '" + dbName + "' not found.");
        }
    
        // Vérifier si la table existe
        Table table = db.getTables().get(tableName);
        if (table == null) {
            throw new IllegalArgumentException("Table '" + tableName + "' not found in database '" + dbName + "'.");
        }
    
        // Supprimer la table de la base de données
        db.removeTable(tableName);
    
        // Supprimer le fichier associé à la table
        File tableFile = new File("databases/" + dbName + "/" + tableName + ".table");
        if (tableFile.exists() && !tableFile.delete()) {
            throw new RuntimeException("Failed to delete table file for '" + tableName + "' in database '" + dbName + "'.");
        }
    
        System.out.println("Table '" + tableName + "' deleted successfully from database '" + dbName + "'.");
    }
    

    // Ajouter une ligne dans une table
    public void insertRow(String dbName, String tableName, List<String> columnNames, List<Object> values)
            throws Exception {
        Database db = getDatabase(dbName);
        if (db == null) {
            System.out.println("Database '" + dbName + "' not found.");
            return;
        }
        Table table = db.getTable(tableName);
        if (table == null) {
            System.out.println("Table '" + tableName + "' not found.");
            return;
        }

        // Vérifier si les colonnes sont spécifiées
        if (columnNames == null || columnNames.isEmpty()) {
            // Insertion sans colonnes spécifiées, utiliser toutes les colonnes de la table
            if (values.size() != table.getColumns().size()) {
                System.out.println("Error: Number of values does not match the number of columns.");
                return;
            }
        } else {
            // Insertion avec colonnes spécifiées
            if (columnNames.size() != values.size()) {
                System.out.println("Error: Number of columns does not match the number of values.");
                return;
            }

            // Compléter avec null si nécessaire pour les colonnes non spécifiées
            for (int i = 0; i < table.getColumns().size(); i++) {
                if (!columnNames.contains(table.getColumns().get(i).getName())) {
                    values.add(null); // Ajouter null pour les colonnes non spécifiées
                }
            }
        }

        // Ajouter la ligne à la table
        table.addRow(values);
        System.out.println("Row inserted successfully into table '" + tableName + "' in database '" + dbName + "'.");
    }

    // Lire toutes les données d'une table
    public String selectAll(String dbName, String tableName) {
        Database db = getDatabase(dbName);
        if (db == null) {
            System.out.println("Database '" + dbName + "' not found.");
            return "Database '" + dbName + "' not found.";
        }
        Table table = db.getTable(tableName);
        if (table == null) {
            System.out.println("Table '" + tableName + "' not found.");
            return "Table '" + tableName + "' not found.";
        }
        if (table.getRows().isEmpty()) {
            System.out.println("No rows found in table '" + tableName + "'.");
            return "No rows found in table '" + tableName + "'.";
        } else {
            String result = " ";
            for (Row row : table.getRows()) {
                result = result + row.getValues() + " ";
                // System.out.println(row.getValues());
            }
            return result;
        }
    }

    // Supprimer une ligne d'une table
    public void deleteRow(String dbName, String tableName, int rowIndex) throws Exception {
        Database db = getDatabase(dbName);
        if (db != null) {
            Table table = db.getTable(tableName);
            if (table != null) {
                table.deleteRow(rowIndex);
                db.saveTable(table);
            } else {
                System.out.println("Table '" + tableName + "' not found in database '" + dbName + "'.");
            }
        } else {
            System.out.println("Database '" + dbName + "' not found.");
        }
    }

    public void renameDatabase(String oldName, String newName) throws Exception {
        File oldFolder = new File("databases/" + oldName);
        File newFolder = new File("databases/" + newName);
    
        // Vérifier si l'ancienne base de données existe
        if (!oldFolder.exists()) {
            throw new IllegalArgumentException("Database '" + oldName + "' does not exist.");
        }
    
        // Vérifier si une base de données avec le nouveau nom existe déjà
        if (newFolder.exists()) {
            throw new IllegalArgumentException("A database with the name '" + newName + "' already exists.");
        }
    
        // Renommer le dossier de la base de données
        if (!oldFolder.renameTo(newFolder)) {
            throw new RuntimeException("Failed to rename database folder from '" + oldName + "' to '" + newName + "'.");
        }
    
        // Mettre à jour les métadonnées des tables pour refléter le nouveau nom de la base
        Database db = databases.get(oldName);
        if (db != null) {
            for (Table table : db.getTables().values()) {
                table.setDatabaseName(newName); // Mise à jour des fichiers des tables
            }
    
            // Mettre à jour la structure de gestion interne
            databases.remove(oldName);
            db = new Database(newName); // Recharger la base de données avec le nouveau nom
            databases.put(newName, db);
        }
    
        System.out.println("Database renamed from '" + oldName + "' to '" + newName + "' successfully.");
    }
    
    
}
