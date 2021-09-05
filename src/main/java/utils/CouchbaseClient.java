package utils;

import com.couchbase.client.java.*;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.query.QueryResult;
import com.google.gson.Gson;
import mdp.generic.Action;
import mdp.generic.Reward;
import mdp.generic.State;
import mdp.generic.Transition;
import org.jgrapht.graph.Vertex;
import reactor.core.publisher.Flux;
import static com.couchbase.client.java.query.QueryOptions.queryOptions;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CouchbaseClient {

    Cluster cluster = null;
    ReactiveCollection statesReactiveCollection = getReactiveCollection("states");

    private Bucket getBucket(String bcktName){
        System.setProperty("com.couchbase.env.timeout.kvTimeout", "3000000s");
        System.setProperty("com.couchbase.env.timeout.queryTimeout", "3000000s");
        cluster = Cluster.connect("127.0.0.1", "Administrator", "Administrator");
        return cluster.bucket(bcktName);
    }

    public ReactiveCollection getReactiveCollection(String bcktName){
        Bucket bucket = getBucket(bcktName);
        return bucket.reactive().defaultCollection();
    }

    public Collection getCollection(String bcktName){
        Bucket bucket = getBucket(bcktName);
        Collection collection = bucket.defaultCollection();
        return collection;
    }

    public State getState(String sttId){
        Collection collection = getCollection("states");
        String dbStateId = Constants.stateRecordPrefix+HashUuidCreator.getSha1Uuid(sttId);
        GetResult sttJson = collection.get(dbStateId);
        State stt = sttJson.contentAs(State.class);
        cluster.disconnect();
        return stt;
    }

    private List<String> bestActionsToJson(List<Action> bestActions){
        return bestActions.stream().map(a->a.getActionId()).collect(Collectors.toList());
    }

    private JsonObject stateToJson(mdp.ctp.State s){
        return JsonObject.create().put("id",s.getId())
                .put("bestAction",(s.getBestAction() != null ? s.getBestAction().getActionId() : null ))
                .put("bestActions",(s.getBestActions() != null ? bestActionsToJson(s.getBestActions()) : new LinkedList() ))
                .put("utility",s.getUtility())
                .put("agentLocation",s.getAgentLocation().toString())
                //.put("statuses",s.getStatuses().toString())
                // todo: add more in the future...
                .put("getInitial",s.getInitial())
                .put("prevUtility",s.getPreviousUtility())
                .put("utility",s.getUtility());

    }
    public void insertState(mdp.ctp.State s){
        Collection collection = getCollection("states");
        JsonObject content = stateToJson(s);
        collection.upsert(Constants.stateRecordPrefix+HashUuidCreator.getSha1Uuid(s.getId()),content );
        cluster.disconnect();
    }

    public void insertBulkStates(Set<mdp.ctp.State> states) {
        List<JsonDocument> jsonStates =
                states.stream().map(st -> new JsonDocument(st.getId(), stateToJson(st))).collect(Collectors.toList());
        ReactiveBucket buc1 = cluster.reactive().bucket("states");
        ReactiveCollection collection1 = buc1.defaultCollection();
        buc1.waitUntilReady(Duration.ofMinutes(2));
        try {
            Flux.fromIterable(jsonStates)
                    .flatMap(docToInsert ->
                            collection1.insert(
                                    Constants.stateRecordPrefix + HashUuidCreator.getSha1Uuid(docToInsert.getId()),
                                    docToInsert.getContent())).doOnError(throwable -> System.out.println(
                    throwable.getMessage())).blockLast();
        } finally {
            cluster.disconnect();
        }
    }
//
//    public static State getExtendedState(String sttId){
//        Collection collection = getCollection("states");
//        String dbStateId = Constants.extandedStateRecordPrefix+HashUuidCreator.getSha1Uuid(sttId);
//        GetResult sttJson = collection.get(dbStateId);
//        State stt = sttJson.contentAs(mdp.ctp.State.class);
//        cluster.disconnect();
//        return stt;
//    }
//
//    public static void insertExtendedState(mdp.ctp.State s){
//        Collection collection = getCollection("state");
//        Gson gson = new Gson();
//        collection.upsert(Constants.extandedStateRecordPrefix+HashUuidCreator.getSha1Uuid(s.getId()),gson.toJson(s) );
//        cluster.disconnect();
//    }
//
//    public static Action getAction(String actionId){
//        Collection collection = getCollection("actions");
//        String dbActionId = Constants.actionPrefix+HashUuidCreator.getSha1Uuid(actionId);
//        GetResult sttJson = collection.get(dbActionId);
//        Action act = sttJson.contentAs(Action.class);
//        cluster.disconnect();
//        return act;
//    }

    public void insertAction(Action a){
        Collection collection = getCollection("actions");
        JsonObject content = JsonObject.create().put("id",a.getActionId())
                .put("utility",a.getUtility());
        collection.upsert(Constants.actionPrefix+HashUuidCreator.getSha1Uuid(a.getActionId()),content );
        cluster.disconnect();
    }
//
//    public static mdp.ctp.Action getExtendedAction(String sttId){
//        Collection collection = getCollection("actions");
//        String dbStateId = Constants.extendedActionPrefix+HashUuidCreator.getSha1Uuid(sttId);
//        GetResult sttJson = collection.get(dbStateId);
//        mdp.ctp.Action stt = sttJson.contentAs(mdp.ctp.Action.class);
//        cluster.disconnect();
//        return stt;
//    }

    public void insertExtendedAction(mdp.ctp.Action a){
        Collection collection = getCollection("actions");
        Gson gson = new Gson();
        JsonObject content = JsonObject.create().put("id",a.getActionId())
                .put("source",a.getSource().toString())
                .put("dest",a.getDest().toString())
                .put("utility",a.getUtility());

        collection.upsert(Constants.extendedActionPrefix+HashUuidCreator.getSha1Uuid(a.getActionId()),content );
        cluster.disconnect();
    }

    public void insertTransition(Transition t){
        Collection collection = getCollection("transitions");
        JsonObject content = JsonObject.create().put("id",t.getTransitionId())
                .put("source",t.getSourceState().getId())
                .put("dest",t.getDestState().getId())
                .put("action",t.getAction().getActionId())
                .put("prob",t.getProbability());
        collection.upsert(Constants.transitionPrefix+HashUuidCreator.getSha1Uuid(t.getTransitionId()),content);
        cluster.disconnect();
    }

    public void insertReward(Reward r){
        Collection collection = getCollection("rewards");
        JsonObject content = JsonObject.create().put("id",r.getId())
                .put("source",r.getSourceState())
                .put("dest",r.getDestState())
                .put("action",r.getAction().getActionId())
                .put("rewardValue",r.getReward());
        collection.upsert(r.getId(),content );
        cluster.disconnect();
    }

    public List<JsonObject> fetchStatesByLocation(Vertex vertex){

        QueryResult result = cluster.query("select meta().id as stateId, * from `states`.`_default`.`_default` data where " +
                "agentLocation=\""+vertex.toString()+"\" order by meta().id offset 0");

        System.out.println("Fetched "+ result.rowsAsObject().size()+" filtered states ");
        return result.rowsAsObject();
    }


    public void main(String [] args) {

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

