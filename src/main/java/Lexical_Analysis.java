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
                res += "<" + Integer.toString(t.type) + "," + ((char_token) t).value + "," + Integer.toString(t.line) + ">\n";
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
    String value;

    char_token(int t, int l, String v) {
        super(t, l);
        this.value = v;
    }
}

public class Lexical_Analysis {
    ArrayList<NFA> NFAs;
    ArrayList<String> Actions;
    BufferedReader reader;

    // %% Here put the assistant definitions and functions

    public void get_NFAs() throws IOException, ClassNotFoundException {
        var fis = new FileInputStream("./src/main/java/NFAs.dat");
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
