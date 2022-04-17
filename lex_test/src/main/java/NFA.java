import java.io.*;
import java.util.ArrayList;
import java.util.Stack;

public class NFA implements Serializable {

    private Digraph graph;     // digraph of epsilon transitions
    private String regexp;     // regular expression
    private int m;       // number of characters in regular expression
    int len = 0; // [start,len) satisfies the rule!

    public int getLen() {
        return len;
    }

    /**
     * Initializes the NFA from the specified regular expression.
     *
     * @param regex the regular expression
     */
    public NFA(String regex) {
        this.regexp = regex.replaceAll("\\\\\\\\", "@");
        m = regexp.length();
        var ops = new Stack<Integer>();
        graph = new Digraph(m + 1);
        for (int i = 0; i < m; i++) {
            int lp = i;
            if (i < m - 1 && regexp.charAt(i) == '@') {// && regexp.charAt(i + 1) == '@') {
                //转义字符
                regexp = regexp.substring(0, i) + regexp.substring(i + 1, m);
                m -= 1;
                continue;
            } else if (regexp.charAt(i) == '(' || regexp.charAt(i) == '|')
                ops.push(i);
            else if (regexp.charAt(i) == ')') {
                int or = ops.pop();

                // 2-way or operator
                if (regexp.charAt(or) == '|') {
                    var or_list = new ArrayList<Integer>();
                    or_list.add(or);
                    int current_op_index = ops.pop();
                    // if there are multiple "or", then we process it here.
                    while (!ops.empty() && regexp.charAt(current_op_index) == '|') {
                        or_list.add(current_op_index);
                        current_op_index = ops.pop();
                    }
                    lp = current_op_index;
                    for (Integer or_index : or_list) {
                        //左括号指向每一个or的下一个字符，同时让每一个or的字符都可以到达当前的右括号
                        graph.addEdge(lp, or_index + 1);
                        graph.addEdge(or_index, i);
                    }
                    //graph.addEdge(lp, or + 1);
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
        this.len = 0;
        DirectedDFS dfs = new DirectedDFS(graph, 0);
        var pc = new Bag<Integer>();
        for (int v = 0; v < graph.V(); v++)
            if (dfs.marked(v)) pc.add(v);

        // Compute possible NFA states for txt[i+1]
        for (int i = 0; i < txt.length(); i++) {
            if (txt.charAt(i) == '*' || txt.charAt(i) == '|')// || txt.charAt(i) == '(' || txt.charAt(i) == ')')
                throw new IllegalArgumentException("text contains the metacharacter '" + txt.charAt(i) + "'");
            if (i > 1 && (txt.charAt(i) == '(' || txt.charAt(i) == ')'))
                break;

            Bag<Integer> match = new Bag<Integer>();
            boolean matched = false;
            for (int v : pc) {
                if (v == m) continue;
                if ((regexp.charAt(v) == txt.charAt(i))) {
                    if (!matched) {
                        matched = true;
                        len++;
                    }
                    match.add(v + 1);// check the next char!!!
                }
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
            if (v == m) {
                //len = txt.length();
                return true;
            }
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
        //var nfa = new NFA("(\\\\(\\\\)\\\\(\\\\(\\\\))");
        //var nfa = new NFA("(a|b|c)(1|2|3|a|b|c)*");
        //var nfa = new NFA("(_|q|w|e|r|t|y|u|i|o|p|a|s|d|f|g|h|j|k|l|z|x|c|v|b|n|m|Q|W|E|R|T|Y|U|I|O|P|A|S|D|F|G|H|J|K|L|Z|X|C|V|B|N|M)((_|q|w|e|r|t|y|u|i|o|p|a|s|d|f|g|h|j|k|l|z|x|c|v|b|n|m|Q|W|E|R|T|Y|U|I|O|P|A|S|D|F|G|H|J|K|L|Z|X|C|V|B|N|M)|(1|2|3|4|5|6|7|8|9|0))*");
        //var nfa=new NFA("\\\\\"(( |    )( |    )*|(_|q|w|e|r|t|y|u|i|o|p|a|s|d|f|g|h|j|k|l|z|x|c|v|b|n|m|Q|W|E|R|T|Y|U|I|O|P|A|S|D|F|G|H|J|K|L|Z|X|C|V|B|N|M)|(1|2|3|4|5|6|7|8|9|0)|(~|!|@|#|$|%|^|&|*|\\\\(|\\\\)|_|+|-|=|,|.|<|>|?|/|[|]|:|;|\\\\{|\\\\}\\\\\\))*\\\\\"");
        //var nfa=new NFA("\\\\\"(m|a)(a|i|n|\\\\\\)*\\\\\"");
        //var nfa=new NFA("\\\\\\n");
        var nfa = new NFA(";");
        try {
            var fos = new FileOutputStream("nfa_test.dat");
            var oos = new ObjectOutputStream(fos);
            oos.writeObject(nfa);
            oos.close();
            var fis = new FileInputStream("nfa_test.dat");
            var ios = new ObjectInputStream(fis);
            var nfa_read = (NFA) ios.readObject();
            System.out.println(nfa_read.recognizes(";") + "\n" + Integer.toString(nfa_read.len));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}