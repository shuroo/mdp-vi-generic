
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

    // todo: in diffStatesToCalcProb
    @Override
    public Boolean isValid(){

        mdp.ctp.Action act = this.extendedAction;
        mdp.ctp.State source = this.extendedSourceState;
        mdp.ctp.State dest = this.extendedDestState;

        // case 0 - Final state is already in the transition source
        if(source.getIsFinal()){
            return false;
        }
        // case 1 - The action doesn't fit
        else if( source.getAgentLocation() != act.getSourceEdge().getSource() ||
                dest.getAgentLocation() != act.getSourceEdge().getDest()
        ){
            return false;
        }



        else  if( source.getAgentLocation() == act.getSourceEdge().getSource()){

            // case 2: th action's edge is blocked

            Edge currentEdg  = act.getSourceEdge();
            for(CTPEdge statusEdge : source.getStatuses().values()){
                if(statusEdge.getId() == currentEdg.getId() && statusEdge.getStatus() == BlockingStatus.Closed){
                    return false;
                }
            }
            // case 3 - a source state is not in status 'unknown'

        for( CTPEdge status : source.getStatuses().values()){
                if(status.getStatus() == BlockingStatus.Unknown){
                    return false;
                }
            }



        // case 4 - the dest status is change to 'unknown':
            for( CTPEdge sourceStatus : source.getStatuses().values()){

                CTPEdge destStatus = dest.getStatuses().get(sourceStatus.getEdge().getId());
                if(destStatus != null && sourceStatus.getStatus() != destStatus.getStatus() && destStatus.getStatus() == BlockingStatus.Unknown){
                    return false;
                }
            }
        }


        return true;
    }
}
