package utils;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.google.gson.Gson;
import mdp.generic.Action;
import mdp.generic.Reward;
import mdp.generic.State;
import mdp.generic.Transition;

public class CouchbaseClient {

    public static Collection getCollection(String bcktName){
        Cluster cluster = Cluster.connect("127.0.0.1", "Administrator", "Administrator");
        Bucket bucket = cluster.bucket(bcktName);
        Collection collection = bucket.defaultCollection();
        return collection;
    }

    public static State getState(String sttId){
        Collection collection = getCollection("state");
        String dbStateId = Constants.stateRecordPrefix+HashUuidCreator.getSha1Uuid(sttId);
        GetResult sttJson = collection.get(dbStateId);
        State stt = sttJson.contentAs(State.class);
        return stt;
    }

    public static void insertState(State s){
        Collection collection = getCollection("state");
        Gson gson = new Gson();
        collection.upsert(Constants.stateRecordPrefix+HashUuidCreator.getSha1Uuid(s.getId()),gson.toJson(s) );
    }

    public static State getExtendedState(String sttId){
        Collection collection = getCollection("state");
        String dbStateId = Constants.extandedStateRecordPrefix+HashUuidCreator.getSha1Uuid(sttId);
        GetResult sttJson = collection.get(dbStateId);
        State stt = sttJson.contentAs(mdp.ctp.State.class);
        return stt;
    }

    public static void insertExtendedState(mdp.ctp.State s){
        Collection collection = getCollection("state");
        Gson gson = new Gson();
        collection.upsert(Constants.extandedStateRecordPrefix+HashUuidCreator.getSha1Uuid(s.getId()),gson.toJson(s) );
    }

    public static Action getAction(String actionId){
        Collection collection = getCollection("action");
        String dbActionId = Constants.actionPrefix+HashUuidCreator.getSha1Uuid(actionId);
        GetResult sttJson = collection.get(dbActionId);
        Action act = sttJson.contentAs(Action.class);
        return act;
    }

    public static void insertAction(Action a){
        Collection collection = getCollection("action");
        Gson gson = new Gson();
        collection.upsert(Constants.actionPrefix+HashUuidCreator.getSha1Uuid(a.getActionId()),gson.toJson(a) );
    }

    public static void insertExtendedAction(Action a){
        Collection collection = getCollection("action");
        Gson gson = new Gson();
        collection.upsert(Constants.extendedActionPrefix+HashUuidCreator.getSha1Uuid(a.getActionId()),gson.toJson(a) );
    }

    public static void insertTransition(Transition t){
        Collection collection = getCollection("transition");
        Gson gson = new Gson();
        collection.upsert(Constants.transitionPrefix+HashUuidCreator.getSha1Uuid(t.getTransitionId()),gson.toJson(t) );
    }

    public static void insertReward(Reward r){
        Collection collection = getCollection("reward");
        Gson gson = new Gson();
        collection.upsert(r.getId(),gson.toJson(r) );
    }
public static void main(String [] args) {

    Cluster cluster = Cluster.connect("127.0.0.1", "Administrator", "Administrator");

    Bucket bucket = cluster.bucket("sample");
    Collection collection = bucket.defaultCollection();

    MutationResult upsertResult = collection.upsert(
            "airbnb_1",
            JsonObject.create().put("name", "Tyler's AirBnB")
                    .put("country", "Canada")
                    .put("type", "hotel")
    );
    // Get a Document
    GetResult getResult = collection.get("airbnb_1");
    System.out.println(getResult);
}

}

