package mdp.ctp;

package org.ctp.CTPAgent;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

public class Agent implements Runnable {

    private Double agentPathCost;
    MDPFromGraph mdp;
    HashMap<String,CTPEdge> graphConfiguration;

    /**
     *  Find state to start with:
     *  - make sure it has an initial agent location
     *  - make sure its configuration is valid.
     *  - make sure it has a bestAction configured (else return null and abort)
     * @return
     */


    private State findInitialStt(){
        HashMap<String,State> states = mdp.extededStates;
        State result = null;

        for(State stt: states.values()){
            if(stt.getBestAction() != null && stt.getAgentLocation().isInitial() && stt.getEdgeStatus() == BlockingStatus.Opened &&
                    graphConfiguration.get(stt.getBestAction().getActionId()).getStatus()  == BlockingStatus.Opened
            ){
                result = new State(stt.getAgentLocation(),new Vector(graphConfiguration.values()));
                break;
            }
        }
        return result;

    }
    public Agent(MDPFromGraph mdp,  HashMap<String,CTPEdge>  graphConfiguration) {

        this.graphConfiguration = graphConfiguration;
        this.mdp = mdp;
        this.agentPathCost = 0.0;

    }

    /**
     * find initial state, run, return a valid path when reached a final state.
     */
    public void run() {
        State initial = findInitialStt();

        /// todo: put the state list as a path property?
        ArrayList<StateList> agentPath = travelGraph( initial,0, model.getGraphPaths(),0,new ArrayList<StateList>(),
                new StateList("First Attempt for stateList",gs.getGraph()));
        // todo: build function out of this:
        System.out.println("|| The agent total path was:"+agentPath+" ||");
        System.out.println("|| Possible paths and costs are:"+model.getGraphPaths() + " ||");
    }

    // Travel one step in the graph each time. try best cost. if fail, return.
    //todo: reorder params.
    public ArrayList<State> travelGraph( mdp.ctp.State current) {

        // For the current state:
            // - Take best action

            mdp.ctp.Action action = mdp.extededActions.get(current.getBestAction());
            Vertex nextV = action.getDest();
            String newSttId = State.buildId(nextV, current.getStatuses());
            State newStt = mdp.extededStates.get(newSttId);

            // - Update agent position +
            // Fix Unknown statuses +
        // - Construct next state
        // Update known statuses

        // todo: newStt.setStatuses(current);
        
        // - Add current state to the list of states
        // - continue... Until reached the end.
        // - If path is blocked: recalc strategy for subgraph ( - find new best path);
        // -- run from there.
        // - until done.
        // - When done, run Dror's graphs.
    }


}

