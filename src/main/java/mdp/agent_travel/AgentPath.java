package mdp.agent_travel;

import mdp.ctp.State;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Class to represent a single travel of the agent over the graph
 */
public class AgentPath {

    private Boolean isSucceeded = null;

    public String getFailureMessage() {
        return failureMessage;
    }

    private String failureMessage;
    private Double pathCost;
    private Set<State> path;


    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public Set<State> getPath() {
        return path;
    }

    public void addToPath(State state) {
        this.pathCost += state.getUtility();
        this.path.add(state);
    }

    public AgentPath(){
        this.path = new HashSet<State>();
        this.pathCost = 0.0;
    }

    public void setSucceeded(Boolean succeeded){
        this.isSucceeded = succeeded;
    }

    @Override
    public String toString(){
        StringBuilder res = new StringBuilder();
        res.append("|Path:");
        path.stream().forEach(stt-> { res.append(stt.toString());
        res.append(System.getProperty("line.separator")); });
        res.append("|"+isSucceeded+"|"+pathCost+"|");
        return res.toString();

    }

}
