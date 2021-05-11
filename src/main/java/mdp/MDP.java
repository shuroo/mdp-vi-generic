package mdp;

import mdp.interfaces.MDPI;

import java.util.HashMap;

public class MDP implements MDPI{

    // HashMap < StateID_ActionID, Transition > == P(s,a,s')
    private HashMap<String,Transition> transitions;

    // HashMap <String ActionId,Action>
    private HashMap<String,Action> actions;

    // HashMap <String StateId,State>  == State(state_id)
    private HashMap<String,State> states;

    // HashMap < StateID_ActionID, Double >  == R(s,a,s')
    private HashMap<String,Reward> rewards;

    public MDP (

            HashMap<String,Transition> transitions,
            // HashMap <String ActionId,Action>
            HashMap<String,Action> actions,
            // HashMap <String StateId,State>  == State(state_id)
            HashMap<String,State> states,
            // HashMap < StateID_ActionID, Double >  == R(s,a,s')
            HashMap<String,Reward> rewards
    ){
            this.actions = actions;
            this.transitions = transitions;
            this.rewards = rewards;
            this.states = states;
    }


    public HashMap<String, Transition> getTransitions() {
        return transitions;
    }

    public HashMap<String, Action> getActions() {
        return actions;
    }

    public HashMap<String, State> getStates() {
        return states;
    }

    public HashMap<String, Reward> getRewards() {
        return rewards;
    }
}
