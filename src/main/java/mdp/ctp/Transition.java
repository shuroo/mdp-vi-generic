package mdp.ctp;

public class Transition extends mdp.generic.Transition {

    public Transition(State source, State dest, Action action, Double probability){

        this.sourceState = source;
        this.destState = dest;
        this.action = action;
        this.probability = probability;

    }
}
