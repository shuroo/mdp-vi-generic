package mdp.ctp;

import mdp.generic.*;
import mdp.generic.Action;
import mdp.generic.State;
import mdp.generic.Transition;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CTPUtilityCalculator extends UtilityCalculator {

    protected MDPFromGraph extendedMDP;

    public CTPUtilityCalculator(MDPFromGraph currentMDP, Double epsilon, Double discountFactor) {

        this.currentMDP = currentMDP;
        this.extendedMDP = currentMDP;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
    }

    protected HashMap<mdp.generic.State, HashMap<mdp.generic.Action, List<Transition>>> aggregateTransitionsPerState(MDP updatedMDP) {

        // DATA STRUCTURE TO CALC UTILITY: FOR EACH STATE, FIND -
        // - ITS RELATED STATES ( IN WHICH ITS THEIR DEST )
        // - ITS RELATED ACTIONS
        // - TO EACH RELATED ACTION - STATES TO SUM THEIR UTILITY BY:
        // - U(action) <--  R(s,s',a) + Sigma[ P(s|s')*( U(s') )]
        // - WE ARE BUILDING THE DATA STRUCTURE AS FOLLOWS:
        //  HashMap<SRC_State, HashMap<Action,List{  TUPLE<DEST_STATE,UTILITY,PROBABILITY>>}>

        HashMap<mdp.generic.State, HashMap<mdp.generic.Action,List<Transition>>> statesDataMap = new HashMap<mdp.generic.State, HashMap<mdp.generic.Action,List<Transition>>>();

        for (Transition transition : updatedMDP.getTransitions().values()) {

            if(!statesDataMap.containsKey(transition.getSourceState())){

                // todo : if the transition and the src states are valid, replace the ds from :
                // todo : HashMap< Action, List< List <Transition>>> to HashMap<Action,List<Transition>>
                // todo: but - how do we know whether to add to the existing calculators, or to insert a new list?
                // so, if prob = 1, take it alone, else take all the calculators and sum them.
                // if a tate is not in the ds, then it is no source for transition, aka, illegal or a final state. give it zero utility

                statesDataMap.put(transition.getSourceState(),new HashMap<Action,List<Transition>>());
            }

            if( !statesDataMap.get(transition.getSourceState()).containsKey(transition.getAction())) {
                statesDataMap.get(transition.getSourceState()).put(transition.getAction(), new LinkedList<Transition>());
            }

            statesDataMap.get(transition.getSourceState()).get(transition.getAction()).add(transition);

        }

        return statesDataMap;
    }

    private HashMap<Action,Double> calcUtilityPerAction(State stt,
                                                        HashMap<mdp.generic.State, HashMap<mdp.generic.Action,List<Transition>>> statesDataMap){

        HashMap<mdp.generic.Action,List<Transition>> stateTransitionsPerAction = statesDataMap.get(stt);
        HashMap<mdp.generic.Action,Double> utilityPerAction = new HashMap<mdp.generic.Action,Double>();
        for(Action act : stateTransitionsPerAction.keySet()){
            // initilize utility per action.
            if(!utilityPerAction.containsKey(act)){
                utilityPerAction.put(act,0.0);
            }
            List<Transition> actTransitions = stateTransitionsPerAction.get(act);
            for(Transition tr : actTransitions){
                if(!tr.isValid() ){
                    //System.out.println("Found and eliminating illegal transition of id:"+tr.getTransitionId());
                    continue;
                }
                else if(!tr.getAction().actionEdgeIsTraversive(stt) ){
                    //System.out.println("Found and eliminating CLOSED EDGE of transition. removing transition:"+tr.getTransitionId());
                    continue;
                }
                if(tr.getProbability() == 1.0){
                    // = reward + 1*(U(s'))

             //       System.out.println("*********utility to add, of u(s')="+tr.getDestState().getUtility());
                    utilityPerAction.put(act,tr.getDestState().getUtility());
                    continue;
                }
                else{
                    boolean hasNoProbabilityOneLegalStt =
                            actTransitions.stream().filter(t-> t.isValid() && t.getProbability() == 1).collect(Collectors.toList()).isEmpty();
                    if(hasNoProbabilityOneLegalStt){
                        Double currentUtil = utilityPerAction.get(act);
                        System.out.println("*********utility to add, of u(s')="+tr.getProbability()*tr.getDestState().getUtility());
                        utilityPerAction.put(act,currentUtil + tr.getProbability()*tr.getDestState().getUtility());
                    }
                    else continue;
                }
            }

            Double reward = extendedMDP.getExtededActions().containsKey(act.getActionId()) ?
                    extendedMDP.getExtededActions().get(act.getActionId()).getSourceEdge().getReward() : 0.0;
            Double currentUtil = utilityPerAction.get(act);
            utilityPerAction.put(act,currentUtil + reward);

        }

        return utilityPerAction;
    }

    protected MDP calcAndSetStatesUtilities(MDP updatedMDP) {

        // DATA STRUCTURE TO CALC UTILITY: FOR EACH STATE, FIND -
        // - ITS RELATED STATES ( IN WHICH ITS THEIR DEST )
        // - ITS RELATED ACTIONS
        // - TO EACH RELATED ACTION - STATES TO SUM THEIR UTILITY BY:
        // - U(action) <--  R(s,s',a) + Sigma[ P(s|s')*( U(s') )]
        // - WE ARE BUILDING THE DATA STRUCTURE AS FOLLOWS:
        //  HashMap<DEST_State, HashMap<Action,List{ List{ TUPLE<SRC_STATE,UTILITY,PROBABILITY>>}}>

        HashMap<mdp.generic.State, HashMap<mdp.generic.Action,List<Transition>>> statesDataMap = aggregateTransitionsPerState(updatedMDP);
        for(State stt :  statesDataMap.keySet()){
           // Find utility per state per action:
            HashMap<mdp.generic.Action,Double> utilityPerAction = calcUtilityPerAction(stt,statesDataMap);

            // Then , find the minimal action and update the dest \ parent state accordingly.
            updatedMDP =findMinimalUtilityAmongActionsPerState(stt,utilityPerAction,updatedMDP);
        }

        return updatedMDP;
    }

    /**
     * For Each state and map of utility actions, choose the most minimal utility and update the state.
     */
    private MDP findMinimalUtilityAmongActionsPerState(State st,HashMap<Action,Double> utilityActions, MDP updatedMDP){

        Double minimalUtil = null;
        Action chosenAction = null;
        if(st.getIsFinal()){
            minimalUtil = 0.0;
            chosenAction = null;
        }
        else {
            for (Action action : utilityActions.keySet()) {
                Double currentUtility = utilityActions.get(action);
                if (minimalUtil == null || currentUtility < minimalUtil) {
                    minimalUtil = currentUtility;
                    chosenAction = action;
                }
            }
        }

        try {
            st.setPreviousUtility(st.getUtility());
            st.setUtility(minimalUtil);
            System.out.println("!!!Set utility:"+minimalUtil+" For State:"+st.getId()+"!!!");
            st.setBestAction(chosenAction);
            // update state in the mdp:
            updatedMDP.getStates().put(st.toString(),st);
        } catch (NullPointerException e) {
            System.out.print("State of id: "+st.toString()+" could not be found via mdp and hence, failed to update!");
        }
        return updatedMDP;
    }

    @Override
    public MDP setOptimalPolicy(MDP updatedMDP) {

        Integer iterationCounter = 0;
        Double stopCondition = epsilon * (1 - discountFactor) / discountFactor;
        while (iterationCounter < 3) {
            System.out.println("In subMethod - setOptimalPolicy!! going..."+(iterationCounter+1)+"th");
            HashMap<String, State> allStates = updatedMDP.getStates();
            iterationCounter++;
            System.out.println("Starting iteration number:" + iterationCounter + " with lambda:" + maxLambda);
            updatedMDP = calcAndSetStatesUtilities(updatedMDP);
            State firstResultingStt = (State)updatedMDP.getStates().values().toArray()[0];
            System.out.println(firstResultingStt.getAgentLocation()+","+firstResultingStt.getUtility());

            //*** Stop condition Version 1 - "Normal"  stop condition:*** //
            //todo: ** Notice that, stopCond != 0 since, if stopCond == 0 then, it could belong to a previous converge step.
            // Check diff to stop...

            //*** Stop condition Version 2 -  stop condition by the number of states converged upon a single iteration :*** //

            for (State state : allStates.values()) {

                Double minimalUtility = state.getUtility();
                Double prevUtility = state.getPreviousUtility();
                Double diffUtility = Math.abs(minimalUtility - prevUtility);
                // max diff per ALL states ... //
                if (maxLambda > diffUtility) {
                    System.out.println("** Setting lambda to :" + diffUtility + "**");
                    maxLambda = diffUtility;
                }


            }

             //When all states finished their current iteration - check lambda:

//            if ( maxLambda!=0.0 && maxLambda<= stopCondition) {
//
//                System.out.println("***** Stopping at lambda:" + maxLambda + " on iteration:" + iterationCounter + " *****");
//                return currentMDP;
//            }




        }

        return currentMDP;
    }

}
