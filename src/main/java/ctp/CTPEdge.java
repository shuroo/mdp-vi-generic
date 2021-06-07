package ctp;

import org.jgrapht.graph.Edge;

/**
 * Edge with Status, to be used later with MDP.
 */

public class CTPEdge {

    private Edge edge;
    private BlockingStatus status;

    public static String generateId(CTPEdge ctpe){  return ctpe.edge.getId()+"_"+ctpe.status.toString(); }

    public Edge getEdge() {
        return edge;
    }

    public String getId() {
        return generateId(this);
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

