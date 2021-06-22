package utils;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import mdp.UtilityCalculator;
import mdp.agent_travel.Agent;
import mdp.ctp.MDPFromGraph;
import mdp.generic.MDP;
import mdp.generic.State;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Graph;

import java.util.HashMap;

public class GraphReader {

    public static void main(String[] args) {

        Graph gr = new Graph("/home/shiris/IdeaProjects/mdpvigeneric/src/main/data/graphs_data/very_basic_mdp_example_graphs/small_graph_81_states.json");

        System.out.println(gr.toString());
        MDPFromGraph mdp = new MDPFromGraph(gr);

        Double epsilon = 0.6;
        Double discountFactor = 0.9;

        UtilityCalculator uc = new UtilityCalculator((MDP) mdp, epsilon, discountFactor);
        MDP mdpNew = uc.setOptimalPolicy();
        for (State s : mdpNew.getStates().values()) {
            System.out.println(s.getId() + ",,," + s.getBestAction()+","+s.getUtility());
        }

        HashMap<String, CTPEdge> graphConfiguration = new HashMap<String, CTPEdge>();
        gr.getEdges().values().stream().forEach(edge -> {
            graphConfiguration.put(((Edge) edge).getId(), new CTPEdge(((Edge) edge), BlockingStatus.Opened));
        });

        Edge edge = graphConfiguration.get("v2_v3").getEdge();
        graphConfiguration.put("v2_v3", new CTPEdge(edge, BlockingStatus.Closed));
        Agent ag = new Agent(mdp, graphConfiguration);
        ag.run();
        //Graph gr = new Graph("graphs_data/very_basic_mdp_example_graphs/very_simple_example_18_states.json");

        //Graph gr = new Graph("graphs_data/dror_data/first_graph.json");

        /// Dror's second Graph Example
        //Graph gr = new Graph("graphs_data/dror_data/second_graph.json");

        /// Dror's third Graph Example
        // Graph gr = new Graph("graphs_data/dror_data/third_graph.json");
        // "default_graph_input.json"
/*
        MDP mdp = GraphReader.GraphToMDP(gr);
        UtilityCalculator uc = new UtilityCalculator(0.1,0.1);
        MDP mdpWithUtility = uc.setOptimalPolicy(mdp);
*/

    }

}
