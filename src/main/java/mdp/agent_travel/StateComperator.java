package mdp.agent_travel;

import mdp.ctp.State;

import java.util.Comparator;

class StateComperator implements Comparator<State> {
    // Used for sorting in ascending order of
    // roll number

    public int compare(State a, State b)
    {
        return  a.getUtility() > b.getUtility()?1:-1;
    }

}
