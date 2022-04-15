import java.io.*;
import java.util.ArrayList;

class Token_Sequence {
    public ArrayList<token> tokens;

    public Token_Sequence() {
        tokens = new ArrayList<token>();
    }
}

class token {
    int type;
    token_property tp = null;

    token(int t) {
        this.type = t;
    }

    token(int t, int l) {
        this(t);
        this.tp = new token_property(l);
    }
}

class token_property {
    int line;

    token_property(int l) {
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
