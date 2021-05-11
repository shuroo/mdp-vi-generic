package mdp.interfaces;

import mdp.Action;

public interface StateI {


    // Override by implementation
    Double utility = 0.0;

    // todo: override by implementation
    //String state_id;

    public String getId();

    public Double getUtility();

    public void setUtility(Double utility);

    public Action getBestAction();

    public  void setBestAction(Action action);

    public void setPreviousUtility(Double utility);

    public Double getPreviousUtility();

}
