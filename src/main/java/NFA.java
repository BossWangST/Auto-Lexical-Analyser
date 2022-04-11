import java.io.*;
import java.util.Stack;

public class NFA implements Serializable {

    private Digraph graph;     // digraph of epsilon transitions
    private String regexp;     // regular expression
    private final int m;       // number of characters in regular expression
    int len; // [start,len) satisfies the rule!

    public int getLen(){
        return len;
    }
    /**
     * Initializes the NFA from the specified regular expression.
     *
     * @param regexp the regular expression
     */
    public NFA(String regexp) {
        this.regexp = regexp;
        m = regexp.length();
        Stack<Integer> ops = new Stack<Integer>();
        graph = new Digraph(m + 1);
        for (int i = 0; i < m; i++) {
            int lp = i;
            if (regexp.charAt(i) == '(' || regexp.charAt(i) == '|')
                ops.push(i);
            else if (regexp.charAt(i) == ')') {
                int or = ops.pop();

                // 2-way or operator
                if (regexp.charAt(or) == '|') {
                    lp = ops.pop();
                    graph.addEdge(lp, or + 1);
                    graph.addEdge(or, i);
                } else if (regexp.charAt(or) == '(')
                    lp = or;
                else assert false;
            }

            // closure operator (uses 1-character lookahead)
            if (i < m - 1 && regexp.charAt(i + 1) == '*') {
                graph.addEdge(lp, i + 1);
                graph.addEdge(i + 1, lp);
            }
            if (regexp.charAt(i) == '(' || regexp.charAt(i) == '*' || regexp.charAt(i) == ')')
                graph.addEdge(i, i + 1);
        }
        if (ops.size() != 0)
            throw new IllegalArgumentException("Invalid regular expression");
    }

    /**
     * Returns true if the text is matched by the regular expression.
     *
     * @param txt the text
     * @return {@code true} if the text is matched by the regular expression,
     * {@code false} otherwise
     */
    public boolean recognizes(String txt) {
        DirectedDFS dfs = new DirectedDFS(graph, 0);
        Bag<Integer> pc = new Bag<Integer>();
        for (int v = 0; v < graph.V(); v++)
            if (dfs.marked(v)) pc.add(v);

        // Compute possible NFA states for txt[i+1]
        for (int i = 0; i < txt.length(); i++) {
            if (txt.charAt(i) == '*' || txt.charAt(i) == '|' || txt.charAt(i) == '(' || txt.charAt(i) == ')')
                throw new IllegalArgumentException("text contains the metacharacter '" + txt.charAt(i) + "'");

            Bag<Integer> match = new Bag<Integer>();
            for (int v : pc) {
                if (v == m) continue;
                if ((regexp.charAt(v) == txt.charAt(i)) || regexp.charAt(v) == '.')
                    match.add(v + 1);// check the next char!!!
            }
            //if (match.isEmpty()) continue;
            if (match.isEmpty()) {
                this.len = i;
                break;
            }//保证从头开始看，必须是串的头部就符合NFA的构造，同时还需要记录到哪里停下

            dfs = new DirectedDFS(graph, match);
            pc = new Bag<Integer>();
            for (int v = 0; v < graph.V(); v++)
                if (dfs.marked(v)) pc.add(v);

            // optimization if no states reachable
            if (pc.size() == 0) return false;
        }

        // check for accept state
        for (int v : pc)
            if (v == m) return true;
        return false;
    }

    public static void main(String[] args) {
        /*
        String regexp2 = "(ab*a*|c)";
        //String regexp = "(" + args[0] + ")";
        //String txt = args[1];
        String txt2 = "c";
        NFA nfa = new NFA(regexp2);
        System.out.println(nfa.recognizes(txt2));
        */
        var nfa = new NFA("(ab*a*|c)");
        try {
            var fos = new FileOutputStream("nfa_test.dat");
            var oos = new ObjectOutputStream(fos);
            oos.writeObject(nfa);
            oos.close();
            var fis = new FileInputStream("nfa_test.dat");
            var ios = new ObjectInputStream(fis);
            var nfa_read = (NFA) ios.readObject();
            System.out.println(nfa_read.recognizes("abaaaba"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}