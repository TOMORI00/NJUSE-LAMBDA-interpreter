
public class Abstraction extends AST {

    public Identifier param;//变量
    public AST body;//表达式

    //@Author: TOMORI00
    //TODO:EXPLAIN
    // Abstraction contains the param and the body
    // param offers a parameter to tell what variable should be shift at this Abstraction
    // body means body, the target when shifting

    //construction method
    Abstraction(Identifier p, AST b){
        param = p;
        body = b;
    }

    // when printing the tree, an Abstraction is like "\\. body", let the body along to print itself
    // BUT because this is a PJ of a course and it use OJ to assert, I have to print like this:
//    public String toString(){ String output = "\\." + body.toString(); return output;}
    // Actually it should like this:
    public String toString(){ String output = "\\" + param + "." + body.toString(); return output;}
}
