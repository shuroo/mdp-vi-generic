package mdp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UtilityCalculator {

    Double epsilon = 0.1;
    Double discountFactor = 0.98;

    private MDP currentMDP;

    // --- Parameter for setting during calculation. for private usage: ---
    private Double maxLambda = 100000.0;

    public UtilityCalculator(MDP currentMDP) {
        this.currentMDP = currentMDP;
    }

    public MDP setOptimalPolicy() {

        Integer iterationCounter = 0;
        Double stopCondition = epsilon * (1 - discountFactor) / discountFactor;

        List<Action> allActions = currentMDP.getActions().values().stream().collect(Collectors.toList());
        List<State> allStates = currentMDP.getStates().values().stream().collect(Collectors.toList());

        while (maxLambda >= stopCondition) {

            iterationCounter++;

            setUtilitiesForStatesIteration(allActions, allStates);


            // Check diff to stop...
            for (State state : allStates) {

                Double minimalUtility = state.getUtility();
                Double prevUtility = state.getPreviousUtility();
                // System.out.println("Utility found for state:"+state.getStateId()+" and bestAction:"+state.getBestAction()+" is: "+state.getUtility());
                Double diffUtility = Math.abs(minimalUtility - prevUtility);
                // max diff per ALL states ... //
                if (maxLambda > diffUtility) {
                    maxLambda = diffUtility;
                }

                if (maxLambda < stopCondition) {

                    System.out.println("Stopping at lambda:" + maxLambda + " on iteration:" + iterationCounter);
                    break;
                }


            }

            currentMDP.getStates().values().stream().forEach(state -> {
                System.out.println("**** Final Set for state:" + state.toString());
            });

        }

        return currentMDP;
    }

    /**
     * Method to return list of actions with updated utility.
     *
     * @param - states to calc utility from
     * @return updated utilities on actions with sorted list.
     * action_utility <-- 0 if final_state, else Sigma(p(s|s')*U'(s'))
     */
    private HashMap<String,Action> setActionsUtilityPerState(List<Action> allActions) {

        // Init & Build Map<ActionId_State,Action>
        for (Transition transition : currentMDP.getTransitions().values()) {
            sourceStates.get(transition.getAction()).add(transition.getSourceState());
        }
        HashMap<Action, List<State>> actionSourcesStMap = getSourceStatesByAction();
        HashMap<Action, List<State>> actionDestinationsMap = getDestStatesByAction();

        HashMap<String,Action> actionsPerSourceStt = new HashMap<String,Action>();
        for (Action action : allActions) {

            // Stage 1: create map<StateId_ActionId,Action> to  find all states belonging to an action.

            List<State> actionSourceStates = actionSourcesStMap.get(action);
            List<State> actionDestStates = actionDestinationsMap.get(action);
            Double actionLocalUtility = 0.0;
            // Business Logic:
            for (State sourceState : actionSourceStates) {
                for (State destState : actionDestStates) {
                    actionLocalUtility = calcStatesUtility(sourceState, destState, action);
                }
                action.setUtility(actionLocalUtility);
                actionsPerSourceStt.put(action.getActionId()+"_"+sourceState.getId(),action);
            }

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
                    , source, dest));

            // The probability for transition between the above states
            Double joinedProb = transition != null ? transition.getProbability() : 0.00;

            // Utility for the two states to add to the action.
            // U(action) <-- Sigma[ P(s|s')*U(s') ]
            Double actionSubUtility = joinedProb * (dest.getUtility());
            // we DON'T set the source utility at this point YET! choosing minimum.

            System.out.println("----^^:" + joinedProb + "actionSubUtility:" + actionSubUtility
            );
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
        HashMap<String,Action> updatedActionsUtility = setActionsUtilityPerState(bestActions);

        for (State state : allStates) {
            setUtilitySingleState(state, updatedActionsUtility);
        }

        return allStates;
    }

    private void setUtilitySingleState(State state, List<Action> actionsWithUtility) {

        //  Get all actions belonging to this state:
        List<Action> stateActionsWithUtility = getStateActions(state, actionsWithUtility);

        //  allActions.stream().filter(action -> action.getId().equals(state.getAgentLocation().getId())).collect(Collectors.toSet());
        Action minimalUtilityAction = null;
        Double minimalUtility = 0.0;

        if (!stateActionsWithUtility.isEmpty()) {
            minimalUtilityAction = currentMDP.isMinimizationProblem() ? findMinimalAction(state,
                    stateActionsWithUtility) : findMaximalAction(state, stateActionsWithUtility);

            // U(s) <- R(s,a,(s'??)) + Utility(a)

            // todo: avoid using the same state twice like in example 17.2

            Double reward = minimalUtilityAction != null ? currentMDP.getRewards().get(Reward.buildId(state, state, minimalUtilityAction)).getReward() : null;
            minimalUtility = minimalUtilityAction != null ? (reward + minimalUtilityAction.getUtility()) : 0.0;
        }

        state.setPreviousUtility(state.getUtility());
        //minimalUtility = CollectionUtils.roundTwoDigits(minimalUtility);
        state.setUtility(minimalUtility);
        state.setBestAction(minimalUtilityAction);

        if(minimalUtilityAction != null){
            System.out.println("--setting action:" + minimalUtilityAction.getActionId() + " with utility:" + minimalUtilityAction.getUtility() + " for state:" + state.getId());
        }
    }


    //todo: do we need to add functionality to check if the action is unblocked \ possible ???

    private Action findMinimalAction(State state, List<Action> stateActionsFiltered) {
        Integer actionIndex = stateActionsFiltered.size() - 1;
        Action currentAction = (Action) stateActionsFiltered.toArray()[actionIndex];
        while (actionIndex > -1 && actionIsImpossible(state, currentAction)) {
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


    // for solving maximization MDP based problems..
    private Action findMaximalAction(State state,  List<Action> stateActionsFiltered) {
        Integer actionIndex = 0;
        Action currentAction = (Action) stateActionsFiltered.toArray()[actionIndex];
        while (actionIndex > -1 && actionIsImpossible(state, currentAction)) {
            actionIndex++;
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
    private Boolean actionIsImpossible(State st, Action action) {
        return false;
    }

    /**
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
            destStates.get(transition.getAction()).add(transition.getDestState());
        }
        return destStates;
    }
     **/

    private List<Action> getStateActions(final State state, List<Action> actionsWithUtility) {
        Set<Transition> stateTransitions =
         currentMDP.getTransitions().values().stream().filter(tran -> tran.getSourceState() == state).collect(Collectors.toSet());

        Set<Action> stateAllActions = stateTransitions.stream().map(transition -> transition.getAction()).collect(Collectors.toSet());

        List<Action> stateActionsWithUtility = actionsWithUtility.stream().filter(action -> stateAllActions.contains(action)).collect(Collectors.toList());

        stateActionsWithUtility.sort(Action::compareTo);
        return stateActionsWithUtility;
    }

    public static void main(String[] args) {

        System.out.println("--");

        // Stimulating MDP example from page 207


        HashMap<String, State> states = new HashMap<String, State>();

        State s11 = new State("pos_1_1", true, false, 0.0);
        states.put(s11.getId(), s11);
        State s12 = new State("pos_1_2", false, false, 0.0);
        states.put(s12.getId(), s12);
        State s13 = new State("pos_1_3", false, false, 0.0);
        states.put(s13.getId(), s13);
        State s14 = new State("pos_1_4", false, false, 0.0);
        states.put(s14.getId(), s14);
        State s21 = new State("pos_2_1", false, false, 0.0);
        states.put(s21.getId(), s21);
        State s22 = new State("pos_2_2", false, false, 0.0);
        states.put(s22.getId(), s22);
        State s23 = new State("pos_2_3", false, false, 0.0);
        states.put(s23.getId(), s23);
        State s24 = new State("pos_2_4", false, false, -1.0);
        states.put(s24.getId(), s24);
        State s31 = new State("pos_3_1", false, false, 0.0);
        states.put(s31.getId(), s31);
        State s32 = new State("pos_3_2", false, false, 0.0);
        states.put(s32.getId(), s32);
        State s33 = new State("pos_3_3", false, true, 0.0);
        states.put(s33.getId(), s33);
        State s34 = new State("pos_3_4", false, false, 1.0);
        states.put(s34.getId(), s34);


        Action up = new Action("up");
        Action left = new Action("left");
        Action right = new Action("right");
        Action down = new Action("down");

        //Reward  everyReward = new Reward(s11,s11,up,-0.04);

        HashMap<String, Transition> transitions = new HashMap<String, Transition>();

        // s11 transitions:
        Transition t1 = new Transition(s11, s12, right, 0.8);
        transitions.put(t1.getTransitionId(), t1);
        Transition t2 = new Transition(s11, s21, right, 0.1);
        transitions.put(t2.getTransitionId(), t2);
        Transition t3 = new Transition(s11, s11, right, 0.1);
        transitions.put(t3.getTransitionId(), t3);
        Transition t4 = new Transition(s11, s21, up, 0.8);
        transitions.put(t4.getTransitionId(), t4);
        Transition t5 = new Transition(s11, s12, up, 0.1);
        transitions.put(t5.getTransitionId(), t5);
        Transition t6 = new Transition(s11, s11, up, 0.1);
        transitions.put(t6.getTransitionId(), t6);
        Transition t7 = new Transition(s11, s11, left, 0.8);
        transitions.put(t7.getTransitionId(), t7);
        Transition t8 = new Transition(s11, s21, left, 0.1);
        transitions.put(t8.getTransitionId(), t8);
        Transition t9 = new Transition(s11, s11, left, 0.1);
        transitions.put(t9.getTransitionId(), t9);

        // s12 transitions:
        Transition t10 = new Transition(s12, s13, right, 0.8);
        transitions.put(t10.getTransitionId(), t10);
        Transition t12 = new Transition(s12, s12, right, 0.2);
        transitions.put(t12.getTransitionId(), t12);
        Transition t13 = new Transition(s12, s12, up, 0.8);
        transitions.put(t13.getTransitionId(), t13);
        Transition t14 = new Transition(s12, s13, up, 0.1);
        transitions.put(t14.getTransitionId(), t14);
        Transition t15 = new Transition(s12, s11, up, 0.1);
        transitions.put(t15.getTransitionId(), t15);
        Transition t16 = new Transition(s12, s11, left, 0.8);
        transitions.put(t16.getTransitionId(), t16);
        Transition t18 = new Transition(s12, s12, left, 0.2);
        transitions.put(t18.getTransitionId(), t18);

        // s13 transitions:
        Transition t19 = new Transition(s13, s14, right, 0.8);
        transitions.put(t19.getTransitionId(), t19);
        Transition t20 = new Transition(s13, s23, right, 0.1);
        transitions.put(t20.getTransitionId(), t20);
        Transition t21 = new Transition(s13, s13, right, 0.1);
        transitions.put(t21.getTransitionId(), t21);
        Transition t22 = new Transition(s13, s23, up, 0.8);
        transitions.put(t22.getTransitionId(), t22);
        Transition t23 = new Transition(s13, s14, up, 0.1);
        transitions.put(t23.getTransitionId(), t23);
        Transition t24 = new Transition(s13, s12, up, 0.1);
        transitions.put(t24.getTransitionId(), t24);
        Transition t25 = new Transition(s13, s12, left, 0.8);
        transitions.put(t25.getTransitionId(), t25);
        Transition t26 = new Transition(s13, s23, left, 0.1);
        transitions.put(t26.getTransitionId(), t26);
        Transition t27 = new Transition(s13, s13, left, 0.1);
        transitions.put(t27.getTransitionId(), t27);


        // s14 transitions:
        Transition t28 = new Transition(s14, s14, right, 0.8);
        transitions.put(t28.getTransitionId(), t28);
        Transition t29 = new Transition(s14, s24, right, 0.1);
        transitions.put(t29.getTransitionId(), t29);
        Transition t30 = new Transition(s14, s13, right, 0.1);
        transitions.put(t30.getTransitionId(), t30);
        Transition t31 = new Transition(s14, s24, up, 0.8);
        transitions.put(t31.getTransitionId(), t31);
        Transition t32 = new Transition(s14, s14, up, 0.1);
        transitions.put(t32.getTransitionId(), t32);
        Transition t33 = new Transition(s14, s13, up, 0.1);
        transitions.put(t33.getTransitionId(), t33);
        Transition t34 = new Transition(s14, s13, left, 0.8);
        transitions.put(t34.getTransitionId(), t34);
        Transition t35 = new Transition(s14, s24, left, 0.1);
        transitions.put(t35.getTransitionId(), t35);
        Transition t36 = new Transition(s14, s14, left, 0.1);
        transitions.put(t36.getTransitionId(), t36);


        // s21 transitions:
        Transition t37 = new Transition(s21, s21, right, 0.9);
        transitions.put(t37.getTransitionId(), t37);
        Transition t38 = new Transition(s21, s31, right, 0.1);
        transitions.put(t38.getTransitionId(), t38);
        Transition t40 = new Transition(s21, s31, up, 0.8);
        transitions.put(t40.getTransitionId(), t40);
        Transition t41 = new Transition(s21, s21, up, 0.2);
        transitions.put(t41.getTransitionId(), t41);
        Transition t43 = new Transition(s21, s21, left, 0.9);
        transitions.put(t43.getTransitionId(), t43);
        Transition t44 = new Transition(s21, s31, left, 0.1);
        transitions.put(t44.getTransitionId(), t44);

        Transition t46 = new Transition(s21, s11, down, 0.8);
        transitions.put(t46.getTransitionId(), t46);
        Transition t47 = new Transition(s21, s21, down, 0.1);
        transitions.put(t47.getTransitionId(), t47);
        Transition t48 = new Transition(s21, s21, down, 0.1);
        transitions.put(t48.getTransitionId(), t48);

        // s23 transitions:
        Transition t49 = new Transition(s23, s24, right, 0.8);
        transitions.put(t49.getTransitionId(), t49);
        Transition t50 = new Transition(s23, s23, right, 0.1);
        transitions.put(t50.getTransitionId(), t50);
        Transition t56 = new Transition(s23, s33, right, 0.1);
        transitions.put(t56.getTransitionId(), t56);
        Transition t51 = new Transition(s23, s33, up, 0.8);
        transitions.put(t51.getTransitionId(), t51);
        Transition t52 = new Transition(s23, s24, up, 0.1);
        transitions.put(t52.getTransitionId(), t52);
        Transition t53 = new Transition(s23, s23, up, 0.1);
        transitions.put(t53.getTransitionId(), t53);
        Transition t54 = new Transition(s23, s23, left, 0.9);
        transitions.put(t54.getTransitionId(), t54);
        Transition t55 = new Transition(s23, s13, left, 0.1);
        transitions.put(t55.getTransitionId(), t55);
        Transition t57 = new Transition(s23, s33, left, 0.1);
        transitions.put(t57.getTransitionId(), t57);
        Transition t58 = new Transition(s23, s13, down, 0.8);
        transitions.put(t58.getTransitionId(), t58);
        Transition t59 = new Transition(s23, s23, down, 0.1);
        transitions.put(t59.getTransitionId(), t59);
        Transition t60 = new Transition(s23, s24, down, 0.1);
        transitions.put(t60.getTransitionId(), t60);

        // s24 transitions:
        Transition t61 = new Transition(s24, s24, right, 0.8);
        transitions.put(t61.getTransitionId(), t61);
        Transition t62 = new Transition(s24, s34, right, 0.1);
        transitions.put(t62.getTransitionId(), t62);
        // todo; does it go DOWN instead of right? - make sure!!
        Transition t63 = new Transition(s24, s14, right, 0.1);
        transitions.put(t63.getTransitionId(), t63);
        Transition t64 = new Transition(s24, s34, up, 0.8);
        transitions.put(t64.getTransitionId(), t64);
        Transition t65 = new Transition(s24, s24, up, 0.2);
        transitions.put(t65.getTransitionId(), t65);
        Transition t67 = new Transition(s24, s23, left, 0.8);
        transitions.put(t67.getTransitionId(), t67);
        Transition t68 = new Transition(s24, s14, left, 0.1);
        transitions.put(t68.getTransitionId(), t68);
        Transition t69 = new Transition(s24, s34, left, 0.1);
        transitions.put(t69.getTransitionId(), t69);
        Transition t70 = new Transition(s24, s14, down, 0.8);
        transitions.put(t70.getTransitionId(), t70);
        Transition t71 = new Transition(s24, s23, down, 0.1);
        transitions.put(t71.getTransitionId(), t71);
        Transition t72 = new Transition(s24, s24, down, 0.1);
        transitions.put(t72.getTransitionId(), t72);

        ////// ------   LAST LINE:  --------

        // s31 transitions:
        Transition t73 = new Transition(s31, s32, right, 0.8);
        transitions.put(t73.getTransitionId(), t73);
        Transition t74 = new Transition(s31, s31, right, 0.2);
        transitions.put(t74.getTransitionId(), t74);
        Transition t75 = new Transition(s31, s31, up, 0.9);
        transitions.put(t75.getTransitionId(), t75);
        Transition t76 = new Transition(s31, s32, up, 0.1);
        transitions.put(t76.getTransitionId(), t76);
        Transition t77 = new Transition(s31, s31, left, 0.9);
        transitions.put(t77.getTransitionId(), t77);
        Transition t78 = new Transition(s31, s21, left, 0.1);
        transitions.put(t78.getTransitionId(), t78);

        Transition t80 = new Transition(s31, s21, down, 0.8);
        transitions.put(t80.getTransitionId(), t80);
        Transition t81 = new Transition(s31, s31, down, 0.1);
        transitions.put(t81.getTransitionId(), t81);
        Transition t82 = new Transition(s31, s32, down, 0.1);
        transitions.put(t82.getTransitionId(), t82);


        // s32 transitions:
        Transition t106 = new Transition(s32, s33, right, 0.8);
        transitions.put(t10.getTransitionId(), t10);
        Transition t107 = new Transition(s32, s32, right, 0.2);
        transitions.put(t12.getTransitionId(), t12);

        // todo: "up" and "down" can be removed.
        Transition t108 = new Transition(s32, s32, up, 0.8);
        transitions.put(t108.getTransitionId(), t108);
        Transition t109 = new Transition(s32, s33, up, 0.1);
        transitions.put(t109.getTransitionId(), t109);
        Transition t110 = new Transition(s32, s31, up, 0.1);
        transitions.put(t110.getTransitionId(), t110);
        Transition t111 = new Transition(s32, s31, left, 0.8);
        transitions.put(t111.getTransitionId(), t111);
        Transition t112 = new Transition(s32, s32, left, 0.2);
        transitions.put(t112.getTransitionId(), t112);

        Transition t113 = new Transition(s32, s32, down, 0.8);
        transitions.put(t113.getTransitionId(), t113);
        Transition t114 = new Transition(s32, s33, down, 0.1);
        transitions.put(t114.getTransitionId(), t114);
        Transition t115 = new Transition(s32, s31, down, 0.1);
        transitions.put(t115.getTransitionId(), t115);

        // s33 transitions:
        Transition t83 = new Transition(s33, s34, right, 0.8);
        transitions.put(t83.getTransitionId(), t83);
        Transition t84 = new Transition(s33, s33, right, 0.1);
        transitions.put(t84.getTransitionId(), t84);
        Transition t85 = new Transition(s33, s23, right, 0.1);
        transitions.put(t85.getTransitionId(), t85);
        Transition t86 = new Transition(s33, s33, up, 0.8);
        transitions.put(t86.getTransitionId(), t86);
        Transition t87 = new Transition(s33, s34, up, 0.1);
        transitions.put(t87.getTransitionId(), t87);
        Transition t88 = new Transition(s33, s32, up, 0.1);
        transitions.put(t88.getTransitionId(), t88);
        Transition t89 = new Transition(s33, s32, left, 0.8);
        transitions.put(t89.getTransitionId(), t89);
        Transition t90 = new Transition(s33, s23, left, 0.1);
        transitions.put(t90.getTransitionId(), t90);
        Transition t91 = new Transition(s33, s33, left, 0.1);
        transitions.put(t91.getTransitionId(), t91);
        Transition t92 = new Transition(s33, s23, down, 0.8);
        transitions.put(t92.getTransitionId(), t92);
        Transition t93 = new Transition(s33, s31, down, 0.1);
        transitions.put(t93.getTransitionId(), t93);
        Transition t94 = new Transition(s33, s34, down, 0.1);
        transitions.put(t94.getTransitionId(), t94);

        // s34 transitions:
        // todo: can remove s24 transitions, s34 transitions as they dont change!!!
        Transition t95 = new Transition(s34, s34, right, 0.9);
        transitions.put(t95.getTransitionId(), t95);
        Transition t96 = new Transition(s34, s24, right, 0.1);
        transitions.put(t96.getTransitionId(), t96);
        // todo; does it go DOWN instead of right? - make sure!!
        Transition t98 = new Transition(s34, s34, up, 0.9);
        transitions.put(t98.getTransitionId(), t98);
        Transition t99 = new Transition(s34, s33, up, 0.1);
        transitions.put(t99.getTransitionId(), t99);

        Transition t100 = new Transition(s34, s33, left, 0.8);
        transitions.put(t100.getTransitionId(), t100);
        Transition t101 = new Transition(s34, s34, left, 0.1);
        transitions.put(t101.getTransitionId(), t101);
        Transition t102 = new Transition(s34, s24, left, 0.1);
        transitions.put(t102.getTransitionId(), t102);
        Transition t103 = new Transition(s34, s24, down, 0.8);
        transitions.put(t103.getTransitionId(), t103);
        Transition t104 = new Transition(s34, s33, down, 0.1);
        transitions.put(t104.getTransitionId(), t104);
        Transition t105 = new Transition(s34, s34, down, 0.1);
        transitions.put(t105.getTransitionId(), t105);

        HashMap<String, Action> actions = new HashMap<String, Action>();

        actions.put(up.getActionId(), up);
        actions.put(left.getActionId(), left);
        actions.put(down.getActionId(), down);
        actions.put(right.getActionId(), right);

        HashMap<String, Reward> rewards = new HashMap<String, Reward>();

        for (Action action : actions.values()) {

            for (State s1 : states.values()) {

                for (State s2 : states.values()) {
                    Reward reward = new Reward(s1, s2, action, -0.04);
                    rewards.put(reward.getId(), reward);
                }
            }
        }


        MDP mdp = new MDP(transitions, actions, states, rewards, false);
        UtilityCalculator uc = new UtilityCalculator(mdp);
        uc.setOptimalPolicy();
    }
}
