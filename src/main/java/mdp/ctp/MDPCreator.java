
package mdp.ctp;

import ctp.CTPEdge;
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


    public HashMap<String, Action> generateAllActions() {

        HashMap<String, Action> allActionsMap = new HashMap<String, Action>();
        //List<Edge> edges = (List<Edge>) graph.getEdges().values().stream().collect(Collectors.toList());
        List<Action> allActions = graph.getEdges().values().stream().map(
                edge ->{
            //Edge mdpe = Edge.mdpeFromEdge(edge);
            Action action = new Action((Edge)edge);
                   // allActions.put(action.getActionId(), action);
            return action;
        }).collect(Collectors.toList());

    //////////////////////

    public Map<String, State> generateStatesMapFromStatuses(List<LinkedList<CTPEdge>> statusCombinations) {
        List<Set<State>> allStatesByLocations =
                statusCombinations.stream().map(statusList -> generateStatesFromStatus(statusList)).collect(Collectors.toList());
        // Flatten list:

        CollectionUtils<State> cu = new CollectionUtils<State>();
        List<State> allStates = cu.flattenList(allStatesByLocations);
        Map<String, State> allStatesMap = cu.stateToMap(allStates);

        return allStatesMap;
    }

    private Set<State> generateStatesFromStatus(List<CTPEdge> statusList) {

        Double blockingProb = calcStateBlockingProbability(statusList);
        Set<State> resultingStates = (Set<State>) graph.getVertices().values().stream().map(vert -> {
            Vertex mdpVert = new Vertex((Vertex) vert);
            Vector<CTPEdge> statusVector = new Vector<CTPEdge>();
            statusVector.addAll(statusList);
            return new State(mdpVert, statusVector, blockingProb);
        }).collect(Collectors.toSet());
        return resultingStates;
    }


    protected List<LinkedList<CTPEdge>> generateStatusesByEdges(List<Edge> edges) {

        List<LinkedList<CTPEdge>> edgeStatusesToCombine = new LinkedList<LinkedList<CTPEdge>>();
        for (Edge edge : edges) {
            //Edge edge = edges.remove(i);


            if (edgeStatusesToCombine.isEmpty()) {

                HashMap<String, State> edgeStatuses = generateAllStatusesFromEdge(edge);

                // create a list of single status element (*3)
                for (State singleStatus : edgeStatuses.values()) {
                    LinkedList<CTPEdge> edgeSingleStatusList = new LinkedList<CTPEdge>();
                    List<CTPEdge> status = singleStatus.getEdgeStatuses(); // edge+O OR
                    // edge+C
                    // OR edge+U
                    edgeSingleStatusList.addAll(status);// make sure this adds only one!! status elemen
                    edgeStatusesToCombine.add(edgeSingleStatusList);
                }

                //System.out.println("Initial edge statuses:"+edgeStatuses.size()+",,edgeStatusesToCombine:"+edgeStatusesToCombine.size());

            } else {
                List<LinkedList<CTPEdge>> statusestoCreate = new LinkedList<LinkedList<CTPEdge>>();
                for (LinkedList<CTPEdge> oldStatus : edgeStatusesToCombine) {
                    HashMap<String, State> edgeStatuses = generateAllStatusesFromEdge(edge);

                    for (State singleStatus : edgeStatuses.values()) {
                        LinkedList<CTPEdge> edgeSingleStatusList = new LinkedList<CTPEdge>();
                        edgeSingleStatusList.addAll(oldStatus);
                        Vector<CTPEdge> status = singleStatus.getEdgeStatuses(); // edge+O OR edge+C OR
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

    protected static HashMap<String, State> generateAllStatusesFromEdge(Edge edge) {

        MDPEdge mdpedge = MDPEdge.mdpeFromEdge(edge);
        CTPEdge es = new CTPEdge(mdpedge, BlockingStatus.Closed);

        // (e1,c)p=...,(e1,o),(e1,u)

        Vector<CTPEdge> st1_v = new Vector<CTPEdge>();
        st1_v.add(es);
        State st1 = new State(null, st1_v, edge.getBlockingProbability());

        Vector<CTPEdge> st2_v = new Vector<CTPEdge>();
        CTPEdge es2 = new CTPEdge(mdpedge, BlockingStatus.Opened);
        st2_v.add(es2);
        State st2 = new State(null, st2_v, (1 - edge.getBlockingProbability()));

        Vector<CTPEdge> st3_v = new Vector<CTPEdge>();
        CTPEdge es3 = new CTPEdge(mdpedge, BlockingStatus.Unknown);
        st3_v.add(es3);
        State st3 = new State(null, st3_v, 1.0);


        HashMap<String, State> newStatesToConcat = new HashMap<String, State>();
        newStatesToConcat.put(st1.getStateId(), st1);
        newStatesToConcat.put(st2.getStateId(), st2);
        newStatesToConcat.put(st3.getStateId(), st3);

        return newStatesToConcat;
    }

}


