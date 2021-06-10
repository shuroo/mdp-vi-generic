package mdp.ctp;

import mdp.generic.MDP;
import org.jgrapht.graph.Graph;
import utils.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;


/**
 *  Extended MDP, Based on CTP
 */
public class MDPFromGraph extends MDP {

    HashMap<String,Action> extededActions = new HashMap<String,Action>();
    HashMap<String,State> extededStates = new HashMap<String,State>();
    HashMap<String,mdp.ctp.Transition> extededTransitions = new HashMap<String, Transition>();

    /**
     *
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
     * @param g
     */

    private Graph graph;

    public MDPFromGraph(Graph g){
        this.graph = g;
        MDPCreator creator = new MDPCreator(graph);
        this.extededActions = creator.generateAllActions();
        this.extededStates =  (HashMap)creator.generateAllStates();//generateAllStates()
        this.rewards = creator.generateAllRewards(this.extededStates,this.extededActions);
        this.extededTransitions = creator.generateTransitions(this.extededStates,this.extededActions);
        CollectionUtils cu = new CollectionUtils<mdp.generic.State>();
        this.states = cu.objToHMap((Collection<State>) extededStates.values() );
        this.transitions = cu.objToHMap((Collection<Transition>) extededTransitions.values() );
        this.actions = cu.objToHMap((Collection<Action>) extededActions.values() );
        this.isMinimizationProblem = true;

    }
}

/**
 *  State s34 = new State("pos_3_4", false, true, 1.0);
 *         states.put(s34.getId(), s34);
 *
 *         Action up = new Action("up");
 *         Action left = new Action("left");
 *         Action right = new Action("right");
 *         //Action down = new Action("down");
 *
 *         HashMap<String, Transition> transitions = new HashMap<String, Transition>();
 *
 *         // s11 transitions:
 *         Transition t1 = new Transition(s11, s12, right, 0.8);
 *         transitions.put(t1.getTransitionId(), t1);
 *
 */
