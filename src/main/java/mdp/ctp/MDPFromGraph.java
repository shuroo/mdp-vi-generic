package mdp.ctp;

import mdp.generic.MDP;
import org.jgrapht.graph.Graph;
import utils.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;


/**
 * Extended MDP, Based on CTP
 */
public class MDPFromGraph extends MDP {

    public HashMap<String, Action> getExtededActions() {
        return extededActions;
    }

    private HashMap<String, Action> extededActions = new HashMap<String, Action>();

    public HashMap<String, State> getExtededStates() {
        return extededStates;
    }

    private HashMap<String, State> extededStates = new HashMap<String, State>();
    private HashMap<String, mdp.ctp.Transition> extededTransitions = new HashMap<String, Transition>();

    /**
     * public MDP (
     * <p>
     * HashMap<String,Transition> transitions,
     * // HashMap <String ActionId,Action>
     * HashMap<String,Action> actions,
     * // HashMap <String StateId,State>  == State(state_id)
     * HashMap<String,State> states,
     * // HashMap < StateID_ActionID, Double >  == R(s,a,s')
     * HashMap<String,Reward> rewards,
     * <p>
     * Boolean isMinimizationProblem
     * ){
     * this.actions = actions;
     * this.transitions = transitions;
     * this.rewards = rewards;
     * this.states = states;
     * this.isMinimizationProblem = isMinimizationProblem;
     * }
     *
     * @param g
     */

    private Graph graph;

    public MDPFromGraph(Graph g) {
        this.graph = g;
        MDPCreator creator = new MDPCreator(graph);
        this.extededActions = creator.generateAllActions();
        this.extededStates = (HashMap) creator.generateAllStates();//generateAllStates()
        this.rewards = creator.generateAllRewards(this.extededStates, this.extededActions);
        this.extededTransitions = creator.generateTransitions(this.extededStates, this.extededActions);
        CollectionUtils cu = new CollectionUtils<mdp.generic.State>();
        this.states = cu.objToHMap((Collection<State>) extededStates.values());
        this.transitions = cu.objToHMap((Collection<Transition>) extededTransitions.values());
        this.actions = cu.objToHMap((Collection<Action>) extededActions.values());
        this.isMinimizationProblem = true;
    }


    /**
     * Find extended action per state -
     * @param state
     * @return
     */
    public mdp.ctp.Action getExtendedAction(State state) {

        if(state.getBestAction() == null){
            return null;
        }
        return this.getExtededActions().get(state.getBestAction().getActionId());
    }
}
