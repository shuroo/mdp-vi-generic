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
        AgentPath path = travelOptimally(initial, new AgentPath(this),0);

        System.out.println("*********************************************");
        System.out.println(path);
        System.out.println("*******************^^^^^^^^^^^^^****************");
        for( State stt : path.getPath()){
            System.out.println("State:"+stt.getId()+" has the following best actions:");
            if(stt.getBestActions() == null) {
                System.out.println("State: "+stt.getId()+" Has No Best Actions specified");
            }else{
                for (int i = 0; i < stt.getBestActions().size(); i++) {
                    mdp.generic.Action action = stt.getBestActions().get(i);
                    if (action != null) {
                        System.out.println("Best Action of priority:"+i+" Is Action:" + action.getActionId() + " has utility:" + action.getUtility());
                    }
                }
            }
        }
        System.out.println("*********************************************");
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
        if(current == null){ return false; }
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

        if (!current.allBestStateActionsAreTried()) {

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

    /**
     * Check whether the current state is final, or its next action should be final (like in v4_t
     * - this is the last state to be added to the path as t is a
     * final state).
     * @param current
     * @return
     */
    private boolean isFinal(State current) {
        return (current.getAgentLocation().isFinal()); //||
                //mdp.getExtededActions().get(current.getBestAction().getActionId()).getDest().isFinal());
    }


    // todo: set to maximal in the parallel problem:
    // When   edge is found blocked, decide where to go to next:

    /**
     * Find minimal state to go to in case of regression:
     *
     * @param 
     * @param currentPath
     * @param bestActionsIndex
     * @return
     */
    private State findMinimalStateAmongSiblingsAndParents(State current, AgentPath currentPath, Integer bestActionsIndex) {
        State bestSt = null;
        Double minimalUtility = 100000000.0;
        List<State> statesToCompareWithSiblingsAndParents = new LinkedList<>();
        statesToCompareWithSiblingsAndParents.addAll(currentPath.getPath());
        statesToCompareWithSiblingsAndParents.add(current);
        for (State st : statesToCompareWithSiblingsAndParents) {
             if (bestActionsIndex < st.getBestActions().size()  &&
                     st.getBestActions().get(bestActionsIndex).getUtility() < minimalUtility) {
                 if(st.getBestActions() != null &&
                         actionIsOpened(mdp.getExtededActions().get(st.getBestActions().get(bestActionsIndex)))) {
                     bestSt = st;
                     minimalUtility = st.getBestActions().get(bestActionsIndex).getUtility();
                 }
            }
        }

        if(bestSt != null) {
            bestSt.setBestAction(bestSt.getBestActions().get(bestActionsIndex));
        }else{
            System.out.println("Failed to appropriate state for regression - returning null!");
        }
        return bestSt;
    }

    private AgentPath travelOptimally(State current, AgentPath currentPath,Integer regressionIndex) {

        if (current == null) {
            System.out.println("We have reached the root state!");
            currentPath.setSucceeded(false);
            return currentPath;
        }else if (isFinal(current)) {
            currentPath.setSucceeded(true);
            currentPath.addToPath(current);
            return currentPath;
        }
        else if (isRootState(current) && current.allBestStateActionsAreTried()) {
            // if next is blocked etc -
            // try to set next action and revisit.
            // if no next best action -
            // go to parent and try again
            currentPath.setSucceeded(false);
            System.out.println("No further Options - aborting.");
            return currentPath;

        }

        else if (!edgeIsOpened(current)) {
            regressionIndex++;
            State nextStateByRegression = findMinimalStateAmongSiblingsAndParents(current, currentPath, regressionIndex);
            while (
                    (current!=null && (nextStateByRegression == null) )||
                            (nextStateByRegression != null && current != nextStateByRegression)
            ) {
                currentPath.addGoBackState(current);
                if (current.getParentState() != null) {
                    current = current.getParentState();
                } else {
                    break;
                }
            }
            if(nextStateByRegression == null){
                return travelOptimally(nextStateByRegression, currentPath,regressionIndex);
            }

            current = nextStateByRegression;

        }
        current.setAgentVisits();

        currentPath.addToPath(current);

        State nextState = buildNextStt(current);

        return travelOptimally(nextState, currentPath,regressionIndex);

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
        } else if (isFinalStateOrNotAllVisited(nextState)) {
            System.out.println("Going to the next state for current :" + current + " and next:" + nextState + " . noticed they should not be siblings!");
            return travelPath2(nextState, currentPath);
        } else if (!current.allBestStateActionsAreTried()) {
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


    private Boolean isFinalStateOrNotAllVisited(State current) {
        if (current.getAgentLocation().isFinal() || !current.allBestStateActionsAreTried()) {
            return true;
        }

        return false;
    }
    // Check if the next state is at all possible - is the edge blocked?


    private Boolean edgeIsOpened(State current) {
        Action best = this.mdp.getExtendedAction(current);
        return actionIsOpened(best);
    }

    private Boolean actionIsOpened(mdp.ctp.Action best) {

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



