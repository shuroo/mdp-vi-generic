package utils;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.codec.JsonTranscoder;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.google.gson.Gson;
import mdp.generic.Action;
import mdp.generic.Reward;
import mdp.generic.State;
import mdp.generic.Transition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sun.activation.registries.LogSupport.log;

public class CouchbaseClient {

    private static Bucket getBucket(String bcktName){
        System.setProperty("com.couchbase.env.timeout.kvTimeout", "30000s");
        System.setProperty("com.couchbase.env.timeout.queryTimeout", "30000s");
        Cluster cluster = Cluster.connect("127.0.0.1", "Administrator", "Administrator");
        return cluster.bucket(bcktName);
    }

    public  static ReactiveCollection getReactiveCollection(String bcktName){
        Bucket bucket = getBucket(bcktName);
        return bucket.reactive().defaultCollection();
    }

    public static Collection getCollection(String bcktName){
        Bucket bucket = getBucket(bcktName);
        Collection collection = bucket.defaultCollection();
        return collection;
    }

    public static State getState(String sttId){
        Collection collection = getCollection("states");
        String dbStateId = Constants.stateRecordPrefix+HashUuidCreator.getSha1Uuid(sttId);
        GetResult sttJson = collection.get(dbStateId);
        State stt = sttJson.contentAs(State.class);
        return stt;
    }

    private static List<String> bestActionsToJson(List<Action> bestActions){
        return bestActions.stream().map(a->a.getActionId()).collect(Collectors.toList());
    }

    private static JsonObject stateToJson(State s){
        return JsonObject.create().put("id",s.getId())
                .put("bestAction",(s.getBestAction() != null ? s.getBestAction().getActionId() : null ))
                .put("bestActions",(s.getBestActions() != null ? bestActionsToJson(s.getBestActions()) : new LinkedList() ))
                .put("utility",s.getUtility())
                .put("getInitial",s.getInitial())
                .put("prevUtility",s.getPreviousUtility())
                .put("utility",s.getUtility());

    }
    public static void insertState(State s){
        Collection collection = getCollection("states");
        JsonObject content = stateToJson(s);
        collection.upsert(Constants.stateRecordPrefix+HashUuidCreator.getSha1Uuid(s.getId()),content );
    }

    public static void insertBulkStates(Set<mdp.ctp.State> states){
        List<JsonDocument> jsonStates   = states.stream().map(st-> new JsonDocument(st.getId(),stateToJson(st))).collect(Collectors.toList()) ;
        Flux.fromIterable(jsonStates)
                .flatMap(docToInsert ->
                        getReactiveCollection("states").insert(
                                        Constants.stateRecordPrefix+HashUuidCreator.getSha1Uuid(docToInsert.getId()),
                                docToInsert.getContent())

                                )
                                .blockLast();
//
//        Observable
//                .from(jsonStates)
//                .flatMap(new Func1<JsonDocument, Observable<JsonDocument>>() {
//                    @Override
//                    public Observable<JsonDocument> call(final JsonDocument docToInsert) {
//                        return bucket.async().insert(docToInsert);
//                    }
//                })
//                .last()
//                .toBlocking()
//                .single();
    }

    public static State getExtendedState(String sttId){
        Collection collection = getCollection("states");
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
        Collection collection = getCollection("actions");
        String dbActionId = Constants.actionPrefix+HashUuidCreator.getSha1Uuid(actionId);
        GetResult sttJson = collection.get(dbActionId);
        Action act = sttJson.contentAs(Action.class);
        return act;
    }

    public static void insertAction(Action a){
        Collection collection = getCollection("actions");
        JsonObject content = JsonObject.create().put("id",a.getActionId())
                .put("utility",a.getUtility());
        collection.upsert(Constants.actionPrefix+HashUuidCreator.getSha1Uuid(a.getActionId()),content );
    }

    public static mdp.ctp.Action getExtendedAction(String sttId){
        Collection collection = getCollection("actions");
        String dbStateId = Constants.extendedActionPrefix+HashUuidCreator.getSha1Uuid(sttId);
        GetResult sttJson = collection.get(dbStateId);
        mdp.ctp.Action stt = sttJson.contentAs(mdp.ctp.Action.class);
        return stt;
    }

    public static void insertExtendedAction(mdp.ctp.Action a){
        Collection collection = getCollection("actions");
        Gson gson = new Gson();
        JsonObject content = JsonObject.create().put("id",a.getActionId())
                .put("source",a.getSource().toString())
                        .put("dest",a.getDest().toString())
                .put("utility",a.getUtility());

        collection.upsert(Constants.extendedActionPrefix+HashUuidCreator.getSha1Uuid(a.getActionId()),content );
    }

    public static void insertTransition(Transition t){
        Collection collection = getCollection("transitions");
        JsonObject content = JsonObject.create().put("id",t.getTransitionId())
                .put("source",t.getSourceState().getId())
                .put("dest",t.getDestState().getId())
                .put("action",t.getAction().getActionId())
                .put("prob",t.getProbability());
        collection.upsert(Constants.transitionPrefix+HashUuidCreator.getSha1Uuid(t.getTransitionId()),content);
    }

    public static void insertReward(Reward r){
        Collection collection = getCollection("rewards");
        JsonObject content = JsonObject.create().put("id",r.getId())
                .put("source",r.getSourceState().getId())
                .put("dest",r.getDestState().getId())
                .put("action",r.getAction().getActionId())
                .put("rewardValue",r.getReward());
        collection.upsert(r.getId(),content );
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

