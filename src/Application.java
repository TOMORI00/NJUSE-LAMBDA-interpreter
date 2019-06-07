
public class Application extends AST{

    private AST lhs;//左树
    private AST rhs;//右树

    //@Author: TOMORI00
    //TODO:EXPLAIN
    // Application contains Lhs and Rhs, it mainly just a node dividing branches when building the trees
    // so it offers:
    // getLhs(), getRhs(), setLhs(lhs), setRhs(rhs)

    //construction method
    Application(AST l, AST s){ lhs = l;rhs = s; }

    public AST getLhs(){return lhs;}
    public AST getRhs(){return rhs;}
    public void setLhs(AST lhs) {this.lhs = lhs;}
    public void setRhs(AST rhs) {this.rhs = rhs;}

    // when printing the tree, an Application is like "(lhs rhs)", let lhs and rhs along to print themselves
    public String toString(){ String output = "(" + lhs.toString() + " " + rhs.toString() + ")"; return output;}
}
