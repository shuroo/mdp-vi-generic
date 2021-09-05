package mdp.generic;

import utils.Constants;
import utils.HashUuidCreator;

public class Reward {

    private String sourceStateId;
    private String destStateId;
    private Action action;

    public String getSourceState() {
        return sourceStateId;
    }

//    public void setSourceState(State sourceState) {
//        this.sourceState = sourceState;
//    }

    public String getDestState() {
        return destStateId;
    }
//
//    public void setDestState(State destState) {
//        this.destState = destState;
//    }

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

    public Reward(String sourceStateId, String destStateId, Action action, Double reward) {
        this.sourceStateId = sourceStateId;
        this.destStateId = destStateId;
        this.action = action;
        this.reward = reward;
        this.id = generateId();
    }

    private String generateId() {
        return buildId(this.sourceStateId,this.destStateId,this.action);
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
