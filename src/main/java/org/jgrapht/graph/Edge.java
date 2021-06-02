package org.jgrapht.graph;

public class Edge extends DefaultEdge {

    public Double getReward() {
        return reward;
    }

    private Double reward = 1.0;
    private double blockingProbability = 0;
    private Boolean isBlocked = false;

    public Vertex getSource() {
        return (Vertex)source;
    }

    public String edgeName(){ return this.source+"_"+this.target; }
/*    public Vertex setDest() {
        return this.getDest();
    }

    public void setSource(Vertex s) {
        this.source = s;
    }

    public void setDest(Vertex s) {
        this.target = s;
    }*/
    public Vertex getDest() {
        return (Vertex)target;
    }

    @Override
    public Object getTarget(){
        return target;
    }

    public Edge(){}

    public Edge(Vertex source, Vertex dest, Double prob, Double reward){
        this.source = source;
        this.target = dest;
        this.blockingProbability = prob;
        this.reward = reward;
    }

    public Edge(Edge e){
        this.source = e.source;
        this.target = e.target;
    }
//
//    public  Boolean isBlocked() {
//        return this.isBlocked;
//    }
//
//
//    public void setIsBlocked(Boolean isBlocked){ this.isBlocked = isBlocked;}

    public double getBlockingProbability() {
        return blockingProbability;
    }

    public void setBlockingProbability(Double blockingProbability) {
        this.blockingProbability = blockingProbability;
    }

    public void setReward(Double reward){this.reward = reward;}

    public void setBlockingAndReward(Double blockingProbability , Double reward){
        setBlockingProbability(blockingProbability);
        setReward(reward);
    }

}
