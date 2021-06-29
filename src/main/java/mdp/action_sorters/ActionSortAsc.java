package mdp.action_sorters;

import mdp.generic.Action;

import java.util.Comparator;

public class ActionSortAsc implements Comparator<Action> {

    public int compare(Action a, Action b) {
        if (a.getUtility() == null || b.getUtility() == null) {
            return 0;
        }
        return a.getUtility().compareTo(b.getUtility());
    }
}


