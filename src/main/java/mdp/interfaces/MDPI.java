package mdp.interfaces;

import mdp.Action;
import mdp.Reward;
import mdp.State;
import mdp.Transition;

import java.util.HashMap;

public interface MDPI {

    // HashMap < StateID_ActionID, Transition > == P(s,a,s')
    HashMap<String, Transition> getTransitions();

    // HashMap <String ActionId,Action>
    HashMap<String, Action> getActions();

    // HashMap <String StateId,State>  == State(state_id)
    HashMap<String, State> getStates();

    // HashMap < StateID_ActionID, Double >  == R(s,a,s')
    HashMap<String, Reward> getRewards();
}
