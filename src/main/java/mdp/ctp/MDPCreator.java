
package mdp.ctp;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import mdp.generic.Reward;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Graph;
import org.jgrapht.graph.Vertex;
import utils.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MDPCreator {

    Graph graph;

    public MDPCreator(Graph g) {
        graph = g;
    }

    public HashMap<String, Transition> generateTransitions(HashMap<String, State> allStates, HashMap<String, Action> allActions){
        HashMap<String,Transition> allTransitions = new HashMap<String,Transition>();
        allStates.values().forEach(stt ->
                allStates.values().forEach(stt2 ->
                {
                    Vertex source = stt.agentLocation;
                    Vertex dest = stt2.agentLocation;
                    if (allActions.containsKey(Action.generateId(source, dest))) {

                        Action statesAction = allActions.get(Action.generateId(source, dest));
                        Double bolcProb = statesAction.getSourceEdge().getBlockingProbability();
                        Transition tran = new Transition(stt, stt2, statesAction,0.0 );// 0.0 is a placeholder
                        allTransitions.put(tran.getTransitionId(),tran);

                    }
                }));

        return allTransitions;
    }

    public HashMap<String, Reward> generateAllRewards(HashMap<String, State> allStates, HashMap<String, Action> allActions) {
        HashMap<String,Reward> rewards = new HashMap<String,Reward>();
        allStates.values().forEach(stt ->
                allStates.values().forEach(stt2 ->
                {
                    Vertex source = stt.agentLocation;
                    Vertex dest = stt2.agentLocation;
                    if (allActions.containsKey(Action.generateId(source, dest))) {

                        Action statesAction = allActions.get(Action.generateId(source, dest));
                        Double reward = statesAction.getSourceEdge().getReward();
                        Reward rewardObj = new Reward(stt, stt2, statesAction, reward);
                        rewards.put(rewardObj.getId(),rewardObj);

                    }
                }));

        return rewards;
    }

    public HashMap<String, Action> generateAllActions() {

        HashMap<String, Action> allActionsMap = new HashMap<String, Action>();
        graph.getEdges().values().stream().forEach(
                edge -> {
                    Action action = new Action((Edge) edge);
                    allActionsMap.put(action.getActionId(), action);
                });

        return allActionsMap;

    }

    //////////////////////

    /**
     * Given a Graph, generate all possible states and return it as a map of <StateId,State>
     *
     * @return- Map<String, State> - All possible states.
     */
    public Map<String, State> generateAllStates() {

        List<LinkedList<CTPEdge>> statusCombinations = generateStatusedEdges();

        List<Set<State>> allStatesByLocations =
                statusCombinations.stream().map(statusList -> generateStatesFromStatus(statusList)).collect(Collectors.toList());
        // Flatten list:

        CollectionUtils<State> cu = new CollectionUtils<State>();
        List<State> allStates = cu.flattenList(allStatesByLocations);
        Map<String, State> allStatesMap = cu.stateToMap(allStates);

        return allStatesMap;
    }

    /**
     * Generate a single state based on list of ctp-statused-edges, and all possible vertex combinations.
     *
     * @param statusList - List of List<CTPEdge> where the inner list represents all possible statuses of a SINGLE ctp edge.
     * @return
     */
    private Set<State> generateStatesFromStatus(List<CTPEdge> statusList) {

        Set<State> resultingStates = (Set<State>) graph.getVertices().values().stream().map(vert -> {
            Vector<CTPEdge> statusVector = new Vector<CTPEdge>();
            statusVector.addAll(statusList);
            return new State((Vertex) vert, statusVector);
        }).collect(Collectors.toSet());
        return resultingStates;
    }


    protected List<LinkedList<CTPEdge>> generateStatusedEdges() {


        Collection<Edge> edges = graph.getEdges().values(); //
        List<LinkedList<CTPEdge>> edgeStatusesToCombine = new LinkedList<LinkedList<CTPEdge>>();
        for (Edge edge : edges) {
            //Edge edge = edges.remove(i);


            if (edgeStatusesToCombine.isEmpty()) {

                List<Vector<CTPEdge>> edgeStatuses = generateAllStatusesFromEdge(edge);

                // create a list of single status element (*3)
                for (List<CTPEdge> status : edgeStatuses) {
                    LinkedList<CTPEdge> edgeSingleStatusList = new LinkedList<CTPEdge>();
                    // edge+O OR
                    // edge+C
                    // OR edge+U
                    edgeSingleStatusList.addAll(status);// make sure this adds only one!! status elemen
                    edgeStatusesToCombine.add(edgeSingleStatusList);
                }

                //System.out.println("Initial edge statuses:"+edgeStatuses.size()+",,edgeStatusesToCombine:"+edgeStatusesToCombine.size());

            } else {
                List<LinkedList<CTPEdge>> statusestoCreate = new LinkedList<LinkedList<CTPEdge>>();
                for (LinkedList<CTPEdge> oldStatus : edgeStatusesToCombine) {
                    List<Vector<CTPEdge>> edgeStatuses = generateAllStatusesFromEdge(edge);

                    for (Vector<CTPEdge> status : edgeStatuses) {
                        LinkedList<CTPEdge> edgeSingleStatusList = new LinkedList<CTPEdge>();
                        edgeSingleStatusList.addAll(oldStatus);
                        // edge+O OR edge+C OR
                        // edge+U
                        edgeSingleStatusList.addAll(status);// make sure this adds only one!! status elemen
                        statusestoCreate.add(edgeSingleStatusList);
                    }
                }
                edgeStatusesToCombine = statusestoCreate;
            }
        }

        return edgeStatusesToCombine;
    }


    /**
     * Generate all 3 possible statuses for a given edge.
     *
     * @param edge
     * @return -  HashMap<String, State>
     */

    protected static List<Vector<CTPEdge>> generateAllStatusesFromEdge(Edge edge) {

        CTPEdge es = new CTPEdge(edge, BlockingStatus.Closed);

        // (e1,c)p=...,(e1,o),(e1,u)

        List<Vector<CTPEdge>> results = new LinkedList<Vector<CTPEdge>>();
        Vector<CTPEdge> st1_v = new Vector<CTPEdge>();
        st1_v.add(es);
        results.add(st1_v);

        Vector<CTPEdge> st2_v = new Vector<CTPEdge>();
        CTPEdge es2 = new CTPEdge(edge, BlockingStatus.Opened);
        st2_v.add(es2);
        results.add(st2_v);

        Vector<CTPEdge> st3_v = new Vector<CTPEdge>();
        CTPEdge es3 = new CTPEdge(edge, BlockingStatus.Unknown);
        st3_v.add(es3);
        results.add(st3_v);

        return results;
    }

}


