package mdp.ctp;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Vertex;
import utils.CollectionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

public class State extends mdp.generic.State {

    public State(Vertex agentLocation, Vector<CTPEdge> statuses) {
        this.agentLocation = agentLocation;
        this.statuses = CollectionUtils.edgeToMap(statuses);
        setStateId();
    }

    public HashMap<String, CTPEdge> getStatuses() {
        return statuses;
    }

    public void setAgentLocation(Vertex agentLocation) {
        this.agentLocation = agentLocation;
        setStateId();

    }

    public BlockingStatus getEdgeStatus(){

        if(this.bestAction == null){
            return null;
        }
        HashMap<String,BlockingStatus> statuses = new HashMap<String,BlockingStatus>();
        String edgeId = this.bestAction.toString();
        return statuses.get(edgeId);
    }

    public Vertex getAgentLocation() {
        return agentLocation;
    }

    public Double getStateProbability() {
        return stateProbability;
    }

    // The probability to occur - based on which edges are currently opened or closed in the current state and thier probsbilities.
    Double stateProbability;

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

    public void setBestAction(Action bestAction) {
        this.bestAction = bestAction;
    }

    // print states properly

}

