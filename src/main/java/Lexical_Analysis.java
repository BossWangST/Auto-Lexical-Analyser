import java.io.*;
import java.util.ArrayList;

class Token_Symbol {
    public ArrayList<token> tokens;
    public ArrayList<Object> symbols;
}

class token {
    int type;
    token_property tp = null;

    token(int t) {
        this.type = t;
    }

    token(int t, token_property tp) {
        this(t);
        this.tp = tp;
    }
}

class token_property {
    int line;

    token_property(int l) {
        this.line = l;
    }
}

class id_token extends token_property {
    String value;

    id_token(int l, String v) {
        super(l);
        this.value = v;
    }
}

class int_token extends token_property {
    int value;

    int_token(int l, int v) {
        super(l);
        this.value = v;
    }
}

class real_token extends token_property {
    double value;

    real_token(int l, double v) {
        super(l);
        this.value = v;
    }
}

class string_token extends token_property {
    String value;

    string_token(int l, String v) {
        super(l);
        this.value = v;
    }
}

class char_token extends token_property {
    char value;

    char_token(int l, char v) {
        super(l);
        this.value = v;
    }
}

public abstract class Lexical_Analysis {
    ArrayList<NFA> NFAs;
    ArrayList<String> Actions;
    BufferedReader reader;

    // %% Here put the assistant definitions and functions

    // %% Here put the main functions
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

    public Token_Symbol scanner() throws IOException, ClassNotFoundException {
        Token_Symbol token_symbol = new Token_Symbol();
        this.get_NFAs();
        String current_line;
        for(;;){
            current_line=reader.readLine();
            if(current_line==null)
                break;
            for(int i=0;i<NFAs.size();i++){
                if(NFAs.get(i).recognizes(current_line)){

                }
            }
        }
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
