package mdp.generic;

import mdp.interfaces.MDPI;

import java.util.HashMap;
import java.util.List;

public class MDP implements MDPI{

    // HashMap <String StateId,State>  == State(state_id)
    protected HashMap<String,State> states;

    // HashMap < StateID_ActionID, Double >  == R(s,a,s')
    protected HashMap<String,Reward> rewards;


    public MDP(){}

    public Boolean isMinimizationProblem() {
        return isMinimizationProblem;
    }

    // Check whether to solve a minimization or maximization problem.
    protected Boolean isMinimizationProblem;

    // HashMap < StateID_ActionID, Transition > == P(s,a,s')
    protected HashMap<String,Transition> transitions;

    // HashMap <String ActionId,Action>
    protected HashMap<String,Action> actions;

    protected void setStates(List<State> states) {
        this.states = new HashMap<String,State>();
        states.stream().forEach(stt-> this.states.put(stt.getId(),stt));
    }

    public MDP (

            HashMap<String,Transition> transitions,
            // HashMap <String ActionId,Action>
            HashMap<String,Action> actions,
            // HashMap <String StateId,State>  == State(state_id)
            HashMap<String,State> states,
            // HashMap < StateID_ActionID, Double >  == R(s,a,s')
            HashMap<String,Reward> rewards,

            Boolean isMinimizationProblem
    ){
            this.actions = actions;
            this.transitions = transitions;
            this.rewards = rewards;
            this.states = states;
            this.isMinimizationProblem = isMinimizationProblem;
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
