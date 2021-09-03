package mdp.generic;

import utils.Constants;
import utils.HashUuidCreator;

public class Reward {

    private State sourceState;
    private State destState;
    private Action action;

    public State getSourceState() {
        return sourceState;
    }

    public void setSourceState(State sourceState) {
        this.sourceState = sourceState;
    }

    public State getDestState() {
        return destState;
    }

    public void setDestState(State destState) {
        this.destState = destState;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Double getReward() {
        return reward;
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Value:
    private Double reward;

     private String id;

    public Reward(State sourceState, State destState, Action action, Double reward) {
        this.sourceState = sourceState;
        this.destState = destState;
        this.action = action;
        this.reward = reward;
        this.id = generateId();
    }

    private String generateId() {
        return buildId(this.sourceState,this.destState,this.action);
    }

    // deprecated
    public static String buildId(State source, State dest, Action action){

        String baseId = "";
        try{
            baseId = action.toString() + "_" + source.getId() + "_" + dest.getId();

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return Constants.rewardsPrefix+HashUuidCreator.getSha1Uuid(baseId);
    }

    //to be used with cb..
    public static String buildId(String sourceSttId, String destSttId, Action action){

        String baseId = "";
        try{
            baseId = action.toString() + "_" + sourceSttId + "_" + destSttId;

        }
        catch(Exception e){
            e.printStackTrace();
        }
        return Constants.rewardsPrefix+HashUuidCreator.getSha1Uuid(baseId);
    }
}
