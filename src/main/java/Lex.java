import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

class regular {
    String regexp;
    String action;

    public regular(String r, String a) {
        this.regexp = r;
        this.action = a;
    }
}

class NFA_list implements Serializable {
    ArrayList<NFA> NFAs;
    ArrayList<String> actions;

    public void setNFAs(ArrayList<NFA> nfas, ArrayList<String> actions) {
        this.NFAs = nfas;
        this.actions = actions;
    }

    public ArrayList<NFA> getNFAs() {
        return NFAs;
    }

    public ArrayList<String> getActions() {
        return actions;
    }
}

public class Lex {

    String assistant;
    ArrayList<regular> token_list;

    Lex(BufferedReader reader) throws IOException {
        String current_line = "";

        var regular_map = new HashMap<String, String>();
        //var token_map = new HashMap<String, String>();
        token_list = new ArrayList<regular>();
        //String assistant="";

        int part = 0;// 0 - regular definition  1 - rule and action  2 - assistant sentence
        var pattern = Pattern.compile("\\{.*?\\}");
        for (; ; ) {
            current_line = reader.readLine();
            if (null == current_line)
                break;
            if (current_line.equals(""))
                continue;
            // begin to process the line!
            if (current_line.charAt(0) == '%' && current_line.charAt(1) == '%') {
                part++;
                continue;
            }
            switch (part) {
                case 0:
                    // regular definition part:
                    this.regular_definition_lex(current_line, regular_map, pattern);
                    break;
                case 1:
                    // rule and action part
                    this.rule_action_lex(current_line, token_list, regular_map, pattern);
                    break;
                case 2:
                    // assistant function part:
                    if (current_line.charAt(0) == '#')
                        continue;
                    assistant += current_line + "\n";
                    break;
            }
        }
        // Now we have finished reading lex.txt, time to construct NFA!
        //NFA[] NFAs = new NFA[token_list.size()];
        var actions = new ArrayList<String>();
        //String [] actions=new String[token_list.size()];
        var fos = new FileOutputStream("NFAs.dat");
        var oos = new ObjectOutputStream(fos);
        var NFAs = new ArrayList<NFA>();
        // save all the NFAs to the serialized file
        int i = 0;
        for (regular regex : token_list) {
            NFAs.add(new NFA("(" + regex.regexp + ")"));
            actions.add(regex.action);
        }
        NFA_list nfa_list = new NFA_list();
        nfa_list.setNFAs(NFAs, actions);
        oos.writeObject(nfa_list);
        fos.close();
        oos.close();
    }

    public void generate_source_code() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/mac/Documents/University/Compilers/lex/src/main/java/Code_template.java")));
        String source_code = "";
        String current_line;
        int part = 0;
        for (; ; ) {
            current_line = reader.readLine();
            if (current_line == null)
                break;
            source_code += current_line + "\n";
            if (current_line.contains("%%")) {
                switch (part) {
                    case 0:
                        source_code += assistant;
                        part++;
                        break;
                    case 1:
                        for (int i = 0; i < token_list.size(); i++) {
                            source_code += token_list.get(i).action + "\n";
                        }
                        break;
                }
            }
        }
        var fos = new FileOutputStream("Lexical_Analyser.java");
        fos.write(source_code.getBytes(StandardCharsets.UTF_8));
        fos.close();
    }

    public void rule_action_lex(String current_line, ArrayList<regular> token_list, HashMap<String, String> regular_map, Pattern pattern) {

        if (current_line.charAt(0) == '#') return;
        String[] ruleAction = current_line.split("\s+", 2);
        var matcher_token = pattern.matcher(current_line);
        while (matcher_token.find()) {
            String current_group = matcher_token.group();
            ruleAction[0] = ruleAction[0].replace(current_group, regular_map.get(current_group.substring(1, current_group.length() - 1)));
        }
        // now ruleAction[0] is a Regex, and ruleAction[1] is the action
        token_list.add(new regular(ruleAction[0], ruleAction[1].substring(3, ruleAction[1].length() - 3)));
        System.out.println(ruleAction[0] + " " + ruleAction[1]);
    }

    public void regular_definition_lex(String current_line, HashMap<String, String> regular_map, Pattern pattern) {
        if (current_line.charAt(0) == '#') return;
        String[] definition = current_line.split("\s+", 2);
        var matcher_def = pattern.matcher(current_line);
        while (matcher_def.find()) {
            String current_group = matcher_def.group();
            definition[1] = definition[1].replace(current_group, regular_map.get(current_group.substring(1, current_group.length() - 1)));
        }
        regular_map.put(definition[0], definition[1]);
        System.out.println(definition[0] + " " + definition[1]);
        return;
    }

    public static void main(String[] args) {
        try (var reader = new BufferedReader(new InputStreamReader(new FileInputStream("/Users/mac/Documents/University/Compilers/lex/src/main/java/lex.txt"), "UTF-8"))) {
            var lex = new Lex(reader);
            lex.generate_source_code();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}