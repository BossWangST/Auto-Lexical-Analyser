import java.io.Serializable;
import java.util.ArrayList;

public class NFA_list implements Serializable {
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
