
public class Interpreter {

    public Parser parser;
    public AST astAfterParser;

    //@Author: TOMORI00
    //TODO: the interpreter still have rules, it matches APPLICATIONs, ABSTRACTIONs and IDETIFIERs
    // RULES:
    // if meets an Application, first check its Lhs, if it`s able to eval, eval it first
    // if meets an Abstraction, eval it`s body first
    // if meets a Identifier, it just a Identifier waiting for substitution

    public Interpreter(Parser p){
        parser = p;
//        System.out.println("    ----PARSER START----");
        astAfterParser = p.parse();
//        System.out.println("after parse: " + astAfterParser);
//        System.out.println("    ----PARSER DONE----");
    }

    private  boolean isAbstraction(AST ast){
        return ast instanceof Abstraction;
    }
    private  boolean isApplication(AST ast){
        return ast instanceof Application;
    }
    private  boolean isIdentifier(AST ast){
        return ast instanceof Identifier;
    }

    public AST eval(){ return evalAST(astAfterParser); }


    //TODO:EXPLAIN
    // check ast
    // if Application, eval(check) it according to it`s Lhs and Rhs
    //      if Lhs==Abstraction, di substitution
    //      if Lhs==Application, do further evaluation according to it`s Rhs(whether Rhs is able to eval or even substitution)
    // else Lhs is over, go to eval Rhs
    // if Abstraction, eval(check) it body
    // else it at recurse returning time, just return itself.
    private AST evalAST(AST ast) {
//        System.out.println("            --evalAST:COMING IN");
//        System.out.println("            ==evalAST:RECEIVE: ast: "+ ast);
        while(true){
            // check ast
            // if Application, eval(check) it according to it`s Lhs and Rhs
            //      if Lhs==Abstraction, di substitution
            //      if Lhs==Application, do further evaluation according to it`s Rhs(whether Rhs is able to eval or even substitution)
            //  else Lhs is over, go to Rhs
            // if Abstraction, eval(check) it body
            // else it at recurse returning time, just return itself.
            if(ast instanceof Application){
//                System.out.println("            evalAST: ast match Application" + "Lhs: " + ((Application)ast).getLhs() + " Rhs: " + ((Application)ast).getRhs());
                if(isAbstraction(((Application) ast).getLhs())){
//                    System.out.println("            evalAST: ast Lhs: " + ((Application)ast).getLhs() + " matches Abstraction, substitute its body: " + ((Abstraction)((Application) ast).getLhs()).body + "with Rhs: " + ((Application) ast).getRhs());
                    //It`s the case that can do substitute: Lhs is Abstraction
                    ast = substitute(((Abstraction)((Application) ast).getLhs()).body,((Application) ast).getRhs());
                }
                else if(isApplication(((Application) ast).getLhs())&&!isIdentifier(((Application) ast).getRhs())){
//                    System.out.println("            evalAST: ast Lhs: " + ((Application) ast).getLhs() + " matches Application and ast Rhs: " + ((Application) ast).getRhs() + " is not a Identifier");
                    ((Application) ast).setLhs(evalAST(((Application) ast).getLhs()));
                    ((Application) ast).setRhs(evalAST(((Application) ast).getRhs()));//Rhs still needs eval
                    if(isAbstraction(((Application) ast).getLhs())) {
                        // Lhs matches Abstraction, go to corresponding method by evalAST() again
                        ast = evalAST(ast);
                    }
                    return ast;
                }
                else if(isApplication(((Application) ast).getLhs())&&isIdentifier(((Application) ast).getRhs())){
//                    System.out.println("            evalAST: ast Lhs: " + ((Application) ast).getLhs() + " matches Application and ast Rhs: " + ((Application) ast).getRhs() + " is a Identifier");
                    ((Application) ast).setLhs(evalAST(((Application) ast).getLhs()));
                    if(isAbstraction(((Application) ast).getLhs())) {
                        // Lhs matches Abstraction, go to corresponding method by evalAST() again
                        ast = evalAST(ast);
                    }
                    return ast;
                }
                else{
                    ((Application) ast).setRhs(evalAST(((Application) ast).getRhs()));// eval Rhs
                    return ast;
                }
            }
            else if(isAbstraction(ast)){
//                System.out.println("            evalAST: ast:" + ast + " matches Abstraction, eval its body first");
                ((Abstraction) ast).body = evalAST(((Abstraction) ast).body);
                return ast;
            }
            else{
                return ast;
            }
        }
    }

//    private AST myevalAST(AST ast){
//        System.out.println("            --evalAST:COMING IN");
//        System.out.println("            ==evalAST:RECEIVE: ast: "+ ast);
//        while(true) {
//            if(isApplication(ast)) {
//                System.out.println("            evalAST: ast match Application" + "Lhs: " + ((Application)ast).getLhs() + " Rhs: " + ((Application)ast).getRhs());
//                if(isAbstraction(((Application)ast).getLhs()) && isAbstraction(((Application)ast).getRhs())) {
//                    System.out.println("            evalAST: Application: Lhs & RHs match Abstraction, substitute Lhs: " + ((Application)ast).getLhs() + "with Rhs: " + ((Application)ast).getRhs());
//                    ast = substitute(((Application) ast).getLhs(), ((Application) ast).getRhs());
//                    System.out.println("            evalAST: substitute over, ast: " + ast);
//                    return ast;
//                }
//                else if(isAbstraction(((Application) ast).getLhs())) {
//                    System.out.println("            evalAST: Application: Lhs match Abstraction, setting Rhs: " + ((Application)ast).getLhs() + " with Rhs: " + ((Application)ast).getRhs());
//                    ((Application) ast).setRhs(evalAST(((Application) ast).getRhs()));
//                    System.out.println("            evalAST: substitute over, ast: " + ast);
//                    return ast;
//                }
//                else {
//                    System.out.println("            evalAST: Application: no matches, setting Lhs with this.Lhs");
//                    ((Application) ast).setLhs(evalAST(((Application) ast).getLhs()));
//                    System.out.println("            evalAST: substitute over, ast: " + ast);
//                    return ast;
//                }
//            }
//            else {
//                System.out.println("            evalAST: ast not match Application, return itself:" + ast);
//                return ast;
//            }
//        }
//    }

