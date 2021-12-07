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

        this.currentMDP = (MDP)currentMDP;
        this.extendedMDP = currentMDP;
        this.discountFactor = discountFactor;
        this.epsilon = epsilon;
    }

    protected HashMap<mdp.generic.State, HashMap<mdp.generic.Action, List<Transition>>> aggregateTransitionsPerState() {

        // DATA STRUCTURE TO CALC UTILITY: FOR EACH STATE, FIND -
        // - ITS RELATED STATES ( IN WHICH ITS THEIR DEST )
        // - ITS RELATED ACTIONS
        // - TO EACH RELATED ACTION - STATES TO SUM THEIR UTILITY BY:
        // - U(action) <--  R(s,s',a) + Sigma[ P(s|s')*( U(s') )]
        // - WE ARE BUILDING THE DATA STRUCTURE AS FOLLOWS:
        //  HashMap<SRC_State, HashMap<Action,List{  TUPLE<DEST_STATE,UTILITY,PROBABILITY>>}>

        HashMap<mdp.generic.State, HashMap<mdp.generic.Action,List<Transition>>> statesDataMap = new HashMap<mdp.generic.State, HashMap<mdp.generic.Action,List<Transition>>>();

        for (Transition transition : currentMDP.getTransitions().values()) {

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

    protected HashMap<mdp.generic.State, HashMap<mdp.generic.Action,List<Transition>>> calcAndSetStatesUtilities() {

        // DATA STRUCTURE TO CALC UTILITY: FOR EACH STATE, FIND -
        // - ITS RELATED STATES ( IN WHICH ITS THEIR DEST )
        // - ITS RELATED ACTIONS
        // - TO EACH RELATED ACTION - STATES TO SUM THEIR UTILITY BY:
        // - U(action) <--  R(s,s',a) + Sigma[ P(s|s')*( U(s') )]
        // - WE ARE BUILDING THE DATA STRUCTURE AS FOLLOWS:
        //  HashMap<DEST_State, HashMap<Action,List{ List{ TUPLE<SRC_STATE,UTILITY,PROBABILITY>>}}>

        HashMap<mdp.generic.State, HashMap<mdp.generic.Action,List<Transition>>> statesDataMap = aggregateTransitionsPerState() ;

        for(State stt :  statesDataMap.keySet()){
            HashMap<mdp.generic.Action,List<Transition>> stateTransitionsPerAction = statesDataMap.get(stt);
            HashMap<mdp.generic.Action,Double> utilityPerAction = new HashMap<mdp.generic.Action,Double>();
            // Find utility per state per action:
            for(Action act : stateTransitionsPerAction.keySet()){
                // init utility per action.
                if(!utilityPerAction.containsKey(act)){
                    utilityPerAction.put(act,0.0);
                }
                List<Transition> actTransitions = stateTransitionsPerAction.get(act);
                for(Transition tr : actTransitions){
                    if(!tr.isValid() ){
                        continue;
                    }
                    if(tr.getProbability() == 1.0){
                        // = reward + 1*(U(s'))
                        utilityPerAction.put(act,tr.getDestState().getUtility());
                        continue;
                    }
                    else{
                        boolean hasNoProbabilityOneLegalStt =
                                actTransitions.stream().filter(t-> t.isValid() && t.getProbability() == 1).collect(Collectors.toList()).isEmpty();
                        if(hasNoProbabilityOneLegalStt){
                            Double currentUtil = utilityPerAction.get(act);
                            utilityPerAction.put(act,currentUtil + tr.getDestState().getUtility());
                        }
                        else continue;
                    }
                }

                Double reward = extendedMDP.getExtededActions().containsKey(act.getActionId()) ?
                        extendedMDP.getExtededActions().get(act.getActionId()).getSourceEdge().getReward() : 0.0;
                Double currentUtil = utilityPerAction.get(act);
                utilityPerAction.put(act,currentUtil + reward);

            }

            // Then , find the minimal action and update the dest \ parent state accordingly.
            findMinimalUtilityAmongActionsPerState(stt,utilityPerAction);
        }

        return statesDataMap;
    }

    /**
     * For Each state and map of utility actions, choose the most minimal utility and update the state.
     */
    private void findMinimalUtilityAmongActionsPerState(State st,HashMap<Action,Double> utilityActions){
        Double minimalUtil = null;
        Action chosenAction = null;
        for(Action action : utilityActions.keySet()){
            Double currentUtility = utilityActions.get(action);
            if(minimalUtil == null || currentUtility < minimalUtil){
                minimalUtil = currentUtility;
                chosenAction = action;
            }
        }

        State stInMdp = currentMDP.getStates().get(st.toString());
        stInMdp.setPreviousUtility(stInMdp.getUtility());
        stInMdp.setUtility(minimalUtil);
        System.out.println("Set utility:"+minimalUtil+" For State:"+stInMdp.getId());
        stInMdp.setBestAction(chosenAction);
    }

    @Override
    protected HashMap<String, State> setUtilitiesForStatesIteration(HashMap<String, State> allStates) {

        System.out.println("in sub method -- setUtilitiesForStatesIteration!!!");
        calcAndSetStatesUtilities();

        return allStates;
    }

}
