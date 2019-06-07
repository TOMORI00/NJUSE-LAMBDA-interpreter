public class Identifier extends AST {

    private String name; //名字
    private String value;//De Bruijn index值


    //@Author: TOMORI00
    //TODO:EXPLAIN
    // Identifier contains name and value, it can trace back to the LCID,
    // now it holds the value of that certain name by using the Dindex System,
    // the Dindex System use Dindex to indicate the shift sequence of variables

    //construction method
    public Identifier(String n,String v){
        name = n;
        value = v;
    }

    //TODO:PS.:
    // the class can be better backed if here offer API for setting the values, but it`s not that necessary so I deleted them.
    public int getDBindex() { return Integer.valueOf(value);}
    public String getName() { return name;}

    // when printing the tree, the identifier is always the last node so it just needs to print itself,
    // BUT because this is a PJ of a course and it use OJ to assert, I have to print like this:
    public String toString(){
        return value;
    }
    // Actually it should like this:
//    public String toString(){ return name;}
}
