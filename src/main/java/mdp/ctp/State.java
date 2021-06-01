package mdp.ctp;

import ctp.CTPEdge;
import org.jgrapht.graph.Vertex;

import java.util.Iterator;
import java.util.Vector;

public class State extends mdp.generic.State {

    Vertex agentLocation;
    Vector<CTPEdge> statuses;

    public State(Vertex agentLocation, Vector<CTPEdge> statuses){
        this.agentLocation = agentLocation;
        this.statuses = statuses;
    }

        public String getStateId() {
            return stateId;
        }

        private String stateId;

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

        public Vector<CTPEdge> getEdgeStatuses() {
            return edgeStatuses;
        }

        // Vector of edgeStatuses
        Vector<CTPEdge> edgeStatuses;

        private void setStateId() {
            StringBuilder uniqueStateStr = new StringBuilder();
            uniqueStateStr.append("Ag_Location::"+this.agentLocation+",");
            Iterator<CTPEdge> statusIterator  = edgeStatuses.iterator();
            while(statusIterator.hasNext()) {
                uniqueStateStr.append(statusIterator.next().toString());
                if(statusIterator.hasNext()){
                    uniqueStateStr.append(",");
                }
            }
            stateId = uniqueStateStr.toString();
        }

        public void setBestAction(Action bestAction) {
            this.bestAction = bestAction;
        }


        public State(Vertex agentLocation, Vector<CTPEdge> edgeStatusVector, Double stateProbability) {
            this.agentLocation = agentLocation;
            this.edgeStatuses = edgeStatusVector;
            this.stateProbability = stateProbability;
            setStateId();
        }

        // print states properly
        @Override
        public String toString(){

            return "<"+this.getStateId()+">";
        }

    }

