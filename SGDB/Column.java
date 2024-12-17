import java.io.Serializable;

public class Column implements Serializable {
    private static final long serialVersionUID = 7540130555679878379L;

    private String name;
    private DataType type;

    public Column(String name, DataType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public DataType getDataType() {
        return type;
    }

    public void setDataType(DataType type) {
        this.type = type;
    }
}
