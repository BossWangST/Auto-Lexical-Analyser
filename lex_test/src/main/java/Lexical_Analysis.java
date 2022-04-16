import java.io.*;
import java.util.ArrayList;

class Token_Sequence {
    public ArrayList<token> tokens;

    public Token_Sequence() {
        tokens = new ArrayList<token>();
    }

    public String toString() {
        String res = "";
        for (token t : tokens) {
            if (t instanceof id_token) {
                res += "<" + Integer.toString(t.type) + "," + ((id_token) t).value + "," + Integer.toString(t.line) + ">\n";
            } else if (t instanceof int_token) {
                res += "<" + Integer.toString(t.type) + "," + Integer.toString(((int_token) t).value) + "," + Integer.toString(t.line) + ">\n";
            } else if (t instanceof real_token) {
                res += "<" + Integer.toString(t.type) + "," + Double.toString(((real_token) t).value) + "," + Integer.toString(t.line) + ">\n";
            } else if (t instanceof op_token) {
                res += "<" + Integer.toString(t.type) + "," + Integer.toString(((op_token) t).value) + "," + Integer.toString(t.line) + ">\n";
            } else if (t instanceof string_token) {
                res += "<" + Integer.toString(t.type) + "," + ((string_token) t).value + "," + Integer.toString(t.line) + ">\n";
            } else if (t instanceof char_token) {
                res += "<" + Integer.toString(t.type) + "," + Character.toString(((char_token) t).value) + "," + Integer.toString(t.line) + ">\n";
            } else {
                res += "<" + Integer.toString(t.type) + "," + Integer.toString(t.line) + ">\n";
            }
        }
        return res;
    }
}

class token {
    int type;
    int line;

    token(int t, int l) {
        this.type = t;
        this.line = l;
    }

}

class id_token extends token {
    String value;

    id_token(int t, int l, String v) {
        super(t, l);
        this.value = v;
    }
}

class int_token extends token {
    int value;

    int_token(int t, int l, String v) {
        super(t, l);
        this.value = Integer.valueOf(v);
    }
}

class real_token extends token {
    double value;

    real_token(int t, int l, String v) {
        super(t, l);
        this.value = Double.valueOf(v);
    }
}

class op_token extends token {
    int value;

    op_token(int t, int l, int v) {
        super(t, l);
        this.value = v;
    }
}

class string_token extends token {
    String value;

    string_token(int t, int l, String v) {
        super(t, l);
        this.value = v;
    }
}

class char_token extends token {
    char value;

    char_token(int t, int l, String v) {
        super(t, l);
        this.value = v.charAt(0);
    }
}

public class Lexical_Analysis {
    ArrayList<NFA> NFAs;
    ArrayList<String> Actions;
    BufferedReader reader;

    // %% Here put the assistant definitions and functions
final int AUTO = 0;
final int ELSE = 1;
final int LONG = 2;
final int SWITCH = 3;
final int BREAK = 4;
final int ENUM = 5;
final int REGISTER = 6;
final int TYPEDEF = 7;
final int CASE = 8;
final int EXTERN = 9;
final int RETURN = 10;
final int UNION = 11;
final int CHAR = 12;
final int FLOAT = 13;
final int SHORT = 14;
final int UNSIGNED = 15;
final int CONST = 16;
final int FOR = 17;
final int SIGNED = 18;
final int VOID = 19;
final int CONTINUE = 20;
final int GOTO = 21;
final int SIZEOF = 22;
final int VOLATILE = 23;
final int DEFAULT = 24;
final int IF = 25;
final int STATIC = 26;
final int WHILE = 27;
final int DO = 28;
final int INT = 29;
final int STRUCT = 30;
final int _PACKED = 31;
final int DOUBLE = 32;
final int ID = 100;
final int INT_NUM = 200;
final int REAL_NUM = 201;
final int STRING_LITERAL = 202;
final int CHAR_LITERAL = 203;
final int RELOP = 300;
final int LT = 0;
final int LE = 1;
final int EQ = 2;
final int NE = 3;
final int GT = 4;
final int GE = 5;
final int ARILOP = 301;
final int PLUS = 0;
final int MINUS = 1;
final int MULTIPLY = 2;
final int DIVIDE = 3;
final int MOD = 4;
final int LOGOP = 302;
final int AND = 0;
final int OR = 1;
final int NOT = 2;
final int BITOP = 303;
final int LSHIFT = 0;
final int RSHIFT = 1;
final int BITAND = 2;
final int BITOR = 3;
final int BITNOT = 4;
final int BITXOR = 5;
final int ASSIGNOP = 304;
final int ASSIGN = 1;
final int ADDASSIGN = 2;
final int MINUSASSIGN = 3;
final int MULTIPLYASSIGN = 4;
final int DIVIDEASSIGN = 5;
final int MODASSIGN = 6;
final int ANDASSIGN = 7;
final int ORASSIGN = 8;
final int XORASSIGN = 10;
final int LSHIFTASSIGN = 11;
final int RSHIFTASSIGN = 12;
final int PUNCTUATION=400;
final int LP = 0;
final int RP = 1;
final int LSBR = 2;
final int RSBR = 3;
final int LBR = 4;
final int RBR = 5;
final int COMMA = 6;
final int SEMICOLON = 7;
final int MACRO = 8;
final int DOT = 9;

