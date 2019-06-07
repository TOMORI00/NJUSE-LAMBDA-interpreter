
public class Abstraction extends AST {

    public Identifier param;//变量
    public AST body;//表达式

    //TODO:EXPLAIN
    //

    Abstraction(Identifier p, AST b){
        param = p;
        body = b;
    }

    // when printing the tree, an Abstraction is like "\\. body", let the body along to print itself
    public String toString(){ String output = "\\." + body.toString(); return output;}
}
