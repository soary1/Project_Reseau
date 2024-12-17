public class QueryExecutor {

    public void executeCreateDatabase(String databaseName) {
        // Logique pour créer une base de données
        System.out.println("Creating database: " + databaseName);
    }

    public void executeCreateTable(String tableName, String columns) {
        // Logique pour créer une table
        System.out.println("Creating table: " + tableName + " with columns: " + columns);
    }

    public void executeInsert(String tableName, String values) {
        // Logique pour insérer des données dans une table
        System.out.println("Inserting values into table: " + tableName + " values: " + values);
    }

    public void executeSelect(String query) {
        // Logique pour sélectionner des données
        System.out.println("Executing select query: " + query);
    }
}
