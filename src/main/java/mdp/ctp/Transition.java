
package mdp.ctp;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Edge;

public class Transition extends mdp.generic.Transition {

    State extendedSourceState;
    State extendedDestState;
    mdp.ctp.Action extendedAction;

    public State getSource() {
        return extendedSourceState;
    }

    public Double diffStatesToCalcProbability() {
        Double probability = 1.0;
        if (!isValid()) {
            return 0.0;
        }
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

    @Override
    public Boolean isValid() {

        mdp.ctp.Action act = this.extendedAction;
        mdp.ctp.State source = this.extendedSourceState;
        mdp.ctp.State dest = this.extendedDestState;

        // case 3 - The action doesn't fit: The action corresponds to s,s’ agent locations:
        //t.edge = (u,v)  && s.agentLocation == u  && s’.agentLocation == v   .

        if (source.getAgentLocation() != act.getSourceEdge().getSource() ||
                dest.getAgentLocation() != act.getSourceEdge().getDest()
        ) {
            return false;

            // source = u
        }

        // case 1: The relevant action’s transition edge must be in status ‘Opened’:
        //t.action.edge.status = ‘Opened’.

        Edge currentEdg = act.getSourceEdge();
        for (CTPEdge statusEdge : source.getStatuses().values()) {
            if (statusEdge.getId() == currentEdg.getId() && statusEdge.getStatus() == BlockingStatus.Closed) {
                return false;
            }
        }
        //case 2.The transition’s source state s, is not a final vertex ( indicating end of agent’s path ):
        //t.edge = (u,v)  &&  u.isFinal == false.

        if (this.extendedSourceState.getIsFinal()) {
            return false;
        }

        // case 3 - a source state is not in status 'unknown'

        for (CTPEdge status : source.getStatuses().values()) {
            if (status.getStatus() == BlockingStatus.Unknown) {
                return false;
            }
        }
        // case 4: All the edges in s.statuses having edge[i].source= u   are not in status ‘Unknown’
        for (CTPEdge sourceStatus : source.getStatuses().values()) {

            CTPEdge destStatus = dest.getStatuses().get(sourceStatus.getEdge().getId());
            if (destStatus != null && sourceStatus.getStatus() != destStatus.getStatus() && sourceStatus.getStatus() == BlockingStatus.Unknown) {
                return false;
            }
        }

        // case 5 - the dest status is changed to 'unknown':
        for (CTPEdge sourceStatus : source.getStatuses().values()) {

            CTPEdge destStatus = dest.getStatuses().get(sourceStatus.getEdge().getId());
            if (destStatus != null && sourceStatus.getStatus() != destStatus.getStatus() && destStatus.getStatus() == BlockingStatus.Unknown) {
                return false;
            }
        }

        // case 6: Each related edge in s having status ‘Opened’ or ‘Closed’ should have the same corresponding status in s’:
        //	s.statuses =<edge[i],status[i]>&&
        //  s'.statuses =<edge'[j],status'[j]>&&  edge[i] == edge'[j] ==>
        //  if(status[i] == 'Opened') then status'[j] == 'Opened'
        //  if(status[i] == 'Closed') then status'[j] == 'Closed'
        for (CTPEdge sourceStatus : source.getStatuses().values()) {

            CTPEdge destStatus = dest.getStatuses().get(sourceStatus.getEdge().getId());
            if (destStatus != null && sourceStatus.getStatus() != BlockingStatus.Unknown &&
                    destStatus.getStatus() != sourceStatus.getStatus()) {
                return false;
            }
        }

        // case 7: if an edge in s list of statuses has a status ‘Unknown’ and its source is not v,
        // it should remain with status ‘Unknown’ in s’:
        for (CTPEdge sourceStatus : source.getStatuses().values()) {

            CTPEdge destStatus = dest.getStatuses().get(sourceStatus.getEdge().getId());
            if (sourceStatus.getStatus() == BlockingStatus.Unknown &&
                    destStatus.getStatus() != BlockingStatus.Unknown) {
                return false;
            }
        }

        return true;
}

}
