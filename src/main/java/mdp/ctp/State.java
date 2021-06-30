package mdp.ctp;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Vertex;
import utils.CollectionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class State extends mdp.generic.State implements Comparable<State> {

    // Visited - and not let to a solution - hence the state is invalid.
    private Integer agentVisits = 0;

    // The index to approach the current best action
    //private Integer agentActionsIndex = -1;

    private State parentState;

    public void setAgentVisits() {
        this.agentVisits++;
        //this.agentActionsIndex++;
    }

    /**
     * Set the next action and return it , when exists:
     * @return
     */
    public State copyStateSetNextBestAction(){

        // Check whether the agent has no actions not yet visited:
        if(hasNextBestAction()){
            return null;
        }

        State newSt  = new State(this.getAgentLocation(),new Vector(this.getStatuses().values()));

        newSt.bestAction = bestActions.get(agentVisits);
        newSt.minimalUtility = bestAction.getUtility();
        newSt.agentVisits = agentVisits;
        newSt.bestActions = bestActions;
        return newSt;
    }


    public HashMap<String, CTPEdge> getStatuses() {
        return statuses;
    }


    public State getParentState() {
        return parentState;
    }

    public void setParentState(State parentState) {
        this.parentState = parentState;
    }


    // Check if the agent is already visited enough
    public Boolean hasNextBestAction() {
        return bestActions!=null && agentVisits == bestActions.size();
    }

    public State(Vertex agentLocation, Vector<CTPEdge> statuses) {
        this.agentLocation = agentLocation;
        this.statuses = CollectionUtils.edgeToMap(statuses);
        setStateId();
    }

    public void setAgentLocation(Vertex agentLocation) {
        this.agentLocation = agentLocation;
        setStateId();

    }

    @Override
    public void setBestAction(mdp.generic.Action action){
        this.bestAction = action;

    }

       public BlockingStatus getEdgeStatus(){

        if(this.bestAction == null){
            return null;
        }
        String edgeId = this.bestAction.toString();
        return this.getStatuses().get(edgeId).getStatus();
    }

    public Vertex getAgentLocation() {
        return agentLocation;
    }

    // Vector of edgeStatuses

    private void setStateId() {

        stateId = buildId(this.agentLocation,this.getStatuses());
    }

    public static String buildId(Vertex agLoc, HashMap<String,CTPEdge> statuses){
        StringBuilder uniqueStateStr = new StringBuilder();
        uniqueStateStr.append("Ag_Location::" +agLoc + ",");
        Iterator<CTPEdge> statusIterator = statuses.values().iterator();
        while (statusIterator.hasNext()) {
            uniqueStateStr.append(statusIterator.next().toString());
            if (statusIterator.hasNext()) {
                uniqueStateStr.append(",");
            }
        }

        return uniqueStateStr.toString();
    }

    // print states properly

    @Override
    public int compareTo(State state) {
        return  this.getUtility() > state.getUtility()?1:-1;
    }

}

