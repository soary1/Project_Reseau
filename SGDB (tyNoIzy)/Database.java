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
    File dbFolder1 = new File("databases/" + name);
    File dbFolder2 = new File("databases2/" + name);

    boolean loadedFromPrimary = tryLoadFromFolder(dbFolder1);
    if (!loadedFromPrimary) {
        System.err.println("Failed to load tables from primary database. Attempting to load from backup...");
        tryLoadFromFolder(dbFolder2);
    }
}

private boolean tryLoadFromFolder(File folder) {
    if (folder.exists() && folder.isDirectory()) {
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".table")) {
                try {
                    Table table = Table.loadFromFile(file, this);
                    if (table != null) {
                        tables.put(table.getName(), table);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load table from file: " + file.getName());
                    return false;
                }
            }
        }
        System.out.println("Tables successfully loaded from " + folder.getAbsolutePath());
        return true;
    }
    return false;
}


    // Sauvegarder toutes les tables
    public void saveAllTables() {
        for (Table table : tables.values()) {
            saveTable(table);
        }
    }

// Sauvegarder une table dans un fichier
public void saveTable(Table table) {
    File dbFolder1 = new File("databases/" + name);
    File dbFolder2 = new File("databases2/" + name);

    if (!dbFolder1.exists()) {
        dbFolder1.mkdirs(); // Créer le dossier si nécessaire
    }
    if (!dbFolder2.exists()) {
        dbFolder2.mkdirs(); // Créer le dossier si nécessaire
    }

    File tableFile1 = new File(dbFolder1, table.getName() + ".table");
    File tableFile2 = new File(dbFolder2, table.getName() + ".table");

    try (ObjectOutputStream out1 = new ObjectOutputStream(new FileOutputStream(tableFile1));
         ObjectOutputStream out2 = new ObjectOutputStream(new FileOutputStream(tableFile2))) {
        out1.writeObject(table);
        out2.writeObject(table);
    } catch (IOException e) {
        e.printStackTrace();
    }
}


public void removeTable(String tableName) {
    if (!tables.containsKey(tableName)) {
        throw new IllegalArgumentException("Table '" + tableName + "' not found in database '" + name + "'.");
    }

    tables.remove(tableName);

    File tableFile1 = new File("databases/" + name + "/" + tableName + ".table");
    File tableFile2 = new File("databases2/" + name + "/" + tableName + ".table");

    if (tableFile1.exists() && !tableFile1.delete()) {
        throw new RuntimeException("Failed to delete file for table '" + tableName + "' in database '" + name + "'.");
    }
    if (tableFile2.exists() && !tableFile2.delete()) {
        throw new RuntimeException("Failed to delete file for table '" + tableName + "' in database2 '" + name + "'.");
    }

    System.out.println("Table '" + tableName + "' removed successfully from database '" + name + "' and database2.");
}

    

}
