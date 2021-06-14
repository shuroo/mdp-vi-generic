package mdp.ctp;

import ctp.BlockingStatus;
import ctp.CTPEdge;
import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Vertex;

import java.util.HashMap;

public class Action extends mdp.generic.Action {

    private Vertex source;
    private Vertex dest;
    private Edge sourceEdge;

    public Edge getSourceEdge() {
        return sourceEdge;
    }


    public Vertex getSource() {
        return source;
    }

    public Vertex getDest() {
        return dest;
    }

    public static String generateId(Vertex source, Vertex dest) {
        return source.toString() + "_" + dest.toString();
    }

    public Action(Vertex source, Vertex dest) {

        this.source = source;
        this.dest = dest;
        this.actionId = generateId(this.source, this.dest);
    }

    public Action(Edge edge) {

        this.source = edge.getSource();
        this.dest = edge.getDest();
        this.sourceEdge = edge;
        this.actionId = generateId(this.source, this.dest);
    }

    /**
     * Make sure to return only allowed actions by the current state ( - edge is not blocked or unknown ).
     * @param state
     * @param
     * @return
     */
/*
    @Override

        public Boolean actionIsAllowed(final mdp.generic.State state) {

        if(!( state instanceof mdp.ctp.State )){
            return true;
        }
        HashMap<String, CTPEdge> stateStatuses = ((mdp.ctp.State)state).getStatuses();
        return ((CTPEdge)stateStatuses.get(this.getActionId())).getStatus() == BlockingStatus.Opened;
    }
}*/

}