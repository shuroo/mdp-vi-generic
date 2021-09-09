package mdp.generic;

import mdp.action_sorters.ActionSortAsc;
import mdp.action_sorters.ActionSortDesc;
import mdp.ctp.MDPCreator;
import mdp.ctp.MDPFromGraph;
import utils.Constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class UtilityCalculator {

    protected Double epsilon;
    protected Double discountFactor;
    protected MDPFromGraph graphMDP;
    protected MDP currentMDP;

    // --- Parameter for setting during calculation. for private usage: ---
    private Double maxLambda = 100000.0;

    protected UtilityCalculator(){}

    public UtilityCalculator(MDP mdp, Double epsilon, Double discountFactor) {

        this.currentMDP = mdp;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
    }

    public MDP setOptimalPolicy() {

        Integer iterationCounter = 0;
        Double stopCondition = epsilon * (1 - discountFactor) / discountFactor;

        while (maxLambda > stopCondition && iterationCounter < 2) {

            HashMap<String, State> allStates = currentMDP.getStates();

            iterationCounter++;
            System.out.println("Starting iteration number:" + iterationCounter + " with lambda:" + maxLambda);

            setUtilitiesForStatesIteration(allStates);

            //*** Stop condition Version 1 - "Normal"  stop condition:*** //
            //todo: ** Notice that, stopCond != 0 since, if stopCond == 0 then, it could belong to a previous converge step.
            // Check diff to stop...new Reward("mockId","mockId",action, reward);

            //*** Stop condition Version 2 -  stop condition by the number of states converged upon a single iteration :*** //

            System.out.println("Successfully set utilities for all states in the current iteration.");
            Integer numOfConvertedStates = 0;
            for (State state : allStates.values()) {

                Double minimalUtility = state.getUtility();
                Double prevUtility = state.getPreviousUtility();
                Double diffUtility = Math.abs(minimalUtility - prevUtility);
                if (diffUtility <= stopCondition) {
                    numOfConvertedStates++;
                }
                // max diff per ALL states ... //
                if (maxLambda > diffUtility) {
                    System.out.println("** Setting lambda to :" + diffUtility + "**");
                    maxLambda = diffUtility;
                }


            }

            // When all states finished their current iteration - check lambda:

            if (maxLambda > 0.0 && maxLambda <= stopCondition) {

                System.out.println("***** Stopping at lambda:" + maxLambda + " on iteration:" + iterationCounter + " *****");
                return currentMDP;
            }


            System.out.println("** current number of states converted:" + numOfConvertedStates + " maxLambda:" + maxLambda + ",stopCondition:" + stopCondition);


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
     * <p>
     * This method returns a hashmap of:
     * <p>
     * stt_3_3_left_stt_3_1 <- 0.8 *(left_action_utility)
     * stt_3_3_left_stt_3_1 <- 0.1 *(right_action_utility)
     * <p>
     * etc...
     */
    protected void calcUtilityPerTransition(List<Transition> transitions) {

        // Init & Build Map<Transition,Utility>

        for (Transition transition : transitions) {

            // U(action) <--  Sigma[ P(s|s')*( R(s,s',a) + U(s') )]
            Double actionLocalUtility = calcStatesUtility(transition);
            System.out.println("Set - Transition:"+transition.getTransitionId()+", by Utility:"+(transition.getPrevUtility() + actionLocalUtility));
            transition.updateUtility(transition.getPrevUtility() + actionLocalUtility);
        }
    }

    /*
    protected void setActionUtility() {

        // setting utilities to: currentMDP.getTransitions().values()
        calcUtilityPerTransition((List<Transition>) currentMDP.getTransitions().values());
        // Structure: <String , Double>
        HashMap<String, List<Action>> actionsWithUtility = new HashMap<String, List<Action>>();

        HashMap<String, Action> actionsWithGroupedUtility = new HashMap<String, Action>();


        for (String sourceActionId : actionsWithUtility.keySet()) {
            List<Action> relatedActions = actionsWithUtility.get(sourceActionId);
            Double accUtility = 0.0;
            if (relatedActions.isEmpty()) {
                continue;
            }
            Action sampleAction = relatedActions.get(0);
            Integer numberOfParticipants = 0;
            for (Action action : relatedActions) {
                if (action.getUtility() > 0) {
                    accUtility += action.getUtility();
                    numberOfParticipants++;
                }
            }
           // numberOfParticipants = numberOfParticipants ==0? 1 : numberOfParticipants;
            Double finalUtil = accUtility ;/// numberOfParticipants;
            //System.out.println("Setting utility:"+finalUtil+" for action:"+sampleAction+", originally:"+accUtility+"  participants: "+numberOfParticipants);
            sampleAction.setUtility(finalUtil);
            actionsWithGroupedUtility.put(sourceActionId, sampleAction);
            currentMDP.getActions().get(sampleAction.getActionId()).setUtility(finalUtil);
        }
        // todo: return void...
    }
    */

    public void setActionUtility() {

            HashMap<String, mdp.ctp.Action> allActions = graphMDP.getExtededActions();
            HashMap<String,Transition> allTransitions = currentMDP.getTransitions();

            for(mdp.ctp.Action action : allActions.values()){
            List<Transition> actionTransitions = new LinkedList<Transition>();
            for(Transition tran : allTransitions.values()){
                if(tran.getSourceState().agentLocation == action.getSource()){
                    actionTransitions.add(tran);
                }
            }
            calcUtilityPerTransition(actionTransitions);
            Double actionUtility = 0.0;
            for(Transition tran : actionTransitions){
                actionUtility += tran.getUtility();
            }
            action.setUtility(actionUtility);
            System.out.println("Set utility for - Action:"+action.getActionId()+",Utility:"+actionUtility);

            }
    }



    // U(s) <- R(s) + Sigma[ P(s|s')*U(s') ]

    // OR

    // U(s) <- Sigma[  R(s,s',a) + P(s|s')*U(s') ]
    protected Double calcStatesUtility(Transition transition) {

        if (!transition.isValid()) {
            return 0.0;
        }
        State source = transition.getSourceState();
        // To fetch updated utility, use the stat from the updated hashmap...
        State dest = transition.getDestState();///allStates.get(transition.getDestState().toString());

        Action action = transition.getAction();

        if (source.getIsFinal()) {
            return source.getUtility() == null ? 0.0 : source.getUtility();
        } else {

            // get P(s,s',a)

            // The probability for transition between the above states
            Double joinedProb = transition.getProbability();

            //Double joinedProb =transition != null ? transition.getProbability() : 0.0;

            // Utility for the two states to add to the action.
            // U(action) <--  Sigma[ P(s|s')*( R(s,s',a) + U(s') )]
            // todo: note to change this if using the original rewards matrix instead of the shrinked
            Reward rewardObj = currentMDP.getRewards().get(Reward.buildId("mockId", "mockId", action));
            Double reward = rewardObj != null ? rewardObj.getReward() : 0.0;
            Double actionSubUtility = joinedProb * (reward + dest.getUtility());
            return actionSubUtility;
        }
    }

    /**
     * Method to set utility per iteration for all states.
     *
     * @param allStates - all possible states
     * @return
     */
    private HashMap<String, State> setUtilitiesForStatesIteration(HashMap<String,State> allStates) {

        setActionUtility();

        for (State state : allStates.values()) {
            setUtilitySingleState(state);
        }

        return allStates;
    }

    private void setUtilitySingleState(State state) {

        //  Get all actions belonging to this state:
        HashMap<String, Action> actionsWithUtility = filterStateActions(state, currentMDP.getActions());

        List<Action> minimalUtilityActions = null;
        Action minimalUtilityAction = null;
        Double minimalUtility = 0.0;

        if (!actionsWithUtility.isEmpty()) {
            minimalUtilityActions = currentMDP.isMinimizationProblem() ? sortMinimalActions(
                    actionsWithUtility) : sortMaximalActions(actionsWithUtility);

            // U(s) <- R(s,a,(s'??)) + Utility(a)

            minimalUtilityAction = minimalUtilityActions.isEmpty() ? null : minimalUtilityActions.get(0);

            // Value to compare before putting reward inside the equasion
            minimalUtility = minimalUtilityAction != null ? minimalUtilityAction.getUtility() : 0.0;

            minimalUtility = minimalUtility * this.discountFactor;

            state.setPreviousUtility(state.getUtility());
            state.setUtility(minimalUtility);
            state.setBestAction(minimalUtilityAction);
            state.setBestActions(minimalUtilityActions);
          //  LocalCacheManager.storeStateInCache(state);
//  ......%%%%%


//            System.out.println("^^^ updating state's minimal utility:: Setting Action:" + minimalUtilityAction + " by utility:" + minimalUtility + " for " +
//                    "state:" + state);


        }


    }


    //todo: do we need to add functionality to check if the action is unblocked \ possible ???


    /**
     * Sort actions by order - biggest at the top
     *
     * @param sttActionsWithutility
     * @return
     */
    private List<Action> sortMaximalActions(HashMap<String, Action> sttActionsWithutility) {
        List<Action> sortedActionsDesc = sttActionsWithutility.values().stream().collect(Collectors.toList());
        Collections.sort(sortedActionsDesc, new ActionSortDesc());
        return sortedActionsDesc;
    }

    /**
     * Sort actions by order - smaller ones at the top
     *
     * @param sttActionsWithutility
     * @return
     */
    private List<Action> sortMinimalActions(HashMap<String, Action> sttActionsWithutility) {
        List<Action> sortedActionsAsc = sttActionsWithutility.values().stream().collect(Collectors.toList());
        Collections.sort(sortedActionsAsc, new ActionSortAsc());
        return sortedActionsAsc;
    }

//    private Action findMinimalAction(HashMap<String, Action> sttActionsWithutility) {
//
//        Double finalUtility = 10000.0;
//        Action result = null;
//        for (String actionStateKey : sttActionsWithutility.keySet()) {
//            Action currentAction = sttActionsWithutility.get(actionStateKey);
//            Double currentUtility = currentAction.getUtility();
//            if (currentUtility <= finalUtility) {
//                result = currentAction;
//                finalUtility = currentUtility;
//            }
//        }
//        return result;
//    }
//
//    private Action findMaximalAction(HashMap<String, Action> sttActionsWithUtility) {
//
//        Double finalUtility = -10000.0;
//        Action result = null;
//        for (String actionStateKey : sttActionsWithUtility.keySet()) {
//            Action currentAction = sttActionsWithUtility.get(actionStateKey);
//            Double currentUtility = currentAction.getUtility();
//            if (currentUtility > finalUtility) {
//                result = currentAction;
//                finalUtility = currentUtility;
//            }
//        }
//
//        return result;
//    }

    /**
     * Given a state, filter all given actions related to it with updated calculated utility..
     *
     * @param state - the state to filter
     * @return HashMap<String, Double>
     **/

    /**
     * Filter state and find its actions. notice - Even if the action is blocked and NOT possible,
     * so we can find other possible actions down its bestActions list.
     * @param state
     * @param stateActionsWithUtility
     * @return
     */
    private HashMap<String, Action> filterStateActions(final State state, HashMap<String, Action> stateActionsWithUtility) {

        //todo: one can add filter for unneeded transitions de to constraints in the future.
        HashMap<String, Action> stateActions = new HashMap<String, Action>();

        // check whether 'key' = action.getId()_state.getId(), and filter accordingly.
        stateActionsWithUtility.entrySet().stream().filter(act ->
                //act.getKey().endsWith("_src:" + state.getId())
                state.getId().startsWith(Constants.statesPrefix+act.getKey().split("_")[0])
                       // bug:: removed:   && act.getValue().actionIsAllowed(state)
        ).forEach(entry ->
                stateActions.put(entry.getKey(), entry.getValue()));

        // todo: add sort --- IF NEEDED! (currently it isn't , just use min or max)
        return stateActions;
    }

}