package mdp.generic;

import ctp.CTPEdge;
import mdp.interfaces.StateI;
import org.jgrapht.graph.Vertex;

import java.util.HashMap;
import java.util.List;

public class State implements StateI {

    protected String stateId;
    // Default value
    // FOR MINIMIZATION PROBLEMS ONLY!!
    // highly important - fetch zero for final states.
    protected Double minimalUtility = null;
    protected Action bestAction;

    public List<Action> getBestActions() {
        return bestActions;
    }

    public void setBestActions(List<Action> bestActions) {
        this.bestActions = bestActions;
    }

    protected List<Action> bestActions;
    protected Boolean isFinal = null;
    protected Boolean isInitial = null;
    protected Double previousUtility = 10000.0;

    // Extended properties for graphs only (not generic states)!

    public Vertex getAgentLocation() {
        return agentLocation;
    }

    protected Vertex agentLocation;
    protected HashMap<String, CTPEdge> statuses;

    public State(String stateId, Boolean isInitial, Boolean isFinal,Double initialUtility) {
        this.stateId = stateId;
        this.isInitial = isInitial;
        this.isFinal = isFinal;
        this.minimalUtility = initialUtility;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setFinal(Boolean aFinal) {
        isFinal = aFinal;
    }

    public Boolean getInitial() {
        return isInitial;
    }

    public void setInitial(Boolean initial) {
        isInitial = initial;
    }

    public Double getPreviousUtility() {
        return previousUtility;
    }


    public State(String stateId, Double initialUtility) {
        this.stateId = stateId;
        this.minimalUtility = initialUtility;
    }

    public String getId() {
        return stateId;
    }

    public Double getUtility() {
        return minimalUtility;
    }

    public void setUtility(Double utility) {
        this.minimalUtility = utility;
    }

    public Action getBestAction() {
        return bestAction;
    }

    public void setBestAction(Action action) {
        this.bestAction = action;
    }

    public void setPreviousUtility(Double prevUtil) {
        this.previousUtility = prevUtil;
    }

    public State()  {
        try {
            throw new Exception("using default state constructor - aborting");
        }catch(Exception e){

        }
    }


    // print states properly
    @Override
    public String toString(){

        return "<"+this.stateId+">< utility:"+this.getUtility()+" >";
    }
}
