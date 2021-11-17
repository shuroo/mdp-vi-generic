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

        return super.calcStatesUtility(transition);
    }

}
