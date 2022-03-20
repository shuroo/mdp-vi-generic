package mdp.ctp;

import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Vertex;

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

    private Action(Vertex source, Vertex dest) {

        this.source = source;
        this.dest = dest;
        this.actionId = generateId(this.source, this.dest);
    }

    public void reverseAction(){
       // Seperate display id from id:
        this.unreversedID = generateId(this.dest,this.source);
        this.isReversed = true;
    }

    public Action(Edge edge) {

        this.source = edge.getSource();
        this.dest = edge.getDest();
        this.sourceEdge = edge;
        this.actionId = generateId(this.source, this.dest);
        this.unreversedID = actionId;
    }

}