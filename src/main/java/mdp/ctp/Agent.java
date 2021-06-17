package mdp.ctp;

package org.ctp.CTPAgent;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Vertex;

import java.util.*;

public class Agent implements Runnable {

    private Double agentPathCost;
    LinkedList<LinkedList<State>> agentPaths = new LinkedList<LinkedList<State>>();
    MDPFromGraph mdp;
    HashMap<String,CTPEdge> graphConfiguration;


    public Agent(MDPFromGraph mdp,  HashMap<String,CTPEdge>  graphConfiguration) {

        this.graphConfiguration = graphConfiguration;
        this.mdp = mdp;
        this.agentPathCost = 0.0;

    }

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

    private  HashMap<String,CTPEdge> setUnknownStatuses(Vertex agentLocation, HashMap<String,CTPEdge> graphConf ){
        HashMap<String,CTPEdge> statuses = new  HashMap<String,CTPEdge>();
        statuses.putAll(graphConf);
        statuses.entrySet().forEach(statusedEdge->{
            if(statusedEdge.getValue().getEdge().getSource() == agentLocation){
                if(statusedEdge.getValue().getStatus() == BlockingStatus.Closed){
                    throw new Exception("Edge is closed!");
                }}
                else if(statusedEdge.getValue().getStatus() == BlockingStatus.Unknown){
                    statuses.put(statusedEdge.getKey(),new CTPEdge(statusedEdge.getValue().getEdge(),BlockingStatus.Opened));
                }
            });
        });

    }

    /**
     * find initial state, run, return a valid path when reached a final state.
     */
    public void run() {
        State initial = findInitialStt();


    }

    public List<State> runOnPath(State current, List<State> previousStates) {
        if(current.getAgentLocation().isFinal()) {
            previousStates.add(current);
            return previousStates;
        }


        // For the current state:
        // - Take best action

        mdp.ctp.Action action = mdp.extededActions.get(current.getBestAction());
        // - Update agent position
        Vertex nextV = action.getDest();
        String newSttId = State.buildId(nextV, current.getStatuses());
        State nextState = mdp.extededStates.get(newSttId);
        // todo: add here !!! Fix Unknown statuses +
        // todo: newStt.setStatuses(current);
        /// ******************* !!! **********************
        if( stateIsValid(current,nextState)) {
            previousStates.add(nextState);
            runOnPath(nextState, previousStates);
        }

        // If the new state is not valid, then the dest is blocked, or already visited.
        // need to sort states by utility asc -  to find an alternative.
        // todo here: return the first unvisited states with minimal utility, and try again
        // else - goto parent vertex and try again.

        State nextState = sortStatesByUtility()



        // - Construct next state
        // Update known statuses


        // - Add current state to the list of states
        // - continue... Until reached the end.
        // - If path is blocked: recalc strategy for subgraph ( - find new best path);
        // -- run from there.
        // - until done.
        // - When done, run Dror's graphs.
    }
/*
        /// todo: put the state list as a path property?
        ArrayList<StateList> agentPath = travelGraph( initial,0, model.getGraphPaths(),0,new ArrayList<StateList>(),
                new StateList("First Attempt for stateList",gs.getGraph()));
        // todo: build function out of this:
        System.out.println("|| The agent total path was:"+agentPath+" ||");
        System.out.println("|| Possible paths and costs are:"+model.getGraphPaths() + " ||");
    }


*/


    // Check if the next state is at all possible - is the edge blocked?

    /**
     * State is valid:
     *
     *  - Not edge is blocked;
     *  - Not yet visited;
     *
     * @param prev
     * @param current
     * @return
     */
    private boolean stateIsValid(State prev,State current) {

        Vertex source = prev.getAgentLocation();
        Vertex dest = current.getAgentLocation();
        BlockingStatus edgeStatus = graphConfiguration.get(Edge.buildId(source,dest)).getStatus();
        if( edgeStatus != BlockingStatus.Opened ) {
            return false;
        }
        return true;

        // todo: make sur 'unknown' is not valid!
    }



}

