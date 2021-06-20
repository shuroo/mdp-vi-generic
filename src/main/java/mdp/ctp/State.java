package mdp.ctp;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Vertex;
import utils.CollectionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class State extends mdp.generic.State implements Comparable<State> {

    // extends best action?
    protected mdp.ctp.Action graphBestAction;

    private State parentState;

    // Visited - and not let to a solution - hence the state is invalid.
    private Boolean isAgentVisited = false;

    public HashMap<String, CTPEdge> getStatuses() {
        return statuses;
    }


    public State getParentState() {
        return parentState;
    }

    public void setParentState(State parentState) {
        this.parentState = parentState;
    }

    public Boolean getAgentVisited() {
        return isAgentVisited;
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
        if(action instanceof mdp.ctp.Action){
            this.graphBestAction = (mdp.ctp.Action)action;
        }
    }

    public mdp.ctp.Action getGraphBestAction(){
        return graphBestAction;
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

    public void setIsVisited(){
        this.isAgentVisited = true;
    }
}

