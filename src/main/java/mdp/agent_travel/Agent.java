package mdp.agent_travel;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import mdp.ctp.Action;
import mdp.ctp.MDPFromGraph;
import mdp.ctp.State;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Vertex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

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


    private State fetchStateByVertexAndStatuses(Vertex ver) {
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
        AgentPath path = travelPath2(initial, new AgentPath(this));

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

    /**
     * private AgentPath travelPath2(State current, AgentPath currentPath) {
     * <p>
     * if (current.getAgentLocation().isFinal()) {
     * currentPath.setSucceeded(true);
     * System.out.println("we have reached the destination!!!");
     * return currentPath;
     * }
     * <p>
     * // if the current edge is possible - proceed.
     * // Else - try siblings
     * // If no siblings - go to parent, try again
     * <p>
     * if (bestActionNotNullAndNotVisited(current)) {
     * current.setAgentVisits();
     * currentPath.addToPath(current);
     * State nextState = buildNextStt(current);
     * System.out.println("Going to the next state for current :" + current + " and next:" + nextState + " . noticed they should not be siblings!");
     * return travelPath2(nextState, currentPath);
     * } else if (current.getBestAction() != null && !current.allActionsVisited()) {
     * State newStt = current.setNextBestAction();
     * System.out.println("Travelling sibling-action for current :" + newStt);
     * <p>
     * return travelPath2(newStt, currentPath);
     * } else {
     * <p>
     * // if we already visited the state and its siblings, go to parent and try again..
     * //  return travelParent(current, currentPath);
     * <p>
     * State parentSt = current.getParentState();
     * return travelPath2(parentSt, currentPath);
     * }
     * }
     *
     * @param current
     * @param currentPath
     * @return
     */


    private AgentPath travelParent(State current, AgentPath currentPath) {

        // else: try the parent...
        State parentSt = current.getParentState();
        return travelPath2(parentSt, currentPath);

    }


    private AgentPath trySiblingsThenParent(State current, AgentPath currentPath) {

        if (!current.allActionsVisited()) {

            // Advance next action and try again:
            State newStt = current.setNextBestAction();
            System.out.println("Travelling sibling-action for current :" + newStt);

            return travelPath2(newStt, currentPath);
        } else {
            // if we already visited the state and its siblings, go to parent and try again..
            if (!isRootState(current)) {
                return travelParent(current, currentPath);
            } else {
                // reached the root and no siblings -
                System.out.println("The parent is null - try siblings again...");
                currentPath.setSucceeded(false);
                return currentPath;

            }

        }


    }

    private boolean isFinal(State current) {
        return (current.getAgentLocation().isFinal());
    }


    // todo: set to maximal in the parallel problem:
    // When   edge is found blocked, decide where to go to next:

    /**
     * Find minimal state to go to in case of regression:
     *
     * @param current
     * @param currentPath
     * @param bestActionsIndex
     * @return
     */
    private State findMinimalState(State current, AgentPath currentPath, Integer bestActionsIndex) {
        State bestSt = current;
        Double minimalUtility = 100000000.0;
        for (State st : currentPath.getPath()) {
            if (st.getBestActions().size() >= bestActionsIndex) {
                continue;
            } else if (st.getBestActions().get(bestActionsIndex).getUtility() < minimalUtility) {
                bestSt = st;
                minimalUtility = st.getBestActions().get(bestActionsIndex).getUtility();
            }
        }

        return bestSt;
    }

    private AgentPath travelOptimally(State current, AgentPath currentPath) {

        Integer regressionIndex = 0;
        current.setAgentVisits();
        currentPath.addToPath(current);

        if (isFinal(current)) {
            currentPath.setSucceeded(true);
            return currentPath;
        }

        // For the current state:
        // - Take best action

        State nextState = buildNextStt(current);

        if (isFinal(nextState)) {
            currentPath.setSucceeded(true);
            currentPath.addToPath(nextState);
            return currentPath;
        } else if (!edgeIsOpened(nextState)) {
            regressionIndex++;
            State nextStateByRegression = findMinimalState(current, currentPath,regressionIndex);
            while(current != nextStateByRegression){
                currentPath.addGoBackState(current);
                current = current.getParentState();
            }
            return travelOptimally(current, currentPath);
        } else if (isActionNotNullOrNotAllVisited(nextState)) {
            System.out.println("Going to the next state for current :" + current + " and next:" + nextState + " . noticed they should not be siblings!");
            return travelPath2(nextState, currentPath);
        } else if (!current.allActionsVisited()) {
            currentPath.addGoBackState(current);
            current = current.getParentState();
            return travelOptimally(current, currentPath);
        }

        // if next is blocked etc -
        // try to set next action and revisit.
        // if no next best action -
        // go to parent and try again
        currentPath.setSucceeded(false);
        System.out.println("No further Options - aborting.");
        return currentPath;


    }

    private AgentPath travelPath2(State current, AgentPath currentPath) {

        current.setAgentVisits();
        currentPath.addToPath(current);

        if (isFinal(current)) {
            currentPath.setSucceeded(true);
            return currentPath;
        }


        // For the current state:
        // - Take best action

        State nextState = buildNextStt(current);

        if (isFinal(nextState)) {
            currentPath.setSucceeded(true);
            currentPath.addToPath(nextState);
            return currentPath;
        } else if (!edgeIsOpened(nextState)) {
            currentPath.addGoBackState(nextState);
            return trySiblingsThenParent(current, currentPath);
        } else if (isActionNotNullOrNotAllVisited(nextState)) {
            System.out.println("Going to the next state for current :" + current + " and next:" + nextState + " . noticed they should not be siblings!");
            return travelPath2(nextState, currentPath);
        } else if (!current.allActionsVisited()) {
            return trySiblingsThenParent(current, currentPath);
        }

        // if next is blocked etc -
        // try to set next action and revisit.
        // if no next best action -
        // go to parent and try again
        currentPath.setSucceeded(false);
        System.out.println("No further Options - aborting.");
        return currentPath;


    }


    private Boolean isActionNotNullOrNotAllVisited(State current) {
        if (current.getAgentLocation().isFinal()) {
            return true;
        }
        if (current.getBestAction() == null ||
                current.allActionsVisited()) {
            return false;
        }

        return true;
    }
    // Check if the next state is at all possible - is the edge blocked?


    private Boolean edgeIsOpened(State current) {
        Action best = this.mdp.getExtendedAction(current);
        if (best == null) {
            return false;
        }
        Vertex source = best.getSource();
        Vertex dest = best.getDest();
        CTPEdge edgeStatus = graphConfiguration.get(Edge.buildId(source, dest));

        if (edgeStatus != null && edgeStatus.getStatus() != BlockingStatus.Opened) {
            return false;
        }
        return true;
    }

}



