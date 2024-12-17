public class Cond {
    private String columnName;
    private Object value;

    public Cond(String columnName, Object value) {
        this.columnName = columnName;
        this.value = value;
    }

    public boolean evaluate(Row row) {
        return row.getValue(columnName).equals(value);
    }
}
