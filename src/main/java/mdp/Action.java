package mdp;

/**
 *
    The actions are related t the states in a 1:* relation.
 *  Every state s has a probability P(s|s') to move to state s' with action a.
 */

public class Action implements Comparable<Action>{

    private String actionId;

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

    public Action(String actionId){
        this.actionId = actionId;
    }


    /**
     * Sort by utility DESC.
     *
     * @param action
     * @return
     */
    @Override
    public int compareTo(Action action) {
        if (getUtility() == null || action.getUtility() == null) {
            return 0;
        }
        return action.getUtility().compareTo(getUtility());
    }
}
