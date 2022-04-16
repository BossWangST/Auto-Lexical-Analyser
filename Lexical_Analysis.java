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
                res += "<" + Integer.toString(t.type) + "," + ((id_token) t).value + "," + Integer.toString(t.line);
            } else if (t instanceof int_token) {
                res += "<" + Integer.toString(t.type) + "," + Integer.toString(((int_token) t).value) + "," + Integer.toString(t.line);
            } else if (t instanceof real_token) {
                res += "<" + Integer.toString(t.type) + "," + Double.toString(((real_token) t).value) + "," + Integer.toString(t.line);
            } else if (t instanceof op_token) {
                res += "<" + Integer.toString(t.type) + "," + Integer.toString(((op_token) t).value) + "," + Integer.toString(t.line);
            } else if (t instanceof string_token) {
                res += "<" + Integer.toString(t.type) + "," + ((string_token) t).value + "," + Integer.toString(t.line);
            } else if (t instanceof char_token) {
                res += "<" + Integer.toString(t.type) + "," + Character.toString(((char_token) t).value) + "," + Integer.toString(t.line);
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
final int LP = 400;
final int RP = 401;
final int LSBR = 402;
final int RSBR = 403;
final int LBR = 404;
final int RBR = 405;
final int COMMA = 406;
final int SEMICOLON = 407;
public int install(int type){
    token_sequence.tokens.add(new token(type,line));
}
public int installID(int type) {
    token_sequence.tokens.add(new id_token(type,line,token_value));
}
public int installINT(int type) {
    token_sequence.tokens.add(new int_token(type,line,token_value));
}
public int installREAL(int type) {
    token_sequence.tokens.add(new real_token(type,line,token_value));
}
public int installOP(int type, int op) {
    token_sequence.tokens.add(new op_token(type,line,op));
}
public int installSTRING(int type) {
    token_sequence.tokens.add(new string_token(type,line,token_value));
}
public int installCHAR(int type) {
    token_sequence.tokens.add(new char_token(type,line,token_value));
}

    public void get_NFAs() throws IOException, ClassNotFoundException {
        var fis = new FileInputStream("NFAs.dat");
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
            for (int i = 0; i < NFAs.size(); i++) {
                if (NFAs.get(i).recognizes(current_line)) {
                    String token_value = current_line.substring(0, NFAs.get(i).getLen());
                    //%% Here insert assistant functions, OK!
/* no action and no return*/
if(i==0) install(AUTO,line);
if(i==1) install(ELSE,line);
if(i==2) install(LONG,line);
if(i==3) install(SWITCH,line);
if(i==4) install(BREAK,line);
if(i==5) install(ENUM,line);
if(i==6) install(REGISTER,line);
if(i==7) install(TYPEDEF,line);
if(i==8) install(CASE,line);
if(i==9) install(EXTERN,line);
if(i==10) install(RETURN,line);
if(i==11) install(UNION,line);
if(i==12) install(CHAR,line);
if(i==13) install(FLOAT,line);
if(i==14) install(SHORT,line);
if(i==15) install(UNSIGNED,line);
if(i==16) install(CONST,line);
if(i==17) install(FOR,line);
if(i==18) install(SIGNED,line);
if(i==19) install(VOID,line);
if(i==20) install(CONTINUE,line);
if(i==21) install(GOTO,line);
if(i==22) install(SIZEOF,line);
if(i==23) install(VOLATILE,line);
if(i==24) install(VOLATILE,line);
if(i==25) install(DEFAULT,line);
if(i==26) install(IF,line);
if(i==27) install(STATIC,line);
if(i==28) install(WHILE,line);
if(i==29) install(DO,line);
if(i==30) install(INT,line);
if(i==31) install(STRUCT,line);
if(i==32) install(_PACKED,line);
if(i==33) install(DOUBLE,line);
if(i==34) installID(ID);
if(i==35) installINT(INT_NUM);
if(i==36) installREAL(REAL_NUM);
if(i==37) installSTRING(STRING_LITERAL);
if(i==38) installCHAR(CHAR_LITERAL);
if(i==39) installOP(RELOP,LT);
if(i==40) installOP(RELOP,LE);
if(i==41) installOP(RELOP,EQ);
if(i==42) installOP(RELOP,NE);
if(i==43) installOP(RELOP,GT);
if(i==44) installOP(RELOP,GE);
if(i==45) installOP(ARILOP,PLUS);
if(i==46) installOP(ARILOP,MINUS);
if(i==47) installOP(ARILOP,MULTIPLY);
if(i==48) installOP(ARILOP,DIVIDE);
if(i==49) installOP(ARILOP,MOD);
if(i==50) installOP(LOGOP,AND);
if(i==51) installOP(LOGOP,OR);
if(i==52) installOP(LOGOP,NOT);
if(i==53) installOP(BITOP,LSHIFT);
if(i==54) installOP(BITOP,RSHIFT);
if(i==55) installOP(BITOP,BITAND);
if(i==56) installOP(BITOP,BITNOT);
if(i==57) installOP(BITOP,BITNOT);
if(i==58) installOP(BITOP,BITXOR);
if(i==59) installOP(ASSIGNOP,ASSIGN);
if(i==60) installOP(ASSIGNOP,ADDASSIGN);
if(i==61) installOP(ASSIGNOP,MINUSASSIGN);
if(i==62) installOP(ASSIGNOP,MULTIPLYASSIGN);
if(i==63) installOP(ASSIGNOP,DIVIDEASSIGN);
if(i==64) installOP(ASSIGNOP,MODASSIGN);
if(i==65) installOP(ASSIGNOP,ANDASSIGN);
if(i==66) installOP(ASSIGNOP,ORASSIGN);
if(i==67) installOP(ASSIGNOP,XORASSIGN);
if(i==68) installOP(ASSIGNOP,LSHIFTASSIGN);
if(i==69) installOP(ASSIGNOP,RSHIFTASSIGN);
if(i==70) install(LP);
if(i==71) install(RP);
if(i==72) install(LSBR);
if(i==73) install(RSBR);
if(i==74) install(LBR);
if(i==75) install(RBR);
if(i==76) install(COMMA);
if(i==77) install(SEMICOLON);
                }
            }
        }
        return token_sequence;
    }

    public static void main(String[] args) {
        try {
            var reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
            var lexical = new Lexical_Analysis(reader);
            //var token_symbol=lexical.scanner();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
