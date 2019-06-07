
public class Identifier extends AST {

    private String name; //名字
    private String value;//De Bruijn index值
    private int DBindex;

    public Identifier(String n,String v){
        name = n;
        value = v;
    }

    public int getDBindex() { return Integer.valueOf(value);}

    public String getName() { return name;}

    public void setName(String name) { this.name = name;}

    public String getValue() { return value;}

    public void setValue(String value) { this.value = value;}

    public void setDBindex( int DBindex) { this.DBindex = DBindex;}

    public String toString(){
        return value;
    }
}