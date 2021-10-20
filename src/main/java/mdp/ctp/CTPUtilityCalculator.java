package mdp.ctp;

import mdp.generic.MDP;
import mdp.generic.Transition;
import mdp.generic.UtilityCalculator;

import java.util.HashMap;

public class CTPUtilityCalculator extends UtilityCalculator {

    protected MDPFromGraph extendedMDP;

    public CTPUtilityCalculator(MDPFromGraph currentMDP, Double epsilon, Double discountFactor) {

        this.currentMDP = (MDP)currentMDP;
        this.extendedMDP = currentMDP;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
    }

    @Override
    protected Double calcStatesUtility(Transition transition) {
//        if ( transition.isValid()) {
//            System.out.println("*** The following transition was found VALID:: "+transition.getTransitionId()+" ***");
//        }

        return super.calcStatesUtility(transition);
    }
    @Override
    protected HashMap<mdp.generic.Transition, Double> calcTransitionsUtility() {



        // Init & Build Map<Transition,Utility>

        HashMap<mdp.generic.Transition, Double> actionsPerSourceStt = new HashMap<mdp.generic.Transition, Double>();

        for (Transition transition : extendedMDP.getExtededTransitions().values()) {

            Double actionLocalUtility = calcStatesUtility(transition);
            if (!actionsPerSourceStt.containsKey(transition)) {
                actionsPerSourceStt.put(transition, actionLocalUtility);
            } else {
                Double prevUtil = actionsPerSourceStt.get(transition);
                actionsPerSourceStt.put(transition, prevUtil + actionLocalUtility);
            }
        }

        return actionsPerSourceStt;
    }
}
