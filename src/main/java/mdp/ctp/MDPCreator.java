
package mdp.ctp;

import com.couchbase.client.java.json.JsonObject;
import ctp.BlockingStatus;
import ctp.CTPEdge;
import mdp.generic.Reward;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Graph;
import org.jgrapht.graph.Vertex;
import utils.CollectionUtils;
import utils.CouchbaseClient;

import java.util.*;
import java.util.stream.Collectors;

public class MDPCreator {

    Graph graph;

    public MDPCreator(Graph g) {
        graph = g;
    }

    public HashMap<String, mdp.ctp.Transition> generateTransitions(HashMap<String, mdp.ctp.State> allStates, HashMap<String, Action> allActions){
        HashMap<String,mdp.ctp.Transition> allTransitions = new HashMap<String,mdp.ctp.Transition>();
//        allStates.values().forEach(stt ->
//                allStates.values().forEach(stt2 ->
//                {
//                    Vertex source = stt.getAgentLocation();
//                    Vertex dest = stt2.getAgentLocation();
//                    if (allActions.containsKey(Action.generateId(source, dest))) {
//
//                        Action statesAction = allActions.get(Action.generateId(source, dest));
//                        mdp.ctp.Transition tran = new mdp.ctp.Transition(stt, stt2,statesAction );
//                        allTransitions.put(tran.getTransitionId(),tran);
//
//                    }
//                }));


        for(Action action : allActions.values()) {
            Vertex source = action.getSource();
            Vertex dest = action.getDest();
            for(State src : allStates.values().stream().filter(stt->stt.getAgentLocation() == source).collect(Collectors.toList())) {
                for(State dst : allStates.values().stream().filter(stt->stt.getAgentLocation() == dest).collect(Collectors.toList())) {
                    mdp.ctp.Transition tran = new mdp.ctp.Transition(src, dst,action );
                    allTransitions.put(tran.getTransitionId(),tran);
                }
            }
        }
        return allTransitions;
    }

    public HashMap<String, Reward> generateAllRewards(HashMap<String, Action> allActions) {
        HashMap<String,Reward> rewards = new HashMap<String,Reward>();

        for(Action action : allActions.values()) {
            Vertex source = action.getSource();
            Vertex dest = action.getDest();
            CouchbaseClient cb = new CouchbaseClient();
            List<JsonObject> actionSrcStates = cb.fetchStatesByLocation(source);
            List<JsonObject> actionDestStates = cb.fetchStatesByLocation(dest);
                    Double reward = action.getSourceEdge().getReward();
                    Reward rewardObj = new Reward(srcJson.getString("id"), destJson.getString("id"), action, reward);
                    rewards.put(rewardObj.getId(),rewardObj);

            }
        }
//        allStates.values().forEach(stt ->
//                allStates.values().forEach(stt2 ->
//                {
//                    Vertex source = stt.getAgentLocation();
//                    Vertex dest = stt2.getAgentLocation();
//                    if (allActions.containsKey(Action.generateId(source, dest))) {
//
//                        Action statesAction = allActions.get(Action.generateId(source, dest));
//                        Double reward = statesAction.getSourceEdge().getReward();
//                        Reward rewardObj = new Reward(stt, stt2, statesAction, reward);
//                        rewards.put(rewardObj.getId(),rewardObj);
//
//                    }
//                    else{
//                        Action statesVirtualAction = new Action(source,dest);
//                        Double reward = 0.0;
//                        Reward rewardObj = new Reward(stt, stt2, statesVirtualAction, reward);
//                        rewards.put(rewardObj.getId(),rewardObj);
//                    }
           //     }));

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
        Set<State> allStates = cu.flattenSet(allStatesByLocations);

        CouchbaseClient cb = new CouchbaseClient();
        cb.insertBulkStates(allStates);
        // Sort the List of states:

        // todo: sort by ids:

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

            // sort statuses by alphabet -
            Collections.sort(statusList);
            statusVector.addAll(statusList);
            State s = new State((Vertex) vert, statusVector);
            return s;
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


