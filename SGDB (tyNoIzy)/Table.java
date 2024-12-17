import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table implements Serializable {
    private String name;
    private List<Column> columns;
    private List<Row> rows;
    private Database database; // Référence à la base de données parent
    private static final long serialVersionUID = 7540130555679878379L;

    // Constructeur pour créer une table avec un nom et une référence à la base de
    // données
    public Table(String name, Database database) {
        this.name = name;
        this.columns = new ArrayList<>();
        this.rows = new ArrayList<>();
        this.database = database; // Assignation de la base de données
    }

    // Getter pour obtenir le nom de la table
    public String getName() {
        return name;
    }

    // Getter pour obtenir la liste des colonnes
    public List<Column> getColumns() {
        return columns;
    }

    // Getter pour obtenir la liste des lignes
    public List<Row> getRows() {
        return rows;
    }

    // Ajouter une colonne à la table et sauvegarder
    public void addColumn(Column column) throws Exception {
        columns.add(column);
        database.saveTable(this); // Sauvegarde la table après l'ajout
        System.out.println("Column '" + column.getName() + "' added successfully to table '" + name + "'.");
    }

    // Ajouter une ligne à la table et sauvegarder les lignes dans un fichier
    public void addRow(List<Object> values) throws Exception {
        if (values.size() != columns.size()) {
            // Compléter avec null si le nombre de valeurs est inférieur au nombre de
            // colonnes
            while (values.size() < columns.size()) {
                values.add(null);
            }
        }
        Row row = new Row(values);
        rows.add(row);
        saveRowsToFile(); // Sauvegarder après l'ajout
    }

    // Sauvegarder uniquement les lignes de la table dans un fichier spécifique
    private void saveRowsToFile() throws Exception {
        File dbFolder = new File("databases/" + database.getName());
        if (!dbFolder.exists()) {
            dbFolder.mkdir(); // Créer le dossier si nécessaire
        }

        File tableFile = new File(dbFolder, name + ".rows");
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tableFile))) {
            out.writeObject(rows); // Sauvegarder seulement les lignes
        } catch (IOException e) {
            throw new Exception("Error saving rows to file: " + e.getMessage());
        }
    }

    // Charger les lignes depuis un fichier
    @SuppressWarnings("unchecked")
    private void loadRowsFromFile() throws Exception {
        File dbFolder = new File("databases/" + database.getName());
        File tableFile = new File(dbFolder, name + ".rows");

        if (tableFile.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(tableFile))) {
                rows = (List<Row>) in.readObject(); // Restaurer les lignes depuis le fichier
            } catch (IOException | ClassNotFoundException e) {
                throw new Exception("Error loading rows from file: " + e.getMessage());
            }
        }
    }

    // Mettre à jour les lignes en fonction de clauses SET et WHERE
    public void updateRows(String setClause, String whereClause) throws Exception {
        // Analyser la clause SET pour obtenir les mises à jour
        Map<String, Object> updates = parseSetClause(setClause);

        // Analyser la clause WHERE pour obtenir la condition de sélection
        Cond condition = parseWhereClause(whereClause);

        // Utiliser un itérateur pour parcourir les lignes et éviter l'usage de for-each
        // sur une collection modifiable
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);

            // Si la condition WHERE est remplie pour la ligne, appliquer les mises à jour
            if (condition.evaluate(row)) {
                // Appliquer chaque mise à jour dans la clause SET à la ligne
                updates.forEach((columnName, newValue) -> row.setValue(columnName, newValue));
            }
        }

        // Sauvegarder les lignes après mise à jour
        saveRowsToFile(); // Sauvegarder après modification
    }

    private Map<String, Object> parseSetClause(String setClause) throws Exception {
        Map<String, Object> updates = new HashMap<>();
        String[] assignments = setClause.split(",");
        for (String assignment : assignments) {
            String[] parts = assignment.trim().split("=");
            String columnName = parts[0].trim();
            Object value = parseValue(parts[1].trim());
            updates.put(columnName, value);
        }
        return updates;
    }

    private Object parseValue(String value) throws Exception {
        if (value.startsWith("'") && value.endsWith("'")) {
            return value.substring(1, value.length() - 1); // Retirer les guillemets pour les chaînes
        } else if (value.matches("-?\\d+(\\.\\d+)?")) {
            return value.contains(".") ? Double.parseDouble(value) : Integer.parseInt(value);
        } else {
            throw new IllegalArgumentException("Invalid value format: " + value);
        }
    }

    private Cond parseWhereClause(String whereClause) {
        String[] parts = whereClause.split("=");
        String columnName = parts[0].trim();
        return new Cond(columnName, parts[1].trim());
    }

    // Supprimer une ligne par son index et sauvegarder les lignes
    public void deleteRow(int rowIndex) throws Exception {
        if (rowIndex >= 0 && rowIndex < rows.size()) {
            rows.remove(rowIndex);
            saveRowsToFile(); // Sauvegarder après suppression
        } else {
            throw new IllegalArgumentException("Invalid row index: " + rowIndex);
        }
    }

    // Charger la table depuis un fichier
    public static Table loadFromFile(File file, Database database) throws Exception {
        Table table;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            table = (Table) in.readObject();
            table.loadRowsFromFile(); // Charger les lignes depuis le fichier
        } catch (IOException | ClassNotFoundException e) {
            throw new Exception("Error loading table from file: " + e.getMessage());
        }
        return table;
    }

    // Constructeur alternatif pour créer une table avec une liste de colonnes
    public Table(String name, List<Column> columns) {
        this.name = name;
        this.columns = columns;
    }

    // Renommer la table et sauvegarder
    public void setName(String newName) throws Exception {
        String oldName = this.name;
        this.name = newName;

        // Renommer le fichier associé à la table
        File dbFolder = new File("databases/" + database.getName());
        File oldFile = new File(dbFolder, oldName + ".table");
        File newFile = new File(dbFolder, newName + ".table");
        if (oldFile.exists()) {
            if (!oldFile.renameTo(newFile)) {
                throw new RuntimeException("Failed to rename table file from '" + oldName + "' to '" + newName + "'.");
            }
        }
        database.saveTable(this); // Sauvegarde la table après le renommage
        System.out.println("Table renamed from '" + oldName + "' to '" + newName + "' successfully.");
    }

    // Supprimer une colonne de la table et sauvegarder
    public void removeColumn(String columnName) throws Exception {
        Column columnToRemove = null;
        for (Column column : columns) {
            if (column.getName().equals(columnName)) {
                columnToRemove = column;
                break;
            }
        }
        if (columnToRemove != null) {
            columns.remove(columnToRemove);
            database.saveTable(this); // Sauvegarde la table après la suppression
            System.out.println("Column '" + columnName + "' removed successfully from table '" + name + "'.");
        } else {
            throw new IllegalArgumentException("Column '" + columnName + "' not found.");
        }
    }

    // Modifier le type d'une colonne et sauvegarder
    public void modifyColumn(String columnName, DataType newType) throws Exception {
        Column columnToModify = null;
        for (Column column : columns) {
            if (column.getName().equals(columnName)) {
                columnToModify = column;
                break;
            }
        }
        if (columnToModify != null) {
            columnToModify.setDataType(newType);
            database.saveTable(this); // Sauvegarde la table après la modification
            System.out.println("Column '" + columnName + "' modified successfully in table '" + name + "'.");
        } else {
            throw new IllegalArgumentException("Column '" + columnName + "' not found.");
        }
    }

    // Retourner la structure de la table sous forme de chaîne
    public String getStructure() {
        StringBuilder structure = new StringBuilder("Table: " + name + " :: ");

        for (Column column : columns) {
            structure.append(column.getName()).append(" : ").append(column.getDataType()).append(" , ");
        }

        return structure.toString();
    }

    public void setDatabaseName(String newDatabaseName) throws Exception {
        String oldDatabaseName = database.getName();
        this.database = new Database(newDatabaseName); // Mise à jour de la référence à la nouvelle base de données

        // Renommer les fichiers associés à la table
        File oldFolder = new File("databases/" + oldDatabaseName);
        File newFolder = new File("databases/" + newDatabaseName);

        if (!newFolder.exists()) {
            newFolder.mkdir(); // Créer le nouveau dossier si nécessaire
        }

        // Renommer le fichier principal de la table
        File oldTableFile = new File(oldFolder, name + ".table");
        File newTableFile = new File(newFolder, name + ".table");
        if (oldTableFile.exists()) {
            if (!oldTableFile.renameTo(newTableFile)) {
                throw new RuntimeException(
                        "Failed to move table file from '" + oldFolder + "' to '" + newFolder + "'.");
            }
        }

        // Renommer le fichier des lignes associées à la table
        File oldRowsFile = new File(oldFolder, name + ".rows");
        File newRowsFile = new File(newFolder, name + ".rows");
        if (oldRowsFile.exists()) {
            if (!oldRowsFile.renameTo(newRowsFile)) {
                throw new RuntimeException("Failed to move rows file from '" + oldFolder + "' to '" + newFolder + "'.");
            }
        }

        System.out
                .println("Table '" + name + "' updated to belong to database '" + newDatabaseName + "' successfully.");
    }

}
