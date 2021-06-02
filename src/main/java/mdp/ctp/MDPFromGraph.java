package mdp.ctp;
import mdp.generic.MDP;
import org.jgrapht.graph.Graph;

import java.util.HashMap;
import java.util.Map;


/**
 *  Extended MDP, Based on CTP
 */
public class MDPFromGraph extends MDP {

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
        this.actions = creator.generateAllActions();
        this.states =  (HashMap)creator.generateAllStates();//generateAllStates()
        for(String stateId : this.states.keySet()){
            System.out.println(stateId);
        }
        //this.transitions = creator.generateTransitions();
        //this.rewards = creator.generateRewards();

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
