


public class Lexer{


	//@Author: TOMORI00
	//TODO:GRAMMAR LIST:
	// '(' == LPAREN
	// ')' == RPAREN
	// '\\' == LAMBDA
	// '.' == DOT
	// 'EOF' == EOF
	// '[a-z][A-Z]*' == LCID      !(NOT IMPLEMENTED, just use[a-z], NEEDS FIX)!
	//
	//TODO:EXPLAIN
	// Lexer is class that interpret the origin string to Tokens, and offer some handle methods to parser, as it needs these to build AST tree.
	// Lexer`s APIs is listed here:
	// next(Token), if the Token stored matches the Token input, return true, else return false
	// match(Token), if next Token matches the Token input, catch next Token and return true, else return false
	// skip(Token), just catch next Token, regardless of matches or not
	//
    private String source;
    private int index;
    private TokenType token;
    protected String tokenValue;
    private char theChar;

	// construction method
    public Lexer(String s){
        index = 0;
        source = s;
        nextToken();
    }

    //TODO:OK
    // get next token
    private TokenType nextToken() {

		if (index == source.length()) {
			token = TokenType.EOF;
		}
		switch (nextChar()) {
			case '\\':
				token = TokenType.LAMBDA;
				break;
			case '(':
				token = TokenType.LPAREN;
				break;
			case ' ':
				token = null;
				break;
			case ')':
				token = TokenType.RPAREN;
				break;
			case '.':
				token = TokenType.DOT;
				break;
			case '#':
				token = TokenType.EOF;
				break;
			default:
				tokenValue = String.valueOf(theChar);
				token = TokenType.LCID;
		}
		index++;
		if(token != null) {
			System.out.println(token);
		}
		return token;
    }

    //TODO:OK
    // get next char
    private char nextChar(){
    	if(source.length()==index) { return '#';}
        else { theChar = source.charAt(index); return theChar;}
    }

    //TODO:OK
    // check token == t ?
    public boolean next(TokenType t){
//    	System.out.println("	Lexer: next( " + t + " )");
        if(token == t) { return true;}
        else { return false;}
    }

    //TODO:OK
    // assert matching the token type, and move to next token
    public boolean match(TokenType t){
//    	System.out.println("	Lexer: match( " + t + " )");
        if(token == t) {nextToken(); return true;}
        else {return false;}
    }

    //TODO:OK
    // skip token and move next token
    public boolean skip(TokenType t){
//    	System.out.println("	Lexer: skip(" + t + " )");
        if(token == t) { nextToken(); return true;}
        else {nextToken(); return false;}
    }

    //TODO: FOR LEXER TEST
	private void print() {
    	while(index < source.length()) {
    		nextToken();
		}
    	System.out.println(TokenType.EOF);
	}

	//TODO:OK
	// test entrance
	public static void main(String[] args) {
    	String a = "((\\n.\\f.\\x.f (n f x))(\\f.\\x.x))";
    	Lexer lexer = new Lexer(a);
    	lexer.print();
	}
}
