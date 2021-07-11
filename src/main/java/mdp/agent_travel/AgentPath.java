package mdp.agent_travel;

import mdp.ctp.State;
import mdp.generic.Action;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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
    private List<State> path;


    public void setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public List<State> getPath() {
        return path;
    }


    public void addGoBackState(State current) {
        State parentSt = current.getParentState();
        if (parentSt == null) {
            return;
        }
        State goBackState = new State(current.getAgentLocation(), new Vector(current.getStatuses().values()));

                // Fetch the best action from the parent (and reverse it maybe - TBD)!);

        mdp.ctp.Action originalAction = agent.mdp.getExtendedAction(parentSt);
        mdp.ctp.Action reversedAction = new mdp.ctp.Action(originalAction.getSourceEdge());
        reversedAction.reverseAction();
        goBackState.setBestAction(reversedAction);
        // Go Back - read the current action with its cost:
        this.addToPath(goBackState);
    }

    public void addToPath(State current) {

        this.path.add(current);
        Double currentReward = current.getBestAction() == null ? 0.0 : agent.mdp.getExtendedAction(current).getSourceEdge().getReward();

        System.out.println("||**Adding cost: " + currentReward + "to original cost:"+this.pathCost+" to path by best action"+current.getBestAction()+" in " +
                "state:" + current+"**||");
        this.pathCost += currentReward;

    }

    /**
     * Go back , update the path, and update the agent cost upon regression:
     *
     * @param agent
     */

    public AgentPath(Agent agent) {

        this.agent = agent;
        this.path = new LinkedList<State>();
        this.pathCost = 0.0;
    }


    public void setSucceeded(Boolean succeeded) {
        this.isSucceeded = succeeded;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append("|Path:");
        this.path.stream().forEach(stt -> {
            res.append(stt.toString() + "|Action:" + stt.getBestAction());
            res.append(System.getProperty("line.separator"));
        });
        res.append("|" + isSucceeded + "|" + pathCost + "|");
        return res.toString();

    }

}
