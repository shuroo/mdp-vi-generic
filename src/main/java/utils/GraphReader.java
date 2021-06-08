package utils;

import mdp.UtilityCalculator;
import mdp.ctp.MDPFromGraph;
import mdp.generic.MDP;
import org.jgrapht.graph.Graph;

public class GraphReader {


   /* public static MDP GraphToMDP(Graph g) {

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
*/



    // todo: write with states and probs instead of statuses only...
/*    private static HashMap<String,State> generateStatusesByEdges(List<Edge> edges, HashMap<String,State> statesUnderConstruction) {
        if (edges.isEmpty()) {
            return statesUnderConstruction;
        } else {


            Edge edge = edges.remove(0);
            HashMap<String,State> edgeStates =  generateAllStatusesFromEdge(edge);
            //for(Edge edge : edges){

            if(statesUnderConstruction.isEmpty()){
                return generateStatusesByEdges(edges, edgeStates);
            }

                // Find all combinations!
                // ()()

            Vector<CTPEdge> updatedStatusSingleState = new Vector<CTPEdge>();
            HashMap<String,State> resultingStates = new HashMap<String, State>();
            for(State edgeState : edgeStates.values()){

                    statesUnderConstruction.values().forEach(prevState -> {

                            updatedStatusSingleState.addAll(prevState.getEdgeStatuses());
                            updatedStatusSingleState.addAll(edgeState.getEdgeStatuses());
                            State mixedState = new State(null, updatedStatusSingleState, edgeState.getStateProbability() * prevState.getStateProbability());
                            resultingStates.put(mixedState.getStateId(),mixedState);
                        });


                }


                return generateStatusesByEdges(edges, resultingStates);

            }


    }*/


    public static void main(String[] args) {

        /// My Default Graph Example
        // SnapshotRunner sr = new SnapshotRunner("default_graph_input.json");

        /// First Graph Example
        // SnapshotRunner sr = new SnapshotRunner("graphs_data/default_graph_input.json");

        /// Dror's first Graph Example
        //  Graph gr = new Graph("graphs_data/default_graph_input.json");

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
