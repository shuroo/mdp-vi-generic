package mdp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UtilityCalculator {

    Double epsilon = 0.1;
    Double discountFactor = 0.1;

    private MDP currentMDP;

    // --- Parameter for setting during calculation. for private usage: ---
    private Double maxLambda = 100000.0;

    public UtilityCalculator(MDP currentMDP) {
        this.currentMDP = currentMDP;
    }

    public MDP setOptimalPolicy() {

        Integer iterationCounter = 0;
        Double stopCondition = epsilon * (1 - discountFactor) / discountFactor;

        List<Action> allActions = (List<Action>) currentMDP.getActions().values();
        List<State> allStates = (List<State>) currentMDP.getStates().values();

        while (maxLambda >= stopCondition) {

            iterationCounter++;

            setUtilitiesForStatesIteration(allActions, allStates);


            // Check diff to stop...
            for (State state : allStates) {

                Double minimalUtility = state.getUtility();
                Double prevUtility = state.getPreviousUtility();
                //    System.out.println("Utility found for state:"+state.getStateId()+" and bestAction:"+state.getBestAction()+" is: "+state.getUtility());
                Double diffUtility = Math.abs(minimalUtility - prevUtility);
                // max diff per ALL states ... //
                if (maxLambda > diffUtility) {
                    maxLambda = diffUtility;
                }

                if (maxLambda < stopCondition) {

                    System.out.println("Stopping at lambda:" + maxLambda);
                    break;
                }


            }

//            currentMDP.getStates().values().stream().forEach(state -> {
//                System.out.println("**** Final Set for state:" + state.toString());
//            });

        }

        return currentMDP;
    }

    /**
     * Method to return list of actions with updated utility.
     *
     * @param   - states to calc utility from
     * @return updated utilities on actions with sorted list.
     * action_utility <-- 0 if final_state, else Sigma(p(s|s')*U'(s'))
     */
    private List<Action> setActionsUtility(List<Action> allActions) {

        // Init & Build Map<ActionId,State>
        HashMap<Action, List<State>> actionSourcesStMap = getSourceStatesByAction();
        HashMap<Action, List<State>> actionDestinationsMap = getDestStatesByAction();

        for (Action action : allActions) {

            // Stage 1: create map<Action,List<State>> to  find all states belonging to an action.

            List<State> actionSourceStates = actionSourcesStMap.get(action);
            List<State> actionDestStates = actionDestinationsMap.get(action);
            Double actionLocalUtility = 0.0;
            // Business Logic:
            for (State sourceState : actionSourceStates) {
                for (State destState : actionDestStates) {
                    actionLocalUtility += calcStatesUtility(sourceState, destState,action);
                }
            }
            action.setUtility(actionLocalUtility);
        }

        allActions.sort(Action::compareTo);

        return allActions;
    }

    // U(s) <- R(s) + Sigma[ P(s|s')*U(s') ]
    private Double calcStatesUtility(State source, State dest, Action action) {

        if (source.getIsFinal()) {
            return 0.0;
        } else {

            // get P(s,s',a)
            Transition transition = currentMDP.getTransitions().get(Transition.buildId(action
            ,source,dest));

            // The probability for transition between the above states
            Double joinedProb = transition.getProbability();

            // Utility for the two states to add to the action.
            // U(action) <-- Sigma[ P(s|s')*U(s') ]
            Double actionSubUtility = joinedProb * (dest.getUtility());
            // we DON'T set the source utility at this point YET! choosing minimum.
            return actionSubUtility;
        }
    }

    /**
     * Method to set utility per iteration for all states.
     *
     * @param bestActions
     * @return
     */
    private List<State> setUtilitiesForStatesIteration(List<Action> bestActions, List<State> allStates) {
        List<Action> updatedActionsUtility = setActionsUtility(bestActions);

        for (State state : allStates) {
            setUtilitySingleState(state, updatedActionsUtility);
        }

        return allStates;
    }

    private void setUtilitySingleState(State state, List<Action> actionsWithUtility) {

        //  Get all actions belonging to this state:
        List<Action> stateActionsWithUtility = getStateActions(state,actionsWithUtility);

        //  allActions.stream().filter(action -> action.getId().equals(state.getAgentLocation().getId())).collect(Collectors.toSet());
        Action minimalUtilityAction = null;
        Double minimalUtility = 0.0;

        if (!stateActionsWithUtility.isEmpty()) {
            Integer actionIndex = stateActionsWithUtility.size() - 1;
            minimalUtilityAction = findMinimalAction(state, actionIndex,
                    stateActionsWithUtility);

            // U(s) <- R(s,a,(s'??)) + Utility(a)

            // todo: avoid using the same state twice like in example 17.2

            Double reward = minimalUtilityAction != null ?  currentMDP.getRewards().get(Reward.buildId(state,state,minimalUtilityAction)).getReward() : null;
            minimalUtility = minimalUtilityAction != null ? (reward + minimalUtilityAction.getUtility()) : 0.0;
        }

        state.setPreviousUtility(state.getUtility());
        //minimalUtility = CollectionUtils.roundTwoDigits(minimalUtility);
        state.setUtility(minimalUtility);
        state.setBestAction(minimalUtilityAction);
    }


    //todo: do we need to add functionality to check if the action is unblocked \ possible ???

    private Action findMinimalAction(State state, Integer actionIndex, List<Action> stateActionsFiltered) {
        Action currentAction = (Action) stateActionsFiltered.toArray()[actionIndex];
        while (actionIndex > -1 && actionIsImpossible(state,currentAction)) {
            actionIndex--;
            currentAction = (Action) stateActionsFiltered.toArray()[actionIndex];
        }
        // IF found no possible valid action due to action blockings...
        if (actionIndex == -1) {
            System.out.println("Found no valid action due to action blockings - returning null! For state:" + state.getId());
            return null;
        }
        return currentAction;
    }


    // TBD: override this and implement by constraints in the future (Blocked edge etc..)
    private Boolean actionIsImpossible(State st, Action action){
        return false;
    }

    private HashMap<Action, List<State>> getSourceStatesByAction() {
        HashMap<Action, List<State>> sourceStates = new HashMap<Action, List<State>>();

        for (Action action : currentMDP.getActions().values()) {
            sourceStates.put(action, new LinkedList<State>());
        }
        for (Transition transition : currentMDP.getTransitions().values()) {
            sourceStates.get(transition.getAction()).add(transition.getSourceState());
        }
        return sourceStates;
    }

    private HashMap<Action, List<State>> getDestStatesByAction() {
        HashMap<Action, List<State>> destStates = new HashMap<Action, List<State>>();

        for (Action action : currentMDP.getActions().values()) {
            destStates.put(action, new LinkedList<State>());
        }
        for (Transition transition : currentMDP.getTransitions().values()) {
            destStates.get(transition.getAction()).add(transition.getSourceState());
        }
        return destStates;
    }

    private List<Action> getStateActions(final State state,List<Action> actionsWithUtility) {
        Set<Transition> stateTransitions =
                (Set<Transition>) currentMDP.getTransitions().values().stream().filter(tran -> tran.getSourceState() == state).collect(Collectors.toSet());

        Set<Action> stateAllActions = stateTransitions.stream().map(transition->transition.getAction()).collect(Collectors.toSet());

        List<Action> stateActionsWithUtility = actionsWithUtility.stream().filter(action->stateAllActions.contains(action)).collect(Collectors.toList());

        stateActionsWithUtility.sort(Action::compareTo);
        return stateActionsWithUtility;
    }

    public static void main(String[] args){
        System.out.println("--");
    }
}
