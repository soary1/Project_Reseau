import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Database implements Serializable {
    private String name;
    private Map<String, Table> tables;
    private static final long serialVersionUID = 7540130555679878379L;


    public Database(String name) throws Exception {
        this.name = name;
        this.tables = new HashMap<>();
        loadTables();
    }

    public String getName() {
        return name;
    }    

    public Map<String, Table> getTables() {
        return tables;
    }

    public void addTable(Table table) {
        tables.put(table.getName(), table);
        saveTable(table);
    }

    public Table getTable(String tableName) {
        return tables.get(tableName);
    }

    // Charger les tables à partir des fichiers
    private void loadTables() throws Exception {
        File dbFolder = new File("databases/" + name);
        if (dbFolder.exists() && dbFolder.isDirectory()) {
            for (File file : dbFolder.listFiles()) {
                if (file.getName().endsWith(".table")) {
                    Table table = Table.loadFromFile(file, this);
                    if (table != null) {
                        tables.put(table.getName(), table);
                    }
                }
            }
        }
    }

    // Sauvegarder toutes les tables
    public void saveAllTables() {
        for (Table table : tables.values()) {
            saveTable(table);
        }
    }

    // Sauvegarder une table dans un fichier
    public void saveTable(Table table) {
        File dbFolder = new File("databases/" + name);
        if (!dbFolder.exists()) {
            dbFolder.mkdir(); // Créer le dossier si nécessaire
        }

        File tableFile = new File(dbFolder, table.getName() + ".table");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tableFile))) {
            out.writeObject(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeTable(String tableName) {
        // Vérifier si la table existe
        if (!tables.containsKey(tableName)) {
            throw new IllegalArgumentException("Table '" + tableName + "' not found in database '" + name + "'.");
        }
    
        // Supprimer la table de la liste des tables
        tables.remove(tableName);
    
        // Supprimer le fichier associé à la table
        File tableFile = new File("databases/" + name + "/" + tableName + ".table");
        if (tableFile.exists() && !tableFile.delete()) {
            throw new RuntimeException("Failed to delete file for table '" + tableName + "' in database '" + name + "'.");
        }
    
        System.out.println("Table '" + tableName + "' removed successfully from database '" + name + "'.");
    }
    

}
