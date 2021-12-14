package mdp.ctp;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Vertex;
import utils.CollectionUtils;
import utils.Constants;

import java.util.*;

public class State extends mdp.generic.State implements Comparable<State> {

    // Visited - and not let to a solution - hence the state is invalid.
    private Integer agentVisits = 0;

    public Double getMinimalUtility() {
        return minimalUtility;
    }


    // The index to approach the current best action

    private State parentState;

    public void setAgentVisits() {
        this.agentVisits++;
    }

    public Integer getAgentVisits() {
        return agentVisits;
    }

    /**
     * Set the next action and return it , when exists:
     * @return
     */
    public State setNextBestAction(){

        // Check whether the agent has no actions not yet visited:
        if(allBestStateActionsAreTried()){
            return null;
        }


        return new State(this.getAgentLocation(),this.getStatuses().values(),this.minimalUtility,
                bestActions, agentVisits);

    }


    /**
     * Copy constructor - and more:
     * @param location
     * @param statuses
     */
    public State(Vertex location, Collection<CTPEdge> statuses,Double utility,
                 List<mdp.generic.Action> bestActions, Integer actionsIndex){
       // super(buildId(location,statuses), Boolean isInitial, Boolean isFinal,Double initialUtility);

        this.agentLocation = location;
        this.statuses = CollectionUtils.edgeToMap(statuses);
        this.minimalUtility = utility;
        this.agentVisits = actionsIndex;
        this.bestActions = bestActions;
        this.bestAction = bestActions.get(actionsIndex);
        setStateId();

    }

    /**
     * Copy constructor
     * @param stToCpy
     */
    public State(State stToCpy){

        this.agentLocation = stToCpy.agentLocation;
        this.statuses = stToCpy.statuses;
        this.minimalUtility = stToCpy.minimalUtility;
        this.agentVisits = stToCpy.agentVisits;
        this.bestActions = stToCpy.bestActions;
        this.bestAction = stToCpy.bestAction;
        this.stateId = stToCpy.stateId;

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
    public Boolean allBestStateActionsAreTried() {
        return bestActions==null || agentVisits >= bestActions.size();
    }

    public State(Vertex agentLocation, Vector<CTPEdge> statuses) {
        /// THIS IS THE CONSTRUCTOR USED IN OUR EXAMPLE!!!
        this.agentLocation = agentLocation;
        this.statuses = CollectionUtils.edgeToMap(statuses);
        this.isFinal = agentLocation.isFinal();
        this.isInitial = agentLocation.isInitial();
        this.setUtility((this.isFinal ? 0.0 : 10000.0));
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
        uniqueStateStr.append(Constants.statesPrefix +agLoc);

         CollectionUtils cu = new CollectionUtils();
        cu.sortMapbykeys(statuses);

        statuses.values().stream().sorted().forEach(status->{
            uniqueStateStr.append(status.toString());
            uniqueStateStr.append(",");
        });

        return uniqueStateStr.toString();
    }

    // print states properly

    @Override
    public int compareTo(State state) {
        return  this.getUtility() > state.getUtility()?1:-1;
    }

    @Override
    public Boolean isValid(){
        // A state is not valid if there exists one of its statuses in status 'unknown'
        // and the same agent location as in the state.
        for (CTPEdge someStatus : this.getStatuses().values()) {

            if (someStatus.getEdge().getSource() == this.getAgentLocation() &&
                    someStatus.getStatus() == BlockingStatus.U) {
                return false;
            }
        }

        return true;
    }
}

