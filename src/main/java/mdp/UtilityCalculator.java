package mdp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class UtilityCalculator {

    Double epsilon = 0.1;
    Double discountFactor = 1.0;

    private MDP currentMDP;

    // --- Parameter for setting during calculation. for private usage: ---
    private Double maxLambda = 100000.0;

    public UtilityCalculator(MDP currentMDP) {
        this.currentMDP = currentMDP;
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
                System.out.println("Utility found for state:"+state.getId()+" and bestAction:"+state.getBestAction()+" is: "+state.getUtility());
                if (prevUtility == null) {
                    continue;
                }
                Double diffUtility = Math.abs(minimalUtility - prevUtility);
                // max diff per ALL states ... //
                if (maxLambda > diffUtility) {
                    maxLambda = diffUtility;
                }

             //   if (maxLambda <= stopCondition) {

                    System.out.println("***** Stopping at lambda:" + maxLambda + " on iteration:" + iterationCounter + " *****");
                    return currentMDP;
               //, }
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
//            if(tran.getTransitionId().startsWith("left_pos_3_3")){
//                System.out.println("state_33_util:"+utility);
//            }

            Action action = tran.getAction();

            if(!actionsWithUtility.containsKey(sourceActionKey)) {
                actionsWithUtility.put(sourceActionKey, new LinkedList<Action>());
            }

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

            System.out.println("transition:"+transition.getTransitionId()+"----^^:" + joinedProb + " actionSubUtility:" + actionSubUtility);
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

            Double reward = minimalUtilityAction != null ? currentMDP.getRewards().get(Reward.buildId(state, state, minimalUtilityAction)).getReward() : null;
            minimalUtility = minimalUtilityAction != null ? (reward + minimalUtilityAction.getUtility()) : 0.0;

            state.setPreviousUtility(state.getUtility());
            //minimalUtility = CollectionUtils.roundTwoDigits(minimalUtility);

            System.out.println("--### Setting utility: " + minimalUtility + " for state: " + state.getId() + "###--");
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
        HashMap<String, Action> stateTransitions = new HashMap<String, Action>();

        // check whether 'key' = action.getId()_state.getId(), and filter accordingly.
        stateActionsWithUtility.entrySet().stream().filter(tran -> tran.getKey().endsWith("_src:" +state.getId())).forEach(entry ->
                stateTransitions.put(entry.getKey(), entry.getValue()));

        // todo: add sort --- IF NEEDED! (currently it isn't , just use min or max)
        //stateTransitions.values();
        return stateTransitions;
    }

    //sort elements by values


    public static void main(String[] args) {

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
        State s33 = new State("pos_3_3", false, false, 0.0);
        states.put(s33.getId(), s33);
        State s34 = new State("pos_3_4", false, true, 1.0);
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
//
//        // s24 transitions:
//        Transition t61 = new Transition(s24, s24, right, 0.8);
//        transitions.put(t61.getTransitionId(), t61);
//        Transition t62 = new Transition(s24, s34, right, 0.1);
//        transitions.put(t62.getTransitionId(), t62);
//        // todo; does it go DOWN instead of right? - make sure!!
//        Transition t63 = new Transition(s24, s14, right, 0.1);
//        transitions.put(t63.getTransitionId(), t63);
//        Transition t64 = new Transition(s24, s34, up, 0.8);
//        transitions.put(t64.getTransitionId(), t64);
//        Transition t65 = new Transition(s24, s24, up, 0.2);
//        transitions.put(t65.getTransitionId(), t65);
//        Transition t67 = new Transition(s24, s23, left, 0.8);
//        transitions.put(t67.getTransitionId(), t67);
//        Transition t68 = new Transition(s24, s14, left, 0.1);
//        transitions.put(t68.getTransitionId(), t68);
//        Transition t69 = new Transition(s24, s34, left, 0.1);
//        transitions.put(t69.getTransitionId(), t69);
//        Transition t70 = new Transition(s24, s14, down, 0.8);
//        transitions.put(t70.getTransitionId(), t70);
//        Transition t71 = new Transition(s24, s23, down, 0.1);
//        transitions.put(t71.getTransitionId(), t71);
//        Transition t72 = new Transition(s24, s24, down, 0.1);
//        transitions.put(t72.getTransitionId(), t72);

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
        Transition t89 = new Transition(s33, s34, left, 0.8);
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
//        // todo: can remove s24 transitions, s34 transitions as they dont change!!!
//        Transition t95 = new Transition(s34, s34, right, 0.9);
//        transitions.put(t95.getTransitionId(), t95);
//        Transition t96 = new Transition(s34, s24, right, 0.1);
//        transitions.put(t96.getTransitionId(), t96);
//        // todo; does it go DOWN instead of right? - make sure!!
//        Transition t98 = new Transition(s34, s34, up, 0.9);
//        transitions.put(t98.getTransitionId(), t98);
//        Transition t99 = new Transition(s34, s33, up, 0.1);
//        transitions.put(t99.getTransitionId(), t99);
//
//        Transition t100 = new Transition(s34, s33, left, 0.8);
//        transitions.put(t100.getTransitionId(), t100);
//        Transition t101 = new Transition(s34, s34, left, 0.1);
//        transitions.put(t101.getTransitionId(), t101);
//        Transition t102 = new Transition(s34, s24, left, 0.1);
//        transitions.put(t102.getTransitionId(), t102);
//        Transition t103 = new Transition(s34, s24, down, 0.8);
//        transitions.put(t103.getTransitionId(), t103);
//        Transition t104 = new Transition(s34, s33, down, 0.1);
//        transitions.put(t104.getTransitionId(), t104);
//        Transition t105 = new Transition(s34, s34, down, 0.1);
//        transitions.put(t105.getTransitionId(), t105);

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
