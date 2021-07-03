package utils;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import mdp.UtilityCalculator;
import mdp.agent_travel.Agent;
import mdp.ctp.MDPFromGraph;
import mdp.generic.MDP;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Graph;

import java.util.HashMap;

public class GraphReader {


    /**
     * Run Graph with the trivial configuration: Having only open edges;
     */
    public static void runConfigurationGraph(String graphName,HashMap<String,CTPEdge> blockedEdges){
        Graph gr = new Graph(graphName);

        MDPFromGraph mdp = new MDPFromGraph(gr);
        Double epsilon = 0.6;
        Double discountFactor = 0.9;

        UtilityCalculator uc = new UtilityCalculator((MDP) mdp, epsilon, discountFactor);
        uc.setOptimalPolicy();

        HashMap<String, CTPEdge> graphConfiguration = new HashMap<String, CTPEdge>();
        gr.getEdges().values().stream().forEach(edge -> {
            graphConfiguration.put(((Edge) edge).getId(), new CTPEdge(((Edge) edge), BlockingStatus.Opened));
        });


        // Override the existing configuration:
        for(String key : blockedEdges.keySet()) {
            if (graphConfiguration.containsKey(key)) {
                Edge edge2 = graphConfiguration.get(key).getEdge();
                graphConfiguration.put(key, new CTPEdge(edge2, BlockingStatus.Closed));
            }
        }

        Agent ag = new Agent(mdp, graphConfiguration);
        ag.run();

    }


    /**
     * Run Graph with the trivial configuration: Having only open edges;
     */
    public static void runStandardConfigurationGraph(String graphName){
        runConfigurationGraph(graphName,new HashMap<String,CTPEdge> ());
    }

    public static void main(String[] args) {

     //   runStandardConfigurationGraph("src/main/data/graphs_data/very_basic_mdp_example_graphs/small_graph_81_states.json");

       // runStandardConfigurationGraph("src/main/data/graphs_data/dror_data/second_graph.json");

        runStandardConfigurationGraph("src/main/data/graphs_data/dror_data/first_graph.json");
    }

}
