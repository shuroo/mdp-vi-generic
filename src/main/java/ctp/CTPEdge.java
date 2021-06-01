package ctp;

import ctp.BlockingStatus;
import org.jgrapht.graph.Edge;

/**
 * Edge with Status, to be used later with MDP.
 */

public class CTPEdge {

    private Edge edge;
    private BlockingStatus status;


    public Edge getEdge() {
        return edge;
    }

    public String getId() {
        return edge.edgeName();
   }

    public BlockingStatus getStatus() {
        return status;
    }


    public CTPEdge(Edge edge, BlockingStatus status){
        this.edge = edge;
        this.status = status;
    }

    @Override
    public String toString(){
        return  "|"+edge.toString()+"::"+status.toString()+"|";
    }

}

