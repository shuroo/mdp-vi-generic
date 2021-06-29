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


    private State fetchStateByVertexAndStatuses(Vertex ver){
        State result = new State(ver, new Vector(graphConfiguration.values()));
        return mdp.getExtededStates().get(result.getId());
    }
    private State buildInitialStt() {
        HashMap<String, State> states = mdp.getExtededStates();

        for (State stt : states.values()) {
            if (stt.getBestAction() != null &&
                    //dont force allowed action hre- as if the edge is blocked, catch it later
                    // stt.getBestAction().actionIsAllowed(stt) && //todo: improve this condition and code
                    stt.getAgentLocation().isInitial()) {

               return fetchStateByVertexAndStatuses(stt.getAgentLocation());
            }
        }

        System.out.println("Failed to find appropriate initial state!");
        return null;
    }


    private List<State> findInitialStts() {

        State firstInitial = buildInitialStt();

        // todo: make sure to configure the other states as expected.
        List<State> initials = findSiblings(firstInitial);

        initials.add(firstInitial);
        if (initials.isEmpty()) {
            System.out.println("Failed to find appropriate initial state!");
        }

        return sortStatesByUtility(initials);

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
        List<State> initials = findInitialStts();
        AgentPath path = travelPath2(initials.get(0), new AgentPath(this));

        System.out.println(path);

    }

    /**
     * Build the next state by the current and the  graph configuration statuses:
     *
     * @param current
     * @return
     */
    private State buildNextStt(State current) {

        mdp.ctp.Action currentSttAction = mdp.getExtendedAction(current);
        //todo: replace this to appropriate inheritance of action!
        Vertex nextV = currentSttAction.getDest();
        HashMap<String, CTPEdge> nextStatuses = openUnknownStatuses(nextV, current);
        String newSttId = State.buildId(nextV, nextStatuses);
        State nextState = mdp.getExtededStates().get(newSttId);
        nextState.setParentState(current);

        System.out.println("Building next state for current:" + current.getAgentLocation() + " and next:" + nextState.getAgentLocation() + ". next action is:" + nextState.getBestAction());
        return nextState;
    }

    private Boolean isRootState(State current) {
        State parentSt = current.getParentState();
        return (parentSt == null);
    }

    private AgentPath travelSiblings( List<State> siblingStates, AgentPath currentPath) {


        State minimalUtilitySiblingStt = siblingStates.get(0);
        System.out.println("running with minimalUtilSib:" + minimalUtilitySiblingStt + " and utility:" + minimalUtilitySiblingStt.getUtility() + " and action:" + minimalUtilitySiblingStt.getBestAction());
        return travelPath2(minimalUtilitySiblingStt, currentPath);

    }


    private AgentPath travelParent(State current, AgentPath currentPath) {

        // Go Back - readd the current action with its cost:
        currentPath.addToPath(current);
        // else: try the parent...
        State parentSt = current.getParentState();

        if (parentSt == null) {
            System.out.println("The parent is null - try siblings again...");
            currentPath.setSucceeded(false);
            return currentPath;
        } else {
            // try the parent again:
            // reset visited flag to try the sibling with a new path.
            // reset path history:

            //todo: update path cost:
            System.out.println("running with parent:" + parentSt);

            return travelPath2(parentSt, currentPath);
        }
    }


    private AgentPath travelPath2(State current, AgentPath currentPath) {

        current.setIsVisited(true);
        currentPath.addToPath(current);
        if (current.getAgentLocation().isFinal()) {
            currentPath.setSucceeded(true);
            System.out.println("we have reached the destination!!!");
            return currentPath;
        }


        // For the current state:
        // - Take best action

        State nextState = buildNextStt(current);

        if (nextStateIsValid(current, nextState)) {
            System.out.println("Going to the next state for current :"+current+" and next:"+nextState+" . noticed they should not be siblings!");
            return travelPath2(nextState, currentPath);
        } else {
            List<State> filteredStates = findSiblings(current);
            List<State> siblingStates = sortStatesByUtility(filteredStates);

            for (State s : siblingStates) {
                System.out.println("returned sorted sibling:" + s);
            }

            if (!siblingStates.isEmpty()) {
                System.out.println("Travelling sibling for current :"+current);

                return travelSiblings( siblingStates, currentPath );
            } else {

                // if we already visited the state and its siblings, go to parent and try again..
                return travelParent(current, currentPath);
            }


        }
    }


    /**
     * Check if
     */

    private Boolean isActionNotNullAndNotVisited(State current){
        if (current.getBestAction() == null && !current.getAgentLocation().isFinal() ||
                current.getAgentVisited()) {
            return false;
        }

        return true;
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

        if(!isActionNotNullAndNotVisited(current)){
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

    private List<State> findSiblings(State current) {
        Vertex vert = current.getAgentLocation();
        List<State> filteredStates = mdp.getExtededStates().values().stream().filter(stt ->

                /// todo: improve this!!! 28/06/2021
                        isActionNotNullAndNotVisited(stt) &&
                                stt.getAgentLocation() == vert &&
                                isSttExpectedStatuses(stt)
        ).distinct().collect(Collectors.toList());

        System.out.println("filteredStates? -from current:"+current+", and siblings:" + filteredStates.size());

        filteredStates.forEach(stt-> System.out.println("found sibling:"+stt));


        return filteredStates;
    }

    /**
     * Make sure the current state is entirely as expected by the graphConfiguration
     *
     * @param stt
     * @return
     */
    private Boolean isSttExpectedStatuses(State stt) {

        Predicate<CTPEdge> edgeStatusesConfigured = edg -> graphConfiguration.get(edg.getEdge().getId()).getStatus() == edg.getStatus();


        List<CTPEdge> validSttStatuses = stt.getStatuses().values().stream()
                .filter(edgeStatusesConfigured)
                .collect(Collectors.toList());

        return validSttStatuses.size() == stt.getStatuses().size();
    }


}



