package mdp.ctp;

import org.jgrapht.graph.Edge;
import org.jgrapht.graph.Vertex;

public class Action extends mdp.generic.Action {

    private Vertex source;
    private Vertex dest;

    public Vertex getSource() {
        return source;
    }

    public Vertex getDest() {
        return dest;
    }

    private String generateId(){
        return source.toString()+"_"+dest.toString();
    }

    public Action(Vertex source, Vertex dest){

        this.source = source;
        this.dest = dest;
        this.actionId = generateId();
    }

    public Action(Edge edge){
        new Action(edge.getSource(), edge.getDest());
    }
}