    public void get_NFAs() throws IOException, ClassNotFoundException {
        var fis = new FileInputStream("/Users/mac/Documents/University/Compilers/lex/lex_test/target/classes/NFAs.dat");
        var ois = new ObjectInputStream(fis);
        var nfa_list = (NFA_list) ois.readObject();
        NFAs = nfa_list.getNFAs();
        Actions = nfa_list.getActions();
        fis.close();
        ois.close();
    }

    public Lexical_Analysis(BufferedReader reader) {
        this.reader = reader;
    }

    public Token_Sequence scanner() throws IOException, ClassNotFoundException {
        Token_Sequence token_sequence = new Token_Sequence();
        this.get_NFAs();
        String current_line;
        int line = 1;
        for (; ; line++) {
            current_line = reader.readLine();
            if (current_line == null) break;
            while (!current_line.equals(""))
                for (int i = 0; i < NFAs.size(); i++) {
                    if (NFAs.get(i).recognizes(current_line)) {
                        int len = NFAs.get(i).getLen();
                        String token_value = current_line.substring(0, len);
                        current_line = current_line.substring(len, current_line.length());
                        //%% Here insert assistant functions, OK!
if(i==0) break;/* no action and no return*/
else if((i-1)==0) {token_sequence.tokens.add(new token(AUTO,line));break;}
else if((i-1)==1) {token_sequence.tokens.add(new token(ELSE,line));break;}
else if((i-1)==2) {token_sequence.tokens.add(new token(LONG,line));break;}
else if((i-1)==3) {token_sequence.tokens.add(new token(SWITCH,line));break;}
else if((i-1)==4) {token_sequence.tokens.add(new token(BREAK,line));break;}
else if((i-1)==5) {token_sequence.tokens.add(new token(ENUM,line));break;}
else if((i-1)==6) {token_sequence.tokens.add(new token(REGISTER,line));break;}
else if((i-1)==7) {token_sequence.tokens.add(new token(TYPEDEF,line));break;}
else if((i-1)==8) {token_sequence.tokens.add(new token(CASE,line));break;}
else if((i-1)==9) {token_sequence.tokens.add(new token(EXTERN,line));break;}
else if((i-1)==10) {token_sequence.tokens.add(new token(RETURN,line));break;}
else if((i-1)==11) {token_sequence.tokens.add(new token(UNION,line));break;}
else if((i-1)==12) {token_sequence.tokens.add(new token(CHAR,line));break;}
else if((i-1)==13) {token_sequence.tokens.add(new token(FLOAT,line));break;}
else if((i-1)==14) {token_sequence.tokens.add(new token(SHORT,line));break;}
else if((i-1)==15) {token_sequence.tokens.add(new token(UNSIGNED,line));break;}
else if((i-1)==16) {token_sequence.tokens.add(new token(CONST,line));break;}
else if((i-1)==17) {token_sequence.tokens.add(new token(FOR,line));break;}
else if((i-1)==18) {token_sequence.tokens.add(new token(SIGNED,line));break;}
else if((i-1)==19) {token_sequence.tokens.add(new token(VOID,line));break;}
else if((i-1)==20) {token_sequence.tokens.add(new token(CONTINUE,line));break;}
else if((i-1)==21) {token_sequence.tokens.add(new token(GOTO,line));break;}
else if((i-1)==22) {token_sequence.tokens.add(new token(SIZEOF,line));break;}
else if((i-1)==23) {token_sequence.tokens.add(new token(VOLATILE,line));break;}
else if((i-1)==24) {token_sequence.tokens.add(new token(VOLATILE,line));break;}
else if((i-1)==25) {token_sequence.tokens.add(new token(DEFAULT,line));break;}
else if((i-1)==26) {token_sequence.tokens.add(new token(IF,line));break;}
else if((i-1)==27) {token_sequence.tokens.add(new token(STATIC,line));break;}
else if((i-1)==28) {token_sequence.tokens.add(new token(WHILE,line));break;}
else if((i-1)==29) {token_sequence.tokens.add(new token(DO,line));break;}
else if((i-1)==30) {token_sequence.tokens.add(new token(INT,line));break;}
else if((i-1)==31) {token_sequence.tokens.add(new token(STRUCT,line));break;}
else if((i-1)==32) {token_sequence.tokens.add(new token(_PACKED,line));break;}
else if((i-1)==33) {token_sequence.tokens.add(new token(DOUBLE,line));break;}
else if((i-1)==34) {token_sequence.tokens.add(new id_token(ID,line,token_value));break;}
else if((i-1)==35) {token_sequence.tokens.add(new int_token(INT_NUM,line,token_value));break;}
else if((i-1)==36) {token_sequence.tokens.add(new real_token(REAL_NUM,line,token_value));break;}
else if((i-1)==37) {token_sequence.tokens.add(new string_token(STRING_LITERAL,line,token_value));break;}
else if((i-1)==38) {token_sequence.tokens.add(new char_token(CHAR_LITERAL,line,token_value));break;}
else if((i-1)==39) {token_sequence.tokens.add(new op_token(RELOP,line,LT));break;}
else if((i-1)==40) {token_sequence.tokens.add(new op_token(RELOP,line,LE));break;}
else if((i-1)==41) {token_sequence.tokens.add(new op_token(RELOP,line,EQ));break;}
else if((i-1)==42) {token_sequence.tokens.add(new op_token(RELOP,line,NE));break;}
else if((i-1)==43) {token_sequence.tokens.add(new op_token(RELOP,line,GT));break;}
else if((i-1)==44) {token_sequence.tokens.add(new op_token(RELOP,line,GE));break;}
else if((i-1)==45) {token_sequence.tokens.add(new op_token(ARILOP,line,PLUS));break;}
else if((i-1)==46) {token_sequence.tokens.add(new op_token(ARILOP,line,MINUS));break;}
else if((i-1)==47) {token_sequence.tokens.add(new op_token(ARILOP,line,MULTIPLY));break;}
else if((i-1)==48) {token_sequence.tokens.add(new op_token(ARILOP,line,DIVIDE));break;}
else if((i-1)==49) {token_sequence.tokens.add(new op_token(ARILOP,line,MOD));break;}
else if((i-1)==50) {token_sequence.tokens.add(new op_token(LOGOP,line,AND));break;}
else if((i-1)==51) {token_sequence.tokens.add(new op_token(LOGOP,line,OR));break;}
else if((i-1)==52) {token_sequence.tokens.add(new op_token(LOGOP,line,NOT));break;}
else if((i-1)==53) {token_sequence.tokens.add(new op_token(BITOP,line,LSHIFT));break;}
else if((i-1)==54) {token_sequence.tokens.add(new op_token(BITOP,line,RSHIFT));break;}
else if((i-1)==55) {token_sequence.tokens.add(new op_token(BITOP,line,BITAND));break;}
else if((i-1)==56) {token_sequence.tokens.add(new op_token(BITOP,line,BITNOT));break;}
else if((i-1)==57) {token_sequence.tokens.add(new op_token(BITOP,line,BITNOT));break;}
else if((i-1)==58) {token_sequence.tokens.add(new op_token(BITOP,line,BITXOR));break;}
else if((i-1)==59) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,ASSIGN));break;}
else if((i-1)==60) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,ADDASSIGN));break;}
else if((i-1)==61) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,MINUSASSIGN));break;}
else if((i-1)==62) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,MULTIPLYASSIGN));break;}
else if((i-1)==63) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,DIVIDEASSIGN));break;}
else if((i-1)==64) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,MODASSIGN));break;}
else if((i-1)==65) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,ANDASSIGN));break;}
else if((i-1)==66) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,ORASSIGN));break;}
else if((i-1)==67) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,XORASSIGN));break;}
else if((i-1)==68) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,LSHIFTASSIGN));break;}
else if((i-1)==69) {token_sequence.tokens.add(new op_token(ASSIGNOP,line,RSHIFTASSIGN));break;}
else if((i-1)==70) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,LP));break;}
else if((i-1)==71) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,RP));break;}
else if((i-1)==72) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,LSBR));break;}
else if((i-1)==73) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,RSBR));break;}
else if((i-1)==74) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,LBR));break;}
else if((i-1)==75) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,RBR));break;}
else if((i-1)==76) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,COMMA));break;}
else if((i-1)==77) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,SEMICOLON));break;}
else if((i-1)==78) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,MACRO));break;}
else if((i-1)==79) {token_sequence.tokens.add(new op_token(PUNCTUATION,line,DOT));break;}

                    }
                }
        }
        return token_sequence;
    }

    public static void main(String[] args) {
        try {
            var reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
            var lexical = new Lexical_Analysis(reader);
            var token_symbol = lexical.scanner();
            System.out.println(token_symbol.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
