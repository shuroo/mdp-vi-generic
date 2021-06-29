package mdp.action_sorters;

import mdp.generic.Action;

import java.util.Comparator;

public class ActionSortDesc implements Comparator<Action> {


    // Used for sorting in ascending order of
    // roll number
//        public int compare(Action a, Action b)
//        {
//            return  a.getPathCost() > b.getPathCost()?1:-1;
//        }

    public int compare(Action a, Action b) {
        if (a.getUtility() == null || b.getUtility() == null) {
            return 0;
        }
        return b.getUtility().compareTo(a.getUtility());
    }
}

