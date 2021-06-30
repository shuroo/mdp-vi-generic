
package mdp.ctp;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Edge;

public class Transition extends mdp.generic.Transition {

    State extendedSourceState;
    State extendedDestState;
    mdp.ctp.Action extendedAction;

    public State getSource(){
        return extendedSourceState;
    }
    public Double diffStatesToCalcProbability() {
        Double probability = 1.0;
        for (CTPEdge sourceStatus : this.extendedSourceState.getStatuses().values()) {
            CTPEdge destStatus = this.extendedDestState.getStatuses().get(sourceStatus.getEdge().getId());
            if (sourceStatus.getStatus() != destStatus.getStatus()) {
                if (destStatus.getStatus() == BlockingStatus.Closed) {
                    probability = probability * destStatus.getEdge().getBlockingProbability();
                } else if (destStatus.getStatus() == BlockingStatus.Opened) {
                    probability = probability * (1 - destStatus.getEdge().getBlockingProbability());
                }
            }

        }
        return probability;
    }

    public Transition(mdp.ctp.State source, mdp.ctp.State dest, mdp.ctp.Action action) {

        // init super constructor
        super(source, dest, action, 0.0);
        this.extendedSourceState = source;
        this.extendedDestState = dest;
        this.extendedAction = action;
        // set super constructor prob
        this.probability = diffStatesToCalcProbability();

    }
}
