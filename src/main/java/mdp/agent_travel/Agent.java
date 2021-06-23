package mdp.agent_travel;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import mdp.ctp.MDPFromGraph;
import mdp.ctp.State;
import mdp.generic.Action;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Vertex;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Agent implements Runnable {

    private Double agentPathCost;
    MDPFromGraph mdp;
    HashMap<String, CTPEdge> graphConfiguration;
    List<AgentPath> agentPaths = new LinkedList<AgentPath>();

    public Agent(MDPFromGraph mdp, HashMap<String, CTPEdge> graphConfiguration) {

        this.graphConfiguration = graphConfiguration;
        this.mdp = mdp;
        this.agentPathCost = 0.0;

    }

    /**
     * Find state to start with:
     * - make sure it has an initial agent location
     * - make sure its configuration is valid.
     * - make sure it has a bestAction configured (else return null and abort)
     *
     * @return
     */


/*    private State findInitialStt() {
        HashMap<String, State> states = mdp.getExtededStates();

        for (State stt : states.values()) {
            if (stt.getBestAction() != null &&
                    stt.getBestAction().actionIsAllowed(stt) && //todo: improve this condition and code
                    graphConfiguration.get(stt.getBestAction().getActionId()).getStatus() == BlockingStatus.Opened
                    && stt.getAgentLocation().isInitial()) {
                // new State(stt.getAgentLocation(), new Vector(graphConfiguration.values()))
                return stt;
            }
        }

        System.out.println("Failed to find appropriate initial state!");
        return null;
    }*/


    private State buildInitialStt() {
        HashMap<String, State> states = mdp.getExtededStates();

        for (State stt : states.values()) {
            if (stt.getBestAction() != null &&
                    stt.getBestAction().actionIsAllowed(stt) && //todo: improve this condition and code
                    graphConfiguration.get(stt.getBestAction().getActionId()).getStatus() == BlockingStatus.Opened
                    && stt.getAgentLocation().isInitial()) {
                State sttWithUpdatedStatuses = new State(stt.getAgentLocation(), new Vector(graphConfiguration.values()));
                String stateId = sttWithUpdatedStatuses.getId();
                // return the state configured, with data fetched from its mdp:
                return mdp.getExtededStates().get(stateId);
            }
        }

        System.out.println("Failed to find appropriate initial state!");
        return null;
    }

    /**
     * Change unknown statuses into 'opened':
     *
     * @param currentState - the state to set
     * @param nextV        - the vertex to use as source
     * @return List of statuses
     */
    private HashMap<String, CTPEdge> openUnknownStatuses(Vertex nextV, State currentState) {
        HashMap<String, CTPEdge> statuses = new HashMap<String, CTPEdge>();
        statuses.putAll(currentState.getStatuses());
        statuses.entrySet().stream().forEach(statusedEdge -> {
            if (statusedEdge.getValue().getEdge().getSource() == nextV) {
                /*
                // todo: edge should be opened - throw exception if it's not.
                if(statusedEdge.getValue().getStatus() == BlockingStatus.Closed){

                    throw new Exception("Edge is closed!");
                }}*/
                if (statusedEdge.getValue().getStatus() == BlockingStatus.Unknown) {
                    statuses.put(statusedEdge.getKey(), new CTPEdge(statusedEdge.getValue().getEdge(), BlockingStatus.Opened));
                }
            }
        });

        return statuses;
    }

    /**
     * find initial state, run, return a valid path when reached a final state.
     */
    public void run() {
        State initial = buildInitialStt();
        List<AgentPath> allPaths = travelPath(initial, new AgentPath(), new LinkedList<AgentPath>());

        System.out.println("**Found:"+allPaths.size()+" Paths.**");
        for (AgentPath path : allPaths) {
            System.out.println(path);
        }

    }

    private List<AgentPath> travelPath(State current, AgentPath currentPath, List<AgentPath> previousPaths) {

        current.setIsVisited(true);
        currentPath.addToPath(current);
        if (current.getAgentLocation().isFinal()) {
            currentPath.setSucceeded(true);
            previousPaths.add(currentPath);
            return previousPaths;
        }


        // For the current state:
        // - Take best action

        mdp.ctp.Action currentSttAction = mdp.getExtededActions().get(current.getBestAction().toString());
        //todo: replace this to appropriate inheritance of action!
        // - Update agent position
        Vertex nextV = currentSttAction.getDest();
        HashMap<String, CTPEdge> nextStatuses = openUnknownStatuses(nextV, current);
        String newSttId = State.buildId(nextV, nextStatuses);
        State nextState = mdp.getExtededStates().get(newSttId);

        nextState.setParentState(current);

        if (nextStateIsValid(current, nextState)) {
            return travelPath(nextState, currentPath, previousPaths);
        }

        List<State> siblingStates = filterDifferentStateActions(current);

        if (!siblingStates.isEmpty()) {
            // todo: fix: return minimal utility to use
            // todo: make sure to fetch minimal utility
            return travelPath(siblingStates.get(0), currentPath, previousPaths);
        }

        // else: try the parent...
        // todo: wrap in method:
        State parentSt = current.getParentState();

            if (parentSt  == null) {
                System.out.println("Reached the root!");
                currentPath.setSucceeded(false);
                previousPaths.add(currentPath);
                return previousPaths;
            } else {
                // try the parent again:
                // reset visited flag to try the sibling with a new path.
                // reset path history:

                currentPath.updateCostUponRegression(currentSttAction);

                return travelPath(parentSt, currentPath, previousPaths);
            }


        // If the siblings are not empty, try the first sibling. remove the current which failed -


    }


    // Check if the next state is at all possible - is the edge blocked?

    /**
     * State is valid:
     * <p>
     * - It's action's edge is not blocked;
     * - Not yet visited;
     *
     * @param prev
     * @param current
     * @return
     */
    private boolean nextStateIsValid(State prev, State current) {

        if ((current.getBestAction() == null && !current.getAgentLocation().isFinal()) || current.getAgentVisited()) {
            return false;
        }
        Vertex source = prev.getAgentLocation();
        Vertex dest = current.getAgentLocation();
        CTPEdge edgeStatus = graphConfiguration.get(Edge.buildId(source, dest));

        if (edgeStatus != null && edgeStatus.getStatus() != BlockingStatus.Opened) {
            return false;
        }
        return true;

        // todo: make sure 'unknown' is not valid!
    }

    /**
     * @param states
     * @return
     */
    private List<State> sortStatesByUtility(List<State> states) {
        Collections.sort(states, new StateComperator());
        return states;
    }


    /**
     * Filter states by vertex, validity, and sort by utility asc.
     */

    private List<State> filterDifferentStateActions(State current) {
        Vertex vert = current.getAgentLocation();
        Action currentAction = current.getBestAction();
        List<State> filteredStates = mdp.getExtededStates().values().stream().filter(stt ->
//                nextStateIsValid(current, stt) &&
//                        stt.getAgentLocation() == vert &&
//
//                        currentAction.getActionId() != stt.getBestAction().getActionId()
                        isSttExpectedStatuses(stt)
                ).collect(Collectors.toList());

        State sampleStt = mdp.getExtededStates().get("Ag_Location::v1,|(v1 : v2)::Opened|,|(v1 : v3)::Opened|,|(v2 : v3)::Closed|");
        //todo: how can it choose v1_v2 action?! -- change logic and reset to v1_v3
        System.out.println("sampleSttIsExpected?"+isSttExpectedStatuses(sampleStt));

        return sortStatesByUtility(filteredStates);
    }

    /**
     * Make sure the current state is entirely as expected by the graphConfiguration
     * @param stt
     * @return
     */
    private Boolean  isSttExpectedStatuses (State stt) {

        Predicate<CTPEdge> edgeStatusesConfigured = edg -> graphConfiguration.get(edg.getEdge().getId()).getStatus() == edg.getStatus();


        List<CTPEdge> validSttStatuses = stt.getStatuses().values().stream()
                    .filter(edgeStatusesConfigured )
                    .collect(Collectors.toList());

        return validSttStatuses.size() == stt.getStatuses().size();
    }



}