    private AST substitute(AST node,AST value){ return shift(-1,subst(node,shift(1,value,0),0),0); }

    /**
     *  value替换node节点中的变量：
     *  如果节点是Applation，分别对左右树替换；
     *  如果node节点是abstraction，替入node.body时深度得+1；
     *  如果node是identifier，则替换De Bruijn index值等于depth的identifier（替换之后value的值加深depth）

     *@param value 替换成为的value
     *@param node 被替换的整个节点
     *@param depth 外围的深度

     *@return AST
     *@exception  (方法有异常的话加)
     */

    private AST subst(AST node, AST value, int depth){
//        System.out.println("            --subst:COMING IN");
//        System.out.println("            ==subst: RECEIVE: node: "+ node + " value: " + value + "depth: " + depth);
        if(isApplication(node)) {
//            System.out.println("        subst: node match Application, subst Lhs: " + ((Application) node).getLhs() + " and Rhs: " + ((Application) node).getRhs());
            return new Application( subst(((Application) node).getLhs(),value,depth),subst(((Application) node).getRhs(),value,depth));
        }
        else if(isAbstraction(node)) {
//            System.out.println("        subst: node match Abstraction, moving body: " + ((Abstraction) node).body + " and go deeper by depth: " + depth + "++");
            return new Abstraction( ((Abstraction) node).param,subst(((Abstraction) node).body,value,depth+1));
        }
        else {
//            System.out.println("        subst: node match Identifier, Dindex = depth.identifier?");
            if(depth == ((Identifier)node).getDBindex()) {
//                System.out.println("        depth: " + depth + " == " + "Dindex: " + ((Identifier)node).getDBindex() + "shift(depth: " + depth + "value: " + value);
                return shift(depth,value,0); }
            else
//                System.out.println("        depth: " + depth + " != " + "Dindex: " + ((Identifier)node).getDBindex() + "return node: " + node);
                return node;
        }
    }

    /**
     *  De Bruijn index值位移
     *  如果节点是Applation，分别对左右树位移；
     *  如果node节点是abstraction，新的body等于旧node.body位移by（from得+1）；
     *  如果node是identifier，则新的identifier的De Bruijn index值如果大于等于from则加by，否则加0（超出内层的范围的外层变量才要shift by位）.
        *@param by 位移的距离
     *@param node 位移的节点
     *@param from 内层的深度
     
     *@return AST
     *@exception  (方法有异常的话加)
     */

