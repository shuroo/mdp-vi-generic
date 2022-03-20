package mdp.generic;

import ctp.BlockingStatus;
import ctp.CTPEdge;

import java.util.HashMap;

/**
 * The actions are related t the states in a 1:* relation.
 * Every state s has a probability P(s|s') to move to state s' with action a.
 */

public class Action {

    protected String actionId;

    public boolean isReversed() {
        return isReversed;
    }

    protected boolean isReversed = false;
    protected String unreversedID;

    public Double getUtility() {
        return utility;
    }

    public void setUtility(Double utility) {
        this.utility = utility;
    }

    private Double utility;

    public String getActionId() {
        return actionId;
    }

    public Action(String actionId) {
        this.actionId = actionId;
        this.unreversedID = actionId;
    }

    public Action(String actionId, Boolean isReversed,String unreversedID) {
        this.actionId = actionId;
        this.unreversedID = unreversedID;
        this.isReversed = isReversed;
    }

    @Override
    public String toString() {
        return unreversedID;
    }

    public Action() {
    }

    // Make sure the action's edge is in status 'OPEN' (Blocked edge etc..)
    public Boolean actionEdgeIsTraversive(final State st) {

        if (!(st instanceof mdp.ctp.State)) {
            return true;
        }
        HashMap<String, CTPEdge> stateStatuses = st.getStatuses();
        if(this.isReversed){
            return ((CTPEdge) stateStatuses.get(this.unreversedID)).getStatus() == BlockingStatus.O;
        }
        return ((CTPEdge) stateStatuses.get(this.getActionId())).getStatus() == BlockingStatus.O;
    }

}
