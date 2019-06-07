
import java.util.ArrayList;

public class Parser {

    //@Author : Damon
    //TODO:READ
    // GRAMMAR:
    // term == LAMBDA LCID DOT term
    // term == application
    // application == application atom
    // application == atom
    // atom == LCID
    // atom == LPAREN term RPAREN
    //
    //TODO:EXPLAIN
    //

    public Lexer lexer;

    //TODO:OK
    // construction method
    public Parser(Lexer l){
        lexer = l;
    }

    //TODO:OK
    // starting by new-ing a ArrayList( ctx ), recursive to traversal all through the string, handle the string by using Lexer`s API.
    // Parser start here by regarding the input as a term
    public AST parse(){
        AST ast = term(new ArrayList<>());
        return ast;
    }

    //TODO:OK/     !(NEEDS UNDERSTANDING)!
    // according to the grammar, the term should match "λ x 。 term" or "application"
    //
    //TODO:EXPLAIN
    // ctx : means context, using for building the tree (NEEDS UNDERSTANDING)
    // param : member of Abstraction, making up Identifier, matching it`s "name"
    // paramIndex : member of Abstraction, making up Identifier, matching it`s "De Bruijn index"
    // body : maybe another part is a term, which is in the ctx, start another term() to analysis it.
    private AST term(ArrayList<String> ctx){
//        System.out.println("        --Term: COMING IN");
        if(lexer.match(TokenType.LAMBDA)) {
//            System.out.println("        term: matched LAMBDA");
            String param = lexer.tokenValue;
//            System.out.println("        term: tokenValue is: " + param);
            lexer.skip(TokenType.LCID);
//            System.out.println("        term: skipped LCID");
            lexer.match(TokenType.DOT);
//            System.out.println("        term: matched DOT");
            ctx.add(0,param);
            String paramIndex = String.valueOf(ctx.indexOf(param));
//            System.out.println("      term: ctx added " + param + " , it`s index is "+ paramIndex + " , now ctx is " + ctx);
//            System.out.println("        term: now analysing AST body = term(" + ctx + ")");
            AST body = term(ctx);//START recursive
            ctx.remove(ctx.indexOf(param));//When recursive of deeper parts is over, remove the context form ctx
//            System.out.println("        term: ctx removed param: " + param);
//            System.out.println("        term: now build an Abstraction with param: " + param + " index:" + paramIndex + " body:" + body);
            return new Abstraction(new Identifier(param,paramIndex),body);
        }
        else {
//            System.out.println("        term: not matching LAMBDA, build application with ctx:" + ctx);
            return application(ctx);}//if it`s not start with λ, use application() to analysis it.
    }

    //TODO:OK/     !(NEEDS UNDERSTANDING)!
    // according to the grammar, the application should match "Application Atom" or "Atom"
    //
    //TODO:EXPLAIN
    // Application is the node of the tree, it will be divided to lhs and rhs, using left recursion
    // if rhs is an Application, it goes deeper and divide the Application, until the rhs no longer an application.
    // what added on the tree is atom, because finally the application will be analysis to a single atom
    //
    //TODO:PS
    // as for the implement, to solve the left recursion might cause unstoppable recursion, focus on the last atom.
    // if it`s time to stop left recursion, return NULL to it`s higher Application, when Application building, it looks at the rhs, if it`s NULL, it will force the lhs to (new Application(this.lhs, this.rhs)) and force the rhs to that atom()
    // else recurse by setting lhs as new Application and setting rhs as atom(ctx)
    private AST application(ArrayList<String> ctx){
//        System.out.println("        --Application: COMING IN");
        Application application = new Application(null, null);
//        System.out.println("        application: setting Lhs by atom with ctx: " + ctx);
        application.setLhs(atom(ctx));
//        System.out.println("        application: setting Rhs by atom with ctx: " + ctx);
        application.setRhs(atom(ctx));
        while (true) {
            if(application.getRhs() == null) {
//                System.out.println("        application: getting Lhs");
                return application.getLhs();
            }
            else {
//                System.out.println("        application: this Lhs = " + application.getLhs() + " Rhs = " + application.getRhs());
//                System.out.println("        application: setting Lhs by application( " + application.getLhs() + application.getRhs() + " )");
                application.setLhs(new Application(application.getLhs(), application.getRhs()));
//                System.out.println("        application: setting Rhs by atom( " + ctx + " )");
                application.setRhs(atom(ctx));
            }
        }
    }

    //TODO: OK/     !(NEEDS UNDERSTANDING)!
    // according to the grammar, the atom should match "LAPAREN term RAPAREN" or "LCID"
    //
    //TODO: EXPLAIN
    // atom is units of the tree, pack a term by ( ), or is a constant value
    // when reach '(' , go deeper by using term()
    // when reach 'LCID' , reach out it`s content and put it into Identifier with it`s De Bruijn index
    // when reach ')' , stop going deeper by returning NULL
    // when reach 'null' , which means it`s deeper branch reaches a constant value or a form that is unable to simplify, skip it by using match(), return that by returning atom(ctx), the value is stored in ctx BTW
    //
    private AST atom(ArrayList<String> ctx){
//        System.out.println("        --Atom: COMING IN");
        if (lexer.match(TokenType.LPAREN)) {
//            System.out.println("        atom: matched LPAREN, building term with ctx: " + ctx);
            return term(ctx);
        }
        else if(lexer.next(TokenType.LCID)){
            String valueInIt = lexer.tokenValue;
//            System.out.println("        atom: next is LCID, store it`s value: " + valueInIt);
            lexer.skip(TokenType.LCID);
//            System.out.println("        atom: skipped LCID");
//            System.out.println("        atom: building Identifier with value: " + valueInIt + " index: " + ctx.indexOf(valueInIt));
            return new Identifier(valueInIt, String.valueOf(ctx.indexOf(valueInIt)));
        }
        else if(lexer.match(TokenType.RPAREN)) {
//            System.out.println("        atom: matched RPAREN, returning");
            return null;
        }
        else if(lexer.match(null)) {
//            System.out.println("        atom: matched NULL, stopping and returning");
            lexer.match(null);
//            System.out.println("        atom: solving unreached atoms by atom( " + ctx + " )");
            return atom(ctx);
        }
        else return null;
    }

    //TODO:
    // test for Parser
    public static void main(String[] args) {
        String s = "(\\x.\\y.(x y)x)(\\x.x)(\\y.y)";

        Lexer lexer = new Lexer(s);
        Parser parser = new Parser(lexer);
        AST afterParse = parser.parse();
        System.out.println("--------DONE--------");
        System.out.println(afterParse.toString());
    }
}
