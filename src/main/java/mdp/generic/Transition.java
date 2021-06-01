package mdp.generic;

public class Transition {


    private State destState;
    // Value:  0.0 =< prob <= 1.0
    private Double probability;

    private State sourceState;

    private Action action;

    private String transitionId;


    public State getSourceState() {
        return sourceState;
    }

    public State getDestState() {
        return destState;
    }

    public String getTransitionId() {
        return transitionId;
    }

    public Action getAction() {
        return action;
    }

    public Double getProbability() {
        return probability;
    }

    public Transition(State sourceState, State destState, Action action, Double probability) {
        this.sourceState = sourceState;
        this.destState = destState;
        this.action = action;
        this.probability = probability;
        this.transitionId = generateId();
    }

    private String generateId() {
        return buildId(this.action, this.sourceState, this.destState);

    }

    public static String buildId(Action action, State sourceState, State destState) {
        return action.getActionId() + "_dest:" + destState.getId() + "_src:" + sourceState.getId();
    }

}
