import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Row implements Serializable {
    private Map<String, Object> columns; // Les colonnes sont stockées dans une Map où la clé est le nom de la colonne
    private static final long serialVersionUID = 7540130555679878379L;
    public Row(Map<String, Object> columns) {
        this.columns = columns;
    }

    // Récupère la valeur d'une colonne spécifiée par son nom
    public Object getValue(String columnName) {
        if (columns.containsKey(columnName)) {
            return columns.get(columnName);
        }
        throw new IllegalArgumentException("Column '" + columnName + "' not found in row.");
    }

    // Définit la valeur d'une colonne spécifiée par son nom
    public void setValue(String columnName, Object value) {
        if (columns.containsKey(columnName)) {
            columns.put(columnName, value); // Met à jour la valeur de la colonne
        } else {
            throw new IllegalArgumentException("Column '" + columnName + "' not found in row.");
        }
    }

    private List<Object> values;

    public Row(List<Object> values) {
        this.values = values;
    }

    public List<Object> getValues() {
        return values;
    }

    // Récupère la valeur d'une colonne par son index
    public Object getValue(int index) {
        if (index >= 0 && index < values.size()) {
            return values.get(index);
        }
        throw new IndexOutOfBoundsException("Index " + index + " is out of bounds.");
    }

    // Définit la valeur d'une colonne par son index
    public void setValue(int index, Object value) {
        if (index >= 0 && index < values.size()) {
            values.set(index, value);
        } else {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds.");
        }
    }
}
