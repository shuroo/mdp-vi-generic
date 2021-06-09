package utils;

import mdp.UtilityCalculator;
import mdp.ctp.MDPFromGraph;
import mdp.generic.MDP;
import mdp.generic.Transition;
import org.jgrapht.graph.Graph;

public class GraphReader {



    public static void main(String[] args) {

        Graph gr = new Graph("/home/shiris/IdeaProjects/mdpvigeneric/src/main/data/graphs_data/very_basic_mdp_example_graphs/small_graph_81_states.json");

        System.out.println(gr.toString());
        MDPFromGraph mdp = new MDPFromGraph(gr);

        Double epsilon = 0.6;
        Double discountFactor = 0.9;

        UtilityCalculator uc = new UtilityCalculator((MDP)mdp,epsilon,discountFactor);
        uc.setOptimalPolicy();
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
