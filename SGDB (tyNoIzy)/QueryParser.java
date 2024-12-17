import java.util.ArrayList;
import java.util.List;


public class QueryParser {
    private DatabaseManager dbManager;

    public QueryParser(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public String parse(String sqlQuery) {
        sqlQuery = sqlQuery.trim();
        String[] parts = sqlQuery.split("\\s+");
        String command = parts[0].toUpperCase();

        try {
            switch (command) {
                case "CREATE":
                    return handleCreate(sqlQuery, parts);
                case "INSERT":
                    return handleInsert(sqlQuery, parts);
                case "SELECT":
                    return handleSelect(sqlQuery, parts);
                case "DELETE":
                    return handleDelete(sqlQuery, parts);
                case "SHOW":
                    return handleShow(sqlQuery, parts);
                case "DESCRIBE":
                    return handleDescribe(parts);
                case "ALTER":
                    return handleAlter(sqlQuery, parts);
                case "RENAME":
                    return handleRename(sqlQuery, parts);
                case "UPDATE":
                    return handleUpdate(sqlQuery, parts);
                case "CHECK":
                    return handleCheck(parts);
                case "OPTIMIZE":
                    return handleOptimize(parts);
                case "ANALYZE":
                    return handleAnalyze(parts);
                default:
                    return "Invalid SQL query.";
            }
        } catch (Exception e) {
            return "Error processing query: " + e.getMessage();
        }
    }

    // Nouvelle méthode : Afficher la structure d'une table
    private String handleDescribe(String[] parts) {
        if (parts.length < 2) {
            return "Error: Missing table name for DESCRIBE query.";
        }
        String dbName = parts[1];
        String tableName = parts[2];
        return "Structure of table '" + tableName + "': " + dbManager.describeTable(dbName, tableName);
    }

    // Nouvelle méthode : Modifier une table
    private String handleAlter(String sqlQuery, String[] parts) throws Exception {
        if (parts.length < 3) {
            return "Error: Invalid ALTER query format.";
        }
        String dbName= parts[2];
        String tableName = parts[3];
        if (parts[4].equalsIgnoreCase("ADD")) {
            if (parts.length < 6) {
                return "Error: Missing column information for ADD in ALTER query.";
            }
            String columnName = parts[5];
            String columnType = parts[6];
            dbManager.addColumn(dbName, tableName, columnName, columnType);
            return "Column '" + columnName + "' added to table '" + tableName + "'.";
        } else if (parts[4].equalsIgnoreCase("DROP")) {
            if (parts.length < 6) {
                return "Error: Missing column name for DROP in ALTER query.";
            }
            String columnName = parts[5];
            dbManager.dropColumn(dbName, tableName, columnName);
            return "Column '" + columnName + "' dropped from table '" + tableName + "'.";
        } else if (parts[4].equalsIgnoreCase("MODIFY")) {
            if (parts.length < 6) {
                return "Error: Missing column information for MODIFY in ALTER query.";
            }
            String columnName = parts[5];
            String newType = parts[6];
            dbManager.modifyColumn( dbName,tableName, columnName, newType);
            return "Column '" + columnName + "' modified in table '" + tableName + "'.";
        }
        return "Invalid ALTER query.";
    }

    private String handleRename(String sqlQuery, String[] parts) throws Exception {
        if (parts.length < 4) {
            return "Error: Missing parameters for RENAME query.";
        }
    
        String targetType = parts[1]; // DATABASE ou TABLE
        String oldName = parts[2];
        String newName = parts[3];
    
        if (targetType.equalsIgnoreCase("DATABASE")) {
            dbManager.renameDatabase(oldName, newName);
            return "Database '" + oldName + "' renamed to '" + newName + "' successfully.";
        } else if (targetType.equalsIgnoreCase("TABLE")) {
            if (parts.length < 5) {
                return "Error: Missing database name for renaming table.";
            }
            String dbName = parts[4]; // Le nom de la base contenant la table
            Database db = dbManager.getDatabase(dbName);
            if (db == null) {
                return "Error: Database '" + dbName + "' not found.";
            }
            Table table = db.getTable(oldName);
            if (table == null) {
                return "Error: Table '" + oldName + "' not found in database '" + dbName + "'.";
            }
    
            table.setName(newName);
            db.saveTable(table); // Sauvegarder la table après renommage
            return "Table '" + oldName + "' renamed to '" + newName + "' successfully in database '" + dbName + "'.";
        }
    
        return "Error: Unsupported rename target. Use DATABASE or TABLE.";
    }
    

    // Nouvelle méthode : Mettre à jour des données
    private String handleUpdate(String sqlQuery, String[] parts) throws Exception {
        if (parts.length < 4) {
            return "Error: Missing table name for UPDATE query.";
        }
        String dbName = parts[1];
        String tableName = parts[2];
        if (sqlQuery.indexOf("SET") == -1 || sqlQuery.indexOf("WHERE") == -1) {
            return "Error: Missing SET or WHERE clause in UPDATE query.";
        }
        String setClause = sqlQuery.substring(sqlQuery.indexOf("SET") + 3, sqlQuery.indexOf("WHERE")).trim();
        String whereClause = sqlQuery.substring(sqlQuery.indexOf("WHERE") + 5).trim();
        dbManager.updateRows(dbName, tableName, setClause, whereClause);
        return "Rows in table '" + tableName + "' updated successfully.";
    }

    // Nouvelle méthode : Vérifier une table
    private String handleCheck(String[] parts) {
        if (parts.length < 3) {
            return "Error: Missing table name for CHECK query.";
        }
        String dbName = parts[1];
        String tableName = parts[2];
        return "Table check result: " + dbManager.checkTable(dbName, tableName);
    }

    // Nouvelle méthode : Optimiser une table
    private String handleOptimize(String[] parts) {
        if (parts.length < 3) {
            return "Error: Missing table name for OPTIMIZE query.";
        }
        String dBName = parts[1];
        String tableName = parts[2];
        dbManager.optimizeTable(dBName,tableName);
        return "Table '" + tableName + "' optimized.";
    }

    // Nouvelle méthode : Analyser une table
    private String handleAnalyze(String[] parts) {
        if (parts.length < 3) {
            return "Error: Missing table name for ANALYZE query.";
        }
        String dbName = parts[1];
        String tableName = parts[2];
        dbManager.analyzeTable(dbName,tableName);
        return "Table '" + tableName + "' analyzed.";
    }

    private String handleInsert(String sqlQuery, String[] parts) throws Exception {
        if (parts.length < 4) {
            return "Error: Missing table name or values for INSERT query.";
        }
        String dbName = parts[2];
        String tableName = parts[3];
        String valuesPart = sqlQuery.substring(sqlQuery.indexOf("VALUES") + 6).trim();
        valuesPart = valuesPart.substring(1, valuesPart.length() - 1); // Remove parentheses
    
        List<Object> values = parseValues(valuesPart);
    
        // Vérifier si des colonnes sont spécifiées dans la requête
        List<String> columnNames = null;
        if (sqlQuery.contains("(") && sqlQuery.contains(")")) {
            String columnsPart = sqlQuery.substring(sqlQuery.indexOf("(") + 1, sqlQuery.indexOf(")"));
            columnNames = parseColumnNames(columnsPart);
        }
    
        dbManager.insertRow(dbName, tableName, columnNames, values);
        return "Row inserted successfully into table '" + tableName + "' in database '" + dbName + "'.";
    }
    
    private List<String> parseColumnNames(String columnsPart) {
        List<String> columnNames = new ArrayList<>();
        String[] columns = columnsPart.split(",");
        for (String column : columns) {
            columnNames.add(column.trim());
        }
        return columnNames;
    }
    

    private String handleSelect(String sqlQuery, String[] parts) {
        if (parts.length < 5) {
            return "Error: Missing database or table name for SELECT query.";
        }
        String dbName = parts[3];
        String tableName = parts[4];
        String le = "Contents of table '" + tableName + "' in database '" + dbName + "': "
                + dbManager.selectAll(dbName, tableName) + " Query executed successfully.";
        System.out.println(le);
        return le;
    }

    private String handleDelete(String sqlQuery, String[] parts) throws Exception {
        if (parts[1].equalsIgnoreCase("DATABASE")) {
            if (parts.length < 3) {
                return "Error: Missing database name for DELETE DATABASE query.";
            }
            String dbName = parts[2];
            dbManager.deleteDatabase(dbName); // Appelle la méthode deleteDatabase
            return "Database '" + dbName + "' deleted successfully.";
        } else if (parts[1].equalsIgnoreCase("TABLE")) {
            if (parts.length < 4) {
                return "Error: Missing table name for DELETE TABLE query.";
            }
            String dbName = parts[2];
            String tableName = parts[3];
            dbManager.deleteTable(dbName, tableName); // Appelle la méthode deleteTable
            return "Table '" + tableName + "' deleted successfully from database '" + dbName + "'.";
        } else if (parts[1].equalsIgnoreCase("ROW")) {
            if (parts.length < 6) {
                return "Error: Missing row index for DELETE ROW query.";
            }
            String dbName = parts[2];
            String tableName = parts[3];
            int rowIndex = Integer.parseInt(parts[5]); // Supposons "DELETE ROW dbName tableName ROW 0"
            dbManager.deleteRow(dbName, tableName, rowIndex); // Appelle deleteRow
            return "Row " + rowIndex + " deleted successfully from table '" + tableName + "' in database '" + dbName + "'.";
        }
        return "Error: Unsupported DELETE query.";
    }
    
    

    private String handleCreate(String sqlQuery, String[] parts) throws Exception {
        if (parts[1].equalsIgnoreCase("DATABASE")) {
            if (parts.length < 3) {
                return "Error: Missing database name for CREATE DATABASE query.";
            }
            String dbName = parts[2];
            dbManager.createDatabase(dbName);
            return "Database '" + dbName + "' created successfully.";
        } else if (parts[1].equalsIgnoreCase("TABLE")) {
            if (parts.length < 4) {
                return "Error: Missing table name or columns for CREATE TABLE query.";
            }
            String dbName = parts[2];
            String tableName = parts[3];
            String columnDefinition = sqlQuery.substring(sqlQuery.indexOf('(') + 1, sqlQuery.lastIndexOf(')')).trim();
            List<Column> columns = parseColumns(columnDefinition);

            dbManager.createTable(dbName, tableName, columns);
            return "Table '" + tableName + "' created successfully in database '" + dbName + "'.";
        }
        return "Invalid CREATE query.";
    }

    private String handleShow(String sqlQuery, String[] parts) {
        if (parts.length < 2) {
            return "Error: Missing argument for SHOW query.";
        }
        if (parts[1].equalsIgnoreCase("TABLES")) {
            if (parts.length < 3) {
                return "Missing database name for SHOW TABLES.";
            }
            String dbName = parts[2];
            List<String> tableNames = dbManager.listTables(dbName); // Obtenez la liste des tables
            if (tableNames == null || tableNames.isEmpty()) {
                return "No tables found in database '" + dbName + "'.";
            }
            return "Tables in database '" + dbName + "': " + String.join(", ", tableNames);
        } else if (parts[1].equalsIgnoreCase("DATABASES")) {
            List<String> databaseNames = dbManager.listDatabases(); // Obtenez la liste des bases de données
            if (databaseNames == null || databaseNames.isEmpty()) {
                return "No databases found.";
            }
            return "Databases: " + String.join(", ", databaseNames);
        }
        return "Invalid SHOW query.";
    }

    private List<Column> parseColumns(String columnDefinition) {
        List<Column> columns = new ArrayList<>();
        String[] columnParts = columnDefinition.split(",");
        for (String columnPart : columnParts) {
            String[] columnInfo = columnPart.trim().split("\\s+");
            String columnName = columnInfo[0];
            DataType dataType = DataType.valueOf(columnInfo[1].toUpperCase());
            columns.add(new Column(columnName, dataType));
        }
        return columns;
    }

    private List<Object> parseValues(String valuesPart) {
        List<Object> values = new ArrayList<>();
        String[] valueParts = valuesPart.split(",");
        for (String value : valueParts) {
            value = value.trim();
            if (value.startsWith("'") && value.endsWith("'")) {
                values.add(value.substring(1, value.length() - 1)); // Remove quotes for strings
            } else if (value.matches("-?\\d+(\\.\\d+)?")) {
                values.add(value.contains(".") ? Double.parseDouble(value) : Integer.parseInt(value));
            } else {
                throw new IllegalArgumentException("Invalid value format: " + value);
            }
        }
        return values;
    }
}
