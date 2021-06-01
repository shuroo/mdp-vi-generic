/*package ctp.translator;

import com.sun.corba.se.impl.orbutil.graph.Graph;
import mdp.generic.Action;
import mdp.generic.MDP;

import java.util.HashMap;
import java.util.stream.Collectors;

public class CTPGraphToMDP {
    public static MDP GraphToMDP(Graph g) {

        MDPCreator mdpc = new MDPCreator(g);

        HashMap<String, Action> actions = mdpc.edgesToActions();
        List<LinkedList<CTPEdge>> statusCombinations =
                mdpc.generateStatusesByEdges((List<Edge>) g.getEdges().values().stream().collect(Collectors.toList()));

        Map<String,State>  allStates = mdpc.generateStatesMapFromStatuses(statusCombinations);

        System.out.println("Graph is translated to an MDP of "+actions.size()+" actions, "+allStates.size()+" states");
        // System.out.println("States before setting location::" + statusCombinations.size() + ":: And after:" + allStates.size());
        // todo: convert list to hashmap
        return new MDP(actions, (HashMap<String,State>)allStates);
    }
}*/
