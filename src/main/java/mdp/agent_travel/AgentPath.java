package mdp.agent_travel;

import mdp.ctp.State;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to represent a single travel of the agent over the graph
 */
public class AgentPath {

    private Agent agent;
    private Boolean isSucceeded = null;

    public String getFailureMessage() {
        return failureMessage;
    }

    private String failureMessage;
    private Double pathCost = 0.0;
    private Set<State> path;


    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public Set<State> getPath() {
        return path;
    }


    public void addToPath(State current) {
        this.path.add(current);

        Double currentReward = current.getBestAction() == null? 0.0 :agent.mdp.getExtendedAction(current).getSourceEdge().getReward();
        this.pathCost += currentReward;
        System.out.println("Adding: "+this.pathCost+" to path with state:"+current);
    }

    /**
     * Go back , update the path, and update the agent cost upon regression:
     * @param agent
     */

    public AgentPath(Agent agent){

        this.agent = agent;
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
        this.path.stream().forEach(stt-> { res.append(stt.toString());
        res.append(System.getProperty("line.separator")); });
        res.append("|"+isSucceeded+"|"+pathCost+"|");
        return res.toString();

    }

}