    private AST shift(int by, AST node,int from){
//        System.out.println("            --shift: COMING IN");
//        System.out.println("            ==shift: RECEIVE: by:" + by + " node: " + node + "from: " + from);
        if(isApplication(node)) {
//            System.out.println("            shift: node match Application, shift Lhs: " + ((Application)(node)).getLhs() + " and Rhs: " + ((Application)(node)).getRhs());
            return new Application( shift(by,((Application)(node)).getLhs(),from), shift(by,((Application)(node)).getRhs(),from));
        }
        else if(isAbstraction(node)) {
//            System.out.println("        shift: node match Abstraction, shift(by: " + by + ", node.body:" + ((Abstraction)node).body + ", from: " + from + "+1)");
            return new Abstraction( ((Abstraction)node).param, shift(by,((Abstraction)node).body,from+1));
        }
        else {
//            System.out.print("        shift: node match Identifier, and ");
            if(((Identifier)node).getDBindex() >= from) {
//                System.out.println("  shift: match (identifier.Dindex >= from), Dindex: "+ ((Identifier)node).getDBindex() + " + by: " + by);
                return new Identifier( ((Identifier)node).getName(), String.valueOf(((Identifier)node).getDBindex() + by));
            }
            else {
//                System.out.println("  shift: match (identifier.Dindex >= from), Dindex: "+ ((Identifier)node).getDBindex() + " + 0");
                return new Identifier( ((Identifier)node).getName(), String.valueOf(((Identifier)node).getDBindex()));
            }
        }
    }

    static String ZERO = "(\\f.\\x.x)";
    static String SUCC = "(\\n.\\f.\\x.f (n f x))";
    static String ONE = app(SUCC, ZERO);
    static String TWO = app(SUCC, ONE);
    static String THREE = app(SUCC, TWO);
    static String FOUR = app(SUCC, THREE);
    static String FIVE = app(SUCC, FOUR);
    static String PLUS = "(\\m.\\n.((m "+SUCC+") n))";
    static String POW = "(\\b.\\e.e b)";       // POW not ready
    static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
    static String SUB = "(\\m.\\n.n"+PRED+"m)";
    static String TRUE = "(\\x.\\y.x)";
    static String FALSE = "(\\x.\\y.y)";
    static String AND = "(\\p.\\q.p q p)";
    static String OR = "(\\p.\\q.p p q)";
    static String NOT = "(\\p.\\a.\\b.p b a)";
    static String IF = "(\\p.\\a.\\b.p a b)";
    static String ISZERO = "(\\n.n(\\x."+FALSE+")"+TRUE+")";
    static String LEQ = "(\\m.\\n."+ISZERO+"("+SUB+"m n))";
    static String EQ = "(\\m.\\n."+AND+"("+LEQ+"m n)("+LEQ+"n m))";
    static String MAX = "(\\m.\\n."+IF+"("+LEQ+" m n)n m)";
    static String MIN = "(\\m.\\n."+IF+"("+LEQ+" m n)m n)";

    private static String app(String func, String x){
        return "(" + func + x + ")";
    }
    private static String app(String func, String x, String y){
        return "(" +  "(" + func + x +")"+ y + ")";
    }
    private static String app(String func, String cond, String x, String y){
        return "(" + func + cond + x + y + ")";
    }

    public static void main(String[] args) {
        String[] sources = {
                ZERO,//0
                ONE,//1
                TWO,//2
                THREE,//3
                app(PLUS, ZERO, ONE),//4
                app(PLUS, TWO, THREE),//5
                app(POW, TWO, TWO),//6
                app(PRED, ONE),//7
                app(PRED, TWO),//8
                app(SUB, FOUR, TWO),//9
                app(AND, TRUE, TRUE),//10
                app(AND, TRUE, FALSE),//11
                app(AND, FALSE, FALSE),//12
                app(OR, TRUE, TRUE),//13
                app(OR, TRUE, FALSE),//14
                app(OR, FALSE, FALSE),//15
                app(NOT, TRUE),//16
                app(NOT, FALSE),//17
                app(IF, TRUE, TRUE, FALSE),//18
                app(IF, FALSE, TRUE, FALSE),//19
                app(IF, app(OR, TRUE, FALSE), ONE, ZERO),//20
                app(IF, app(AND, TRUE, FALSE), FOUR, THREE),//21
                app(ISZERO, ZERO),//22
                app(ISZERO, ONE),//23
                app(LEQ, THREE, TWO),//24
                app(LEQ, TWO, THREE),//25
                app(EQ, TWO, FOUR),//26
                app(EQ, FIVE, FIVE),//27
                app(MAX, ONE, TWO),//28
                app(MAX, FOUR, TWO),//29
                app(MIN, ONE, TWO),//30
                app(MIN, FOUR, TWO),//31
        };

//        for(int i=0 ; i<sources.length; i++) {

        for(int i = 0; i < 10; i++) {
            String source = sources[i];

            System.out.println(i+":"+source);

            Lexer lexer = new Lexer(source);

            Parser parser = new Parser(lexer);

            Interpreter interpreter = new Interpreter(parser);

            AST result = interpreter.eval();

            System.out.println(i+":" + result.toString());
        }
    }
}
