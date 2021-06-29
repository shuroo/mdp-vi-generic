package mdp.generic;

import ctp.CTPEdge;
import mdp.interfaces.StateI;
import org.jgrapht.graph.Vertex;

import java.util.HashMap;
import java.util.List;

public class State implements StateI {

    protected String stateId;
    // Default value
    protected Double minimalUtility = 0.0;
    protected Action bestAction;

    public List<Action> getBestActions() {
        return bestActions;
    }

    public void setBestActions(List<Action> bestActions) {
        this.bestActions = bestActions;
    }

    protected List<Action> bestActions;
    protected Boolean isFinal = false;
    protected Boolean isInitial = false;
    protected Double previousUtility = 0.0;

    // Extended properties for graphs only (not generic states)!

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

    public State(){}


    // print states properly
    @Override
    public String toString(){

        return "<"+this.stateId+">< utility:"+this.getUtility()+" >";
    }
}
