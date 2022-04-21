package mdp;

import mdp.generic.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class UtilityCalculator {

    private Double epsilon;
    private Double discountFactor;

    private MDP currentMDP;

    // --- Parameter for setting during calculation. for private usage: ---
    private Double maxLambda = 100000.0;

    public UtilityCalculator(MDP currentMDP, Double epsilon, Double discountFactor) {

        this.currentMDP = currentMDP;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
    }

    public MDP setOptimalPolicy() {

        Integer iterationCounter = 0;
        Double stopCondition = epsilon * (1 - discountFactor) / discountFactor;

        List<State> allStates = currentMDP.getStates().values().stream().collect(Collectors.toList());

        while (maxLambda > stopCondition) {

            iterationCounter++;
            System.out.println("Starting iteration number:" + iterationCounter);

            setUtilitiesForStatesIteration(allStates);

            // Check diff to stop...
            for (State state : allStates) {

                Double minimalUtility = state.getUtility();
                Double prevUtility = state.getPreviousUtility();
                //System.out.println("Utility found for state:"+state.getId()+" and bestAction:"+state.getBestAction()+" is: "+state.getUtility());
                if (prevUtility == null) {
                    continue;
                }
                Double diffUtility = Math.abs(minimalUtility - prevUtility);
                // max diff per ALL states ... //
                if (maxLambda > diffUtility && diffUtility > 0.0) {
                    maxLambda = diffUtility;
                }

                if (maxLambda <= stopCondition){//  && maxLambda != 0.0) {

                    System.out.println("***** Stopping at lambda:" + maxLambda + " on iteration:" + iterationCounter + " *****");
                    return currentMDP;
                }
            }

            currentMDP.getStates().values().stream().forEach(state -> {
                System.out.println("**** Final Utility state:" + state.getId() + " is:" + state.getUtility() + " chosen action is: " + state.getBestAction());
            });

            // currentMDP.setStates(allStates);
        }

        return currentMDP;
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

    public HashMap<String, Action> groupByActionAndSourceState(HashMap<Transition, Double> transtionsWithUtility) {

        // Structure: <String , Double>
        HashMap<String, List<Action>> actionsWithUtility = new HashMap<String, List<Action>>();

        for (Transition tran : transtionsWithUtility.keySet()) {
            String transitionId = tran.getTransitionId();
            String sourceActionKey = tran.getAction().toString()+"_"+transitionId.substring(transitionId.indexOf("_src:"),
                    transitionId.length());
            Double utility = transtionsWithUtility.get(tran);

            Action action = new Action(tran.getAction().getActionId());

            if(!actionsWithUtility.containsKey(sourceActionKey)) {
                actionsWithUtility.put(sourceActionKey, new LinkedList<Action>());
            }

            //System.out.println("Setting utility:"+utility+" for action:"+action.getActionId()+" on key:"+sourceActionKey);
            action.setUtility(utility);
            actionsWithUtility.get(sourceActionKey).add(action);

        }

        HashMap<String, Action> actionsWithGroupedUtility = new HashMap<String, Action>();

        for(String sourceActionId : actionsWithUtility.keySet()){
            List<Action> relatedActions = actionsWithUtility.get(sourceActionId);
            Double accUtility = 0.0;
            if(relatedActions.isEmpty()){
                continue;
            }
            Action sampleAction = relatedActions.get(0);
            for(Action action : relatedActions){

                accUtility+=action.getUtility();
            }

            sampleAction.setUtility(accUtility);
            actionsWithGroupedUtility.put(sourceActionId,sampleAction);
        }

        return actionsWithGroupedUtility;
    }

    /**
     * Method to return list of transitions with utility for each ( for grouping them later by action ).
     *
     * @param - states to calc utility from
     * @return updated utilities on actions with sorted list.
     * action_utility <-- 0 if final_state, else Sigma(p(s|s')*U'(s'))
     * .
     * EXAMPLE CALC: stt2_3->stt_3_3 = 0.8*left_action_utility + 0.1 * right_action_utility + 0.1 * stay =  0.8 * 0.76 + 0.2 * -0.04
     * <p>
     * This method returns a hashmap of:
     * <p>
     * stt_3_3_left_stt_3_1 <- 0.8 *(left_action_utility)
     * stt_3_3_left_stt_3_1 <- 0.1 *(right_action_utility)
     * <p>
     * etc...
     */
    private HashMap<Transition, Double> calcTransitionsUtility() {

        // Init & Build Map<Transition,Utility>

        HashMap<Transition, Double> actionsPerSourceStt = new HashMap<Transition, Double>();

        for (Transition transition : currentMDP.getTransitions().values()) {
            Double actionLocalUtility = calcStatesUtility(transition.getSourceState(), transition.getDestState(), transition.getAction());
            if (!actionsPerSourceStt.containsKey(transition)) {
                actionsPerSourceStt.put(transition, actionLocalUtility);
            } else {
                Double prevUtil = actionsPerSourceStt.get(transition);
                actionsPerSourceStt.put(transition, prevUtil + actionLocalUtility);
            }
            //   actionsPerSourceStt.put(transition, actionLocalUtility);
        }

        return actionsPerSourceStt;
    }

    private HashMap<Transition, Double> calcTransitionsUtilityOld() {

        // Init & Build Map<Transition,Utility>

        HashMap<Transition, Double> actionsPerSourceStt = new HashMap<Transition, Double>();

        for (Transition transition : currentMDP.getTransitions().values()) {
            Double actionLocalUtility = calcStatesUtility(transition.getSourceState(),transition.getDestState(),transition.getAction());
            if (!actionsPerSourceStt.containsKey(transition)) {
                actionsPerSourceStt.put(transition, actionLocalUtility);
            } else {
                Double prevUtil = actionsPerSourceStt.get(transition);
                actionsPerSourceStt.put(transition, prevUtil + actionLocalUtility);
            }
            //   actionsPerSourceStt.put(transition, actionLocalUtility);
        }

        return actionsPerSourceStt;
    }

    // U(s) <- R(s) + Sigma[ P(s|s')*U(s') ]
    private Double calcStatesUtility(State source, State dest, Action action) {

        if (source.getIsFinal()) {
            return source.getUtility() == null ? 0.0 : source.getUtility();
        } else {

            // get P(s,s',a)
            Transition transition = currentMDP.getTransitions().get(Transition.buildId(action
                    , source, dest));

            // The probability for transition between the above states
            Double joinedProb = transition != null ? transition.getProbability() : 0.0;

            // Utility for the two states to add to the action.
            // U(action) <-- Sigma[ P(s|s')*U(s') ]
            Double actionSubUtility = joinedProb * (dest.getUtility());
            // we DON'T set the source utility at this point YET! choosing minimum.

            //System.out.println("transition:"+transition.getTransitionId()+"----^^:" + joinedProb + " actionSubUtility:" + actionSubUtility);
            return actionSubUtility;
        }
    }

    /**
     * Method to set utility per iteration for all states.
     *
     * @param allStates - all possible states
     * @return
     */
    private List<State> setUtilitiesForStatesIteration(List<State> allStates) {
        HashMap<Transition, Double> updatedTransitionsUtility = calcTransitionsUtility();

        HashMap<String, Action> utilityPerActionState = groupByActionAndSourceState(updatedTransitionsUtility);

        for (State state : allStates) {
            setUtilitySingleState(state, utilityPerActionState);
        }

        return allStates;
    }

    private void setUtilitySingleState(State state, HashMap<String, Action> updatedStateActionsUtility) {

        //  Get all actions belonging to this state:
        HashMap<String, Action> actionsWithUtility = filterStateActions(state, updatedStateActionsUtility);

       // System.out.println("State: "+state.getId()+" has actions with utility of size:"+actionsWithUtility.size());
        Action minimalUtilityAction = null;
        Double minimalUtility = 0.0;

        if (!actionsWithUtility.isEmpty()) {
            minimalUtilityAction = currentMDP.isMinimizationProblem() ? findMinimalAction(
                    actionsWithUtility) : findMaximalAction(actionsWithUtility);

            // U(s) <- R(s,a,(s'??)) + Utility(a)

            // todo: avoid using the same state twice like in example 17.2

            String currentRewardId = Reward.buildId(
                    state, state, minimalUtilityAction);

            Double reward = minimalUtilityAction != null ? ( currentMDP.getRewards().get(currentRewardId) != null ?
                    currentMDP.getRewards().get(currentRewardId).getReward() : 0.0) : 0.0;
            minimalUtility = minimalUtilityAction != null ? (reward + minimalUtilityAction.getUtility()) : 0.0;

            minimalUtility = minimalUtility * this.discountFactor;

            state.setPreviousUtility(state.getUtility());
            //minimalUtility = CollectionUtils.roundTwoDigits(minimalUtility);

            //System.out.println("--### Setting utility: " + minimalUtility + " for state: " + state.getId() + "###--");
            state.setUtility(minimalUtility);
            state.setBestAction(minimalUtilityAction);

        }


    }


    //todo: do we need to add functionality to check if the action is unblocked \ possible ???


    private Action findMinimalAction(HashMap<String, Action> sttActionsWithutility) {

        Double finalUtility = 10000.0;
        Action result = null;
        for (String actionStateKey : sttActionsWithutility.keySet()) {
            Action currentAction = sttActionsWithutility.get(actionStateKey);
            Double currentUtility = currentAction.getUtility();
            if (currentUtility <= finalUtility) {
                result = currentAction;
                finalUtility = currentUtility;
            }
        }
        return result;
    }

    private Action findMaximalAction(HashMap<String, Action> sttActionsWithUtility) {

        Double finalUtility = -10000.0;
        Action result = null;
        for (String actionStateKey : sttActionsWithUtility.keySet()) {
            Action currentAction = sttActionsWithUtility.get(actionStateKey);
            Double currentUtility = currentAction.getUtility();
            if (currentUtility > finalUtility) {
                result = currentAction;
                finalUtility = currentUtility;
            }
        }

        return result;
    }

    // TBD: override this and implement by constraints in the future (Blocked edge etc..)
    private Boolean actionIsImpossible(State st, Action action) {
        return false;
    }

    /**
     * Given a state, filter all given actions related to it with updated calculated utility..
     *
     * @param state - the state to filter
     * @return HashMap<String, Double>
     **/

    private HashMap<String, Action> filterStateActions(final State state, HashMap<String, Action> stateActionsWithUtility) {

        //todo: one can add filter for unneeded transitions de to constraints in the future.
        HashMap<String, Action> stateActions = new HashMap<String, Action>();

        // check whether 'key' = action.getId()_state.getId(), and filter accordingly.
        stateActionsWithUtility.entrySet().stream().filter(tran -> tran.getKey().endsWith("_src:" +state.getId())).forEach(entry ->
                stateActions.put(entry.getKey(), entry.getValue()));

        // todo: add sort --- IF NEEDED! (currently it isn't , just use min or max)
        //stateTransitions.values();
        return stateActions;
    }

}
