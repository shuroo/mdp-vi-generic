package mdp.ctp;

import mdp.generic.MDP;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UtilityCalculator extends mdp.generic.UtilityCalculator {

    public UtilityCalculator(MDPFromGraph extendedMDP, Double epsilon, Double discountFactor) {

        this.graphMDP = extendedMDP;
        this.currentMDP = (MDP)extendedMDP;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
    }


    /**
     * Method to return list of transitions with utility for each ( for grouping them later by action ).
     *
     * @param - states to calc utility from
     * @return updated utilities on actions with sorted list.
     * action_utility <-- 0 if final_state, else Sigma(p(s|s')*U'(s'))
     * .
     * EXAMPLE CALC: stt2_3->stt_3_3 = 0.8*left_action_utility + 0.1 * right_action_utility + 0.1 * stay =  0.8 * 0.76 + 0.2 * -0.04
     */

    /** Dynamically generate action related transitions... **/

    @Override
    public void setActionUtility() {

        HashMap<String, Action> allActions = graphMDP.getExtededActions();
        HashMap<String,mdp.ctp.State> allStates = graphMDP.getExtededStates();

        for(mdp.ctp.Action action : allActions.values()){
            List<mdp.generic.Transition> actionTransitions = MDPCreator.fetchActionRelatedTransitions(action,allStates).stream()
                    .map(tran->(mdp.generic.Transition)tran).collect(Collectors.toList());
            calcUtilityPerTransition(actionTransitions);
            Double actionUtility = 0.0;
            for(mdp.generic.Transition tran : actionTransitions){
                actionUtility += tran.getUtility();
            }
            action.setUtility(actionUtility);
        }
    }

}
