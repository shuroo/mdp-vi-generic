package mdp;

import mdp.interfaces.StateI;

public class State implements StateI {

    private String stateId;
    private Double minimalUtility;
    private Action bestAction;
    private Boolean isFinal = false;
    private Boolean isInitial = false;
    private Double previousUtility;

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
}
