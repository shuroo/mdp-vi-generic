package mdp.ctp;

import ctp.CTPEdge;
import org.jgrapht.graph.Vertex;
import utils.CollectionUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
        StringBuilder uniqueStateStr = new StringBuilder();
        uniqueStateStr.append("Ag_Location::" + this.agentLocation + ",");
        Iterator<CTPEdge> statusIterator = this.statuses.values().iterator();
        while (statusIterator.hasNext()) {
            uniqueStateStr.append(statusIterator.next().toString());
            if (statusIterator.hasNext()) {
                uniqueStateStr.append(",");
            }
        }
        stateId = uniqueStateStr.toString();
    }

    public void setBestAction(Action bestAction) {
        this.bestAction = bestAction;
    }

    // print states properly

}

