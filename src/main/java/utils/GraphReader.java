package utils;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import mdp.ctp.CTPUtilityCalculator;
import mdp.generic.UtilityCalculator;
import mdp.agent_travel.Agent;
import mdp.ctp.MDPFromGraph;
import mdp.generic.MDP;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphReader {


    /**
     * Run Graph with the trivial configuration: Having only open edges;
     */
    public static void runConfigurationGraph(String graphName, List<String> edgeKeysToBlock, Double epsilon, Double discount){
        Graph gr = new Graph(graphName);

        System.out.println("Constructing MDP:");
        MDPFromGraph mdp = new MDPFromGraph(gr);
        System.out.println("Built MDP with:"+mdp.getStates().size()+" States");
        CTPUtilityCalculator uc = new CTPUtilityCalculator( mdp, epsilon, discount);
        MDP mdpWithUtility = uc.setOptimalPolicy();

        List statesList = Arrays.stream(mdpWithUtility.getStates().values().stream().toArray()).collect(Collectors.toList());

        for(Object s : statesList){
            System.out.println(s);
        }
        //

        ////////////////////////////////////////////////////////////////////////////

        HashMap<String, CTPEdge> graphConfiguration = new HashMap<String, CTPEdge>();
        gr.getEdges().values().stream().forEach(edge -> {
            graphConfiguration.put(((Edge) edge).getId(), new CTPEdge(((Edge) edge), BlockingStatus.Opened));
        });


        // Override the existing configuration:
        for(String key : edgeKeysToBlock) {
            if (graphConfiguration.containsKey(key)) {
                Edge edge2 = graphConfiguration.get(key).getEdge();
                graphConfiguration.put(key, new CTPEdge(edge2, BlockingStatus.Closed));
            }
        }

        System.out.println("Initializing Agent...");
        Agent ag = new Agent(mdp, graphConfiguration);

        System.out.println("Running Agent...");
        ag.run();

    }


    /**
     * Run Graph with the trivial configuration: Having only open edges;
     */
    public static void runStandardConfigurationGraph(String graphName,Double epsilon,Double discount){
        runConfigurationGraph(graphName,new LinkedList<String> (),epsilon,discount);
    }

    public static void runSimpleGraphWithBlocks(){
        String simpleGraph = "src/main/data/graphs_data/very_basic_mdp_example_graphs/small_graph_81_states.json";
        List<String> edgesToBlock = new LinkedList<String>();
        edgesToBlock.add("v1_v3");
        edgesToBlock.add("v2_v3");
        runConfigurationGraph(simpleGraph,edgesToBlock,0.6,0.9);
    }

    public static void runFirstGraphWithBlocks(){
        String firstGraph = "src/main/data/graphs_data/dror_data/first_graph_releifed.json";
        List<String> edgesToBlock = new LinkedList<String>();
        //edgesToBlock.add("v1_v4");
       // edgesToBlock.add("s_v2");
        //edgesToBlock.add("v1_t");
        runConfigurationGraph(firstGraph,edgesToBlock,0.6,0.9);
    }

    public static void runThirdGraphNoBlocks(){
        String firstGraph = "src/main/data/graphs_data/dror_data/third_graph_releifed.json";
        runStandardConfigurationGraph(firstGraph,0.6,0.9);
    }
    public static void main(String[] args) {

        runFirstGraphWithBlocks();
        //runThirdGraphNoBlocks();


      //  String firstGraph = "src/main/data/graphs_data/dror_data/first_graph_releifed.json";
     //   runStandardConfigurationGraph(firstGraph,0.5,0.98);

       // HashMap<String,CTPEdge> blockedEdges = new HashMap<String,CTPEdge>();
       // (Edge) edge.getId(), new CTPEdge(((Edge) edge), BlockingStatus.Opened));
       //runStandardConfigurationGraph("src/main/data/graphs_data/dror_data/second_graph.json");

/*        Graph gr = new Graph(firstGraph);
        MDPFromGraph mdp = new MDPFromGraph(gr);
        HashMap<String,CTPEdge> blockedMap = new HashMap<String,CTPEdge>();
        blockedMap.put("v1_t", new CTPEdge((Edge)gr.getEdges().get("v1_t"), BlockingStatus.Closed))
        runConfigurationGraph(firstGraph,blockedMap);*/
    }

}
