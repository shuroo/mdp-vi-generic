package utils;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import mdp.ctp.CTPUtilityCalculator;
import mdp.generic.State;
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
        MDP mdpWithUtility = uc.setOptimalPolicy(mdp);

        List statesList = Arrays.stream(mdpWithUtility.getStates().values().stream().toArray()).collect(Collectors.toList());

        for(Object s : statesList){

            if(((State)s).getUtility() > 0){
                State currentS = ((State)s);

                System.out.println("State of agent location:"+currentS.getAgentLocation().toString()+" Has final Utility of:"+currentS.getUtility()+"| state " +
                        "statuses:"+currentS.getId()+"|");
            }

        }


        ////////////////////////////////////////////////////////////////////////////

        HashMap<String, CTPEdge> graphConfiguration = new HashMap<String, CTPEdge>();
        gr.getEdges().values().stream().forEach(edge -> {
            graphConfiguration.put(((Edge) edge).getId(), new CTPEdge(((Edge) edge), BlockingStatus.O));
        });


        // Override the existing configuration:
        for(String key : edgeKeysToBlock) {
            if (graphConfiguration.containsKey(key)) {
                Edge edge2 = graphConfiguration.get(key).getEdge();
                graphConfiguration.put(key, new CTPEdge(edge2, BlockingStatus.C));
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

    public static void runThirdGraphNoBlocks(){
        String graphName = "src/main/data/graphs_data/dror_data/third_graph_releifed.json";
        runStandardConfigurationGraph(graphName,0.6,0.9);
    }

    public static void runFirstGraph(){
        String firstGraph = "src/main/data/graphs_data/dror_data/first_graph.json";
        List<String> edgesToBlock = new LinkedList<String>();
        //edgesToBlock.add("v1_v4");
       // edgesToBlock.add("s_v2");
        //edgesToBlock.add("v1_t");
        runConfigurationGraph(firstGraph,edgesToBlock,0.6,0.9);
    }

    public static void runFirstGraphReleifedV2(){
        String firstGraph = "src/main/data/graphs_data/dror_data/first_graph_releifed_v2.json";
        List<String> edgesToBlock = new LinkedList<String>();
        //edgesToBlock.add("v1_v4");
        // edgesToBlock.add("s_v2");
        //edgesToBlock.add("v1_t");
        runConfigurationGraph(firstGraph,edgesToBlock,0.6,0.9);
    }


    public static void runSecondGraph(){
        String graphName = "src/main/data/graphs_data/dror_data/second_graph.json";
        List<String> edgesToBlock = new LinkedList<String>();
        //edgesToBlock.add("v0_t");
        // edgesToBlock.add("s_v2");
        //edgesToBlock.add("v1_t");
        runConfigurationGraph(graphName,edgesToBlock,0.6,0.9);
    }


    public static void main(String[] args) {

       // runFirstGraphWithBlocks();

        runFirstGraphReleifedV2();
        //runSecondGraph();
        //runThirdGraphNoBlocks();
        // runSimpleGraphWithBlocks();


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
