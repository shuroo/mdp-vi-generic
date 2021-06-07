package utils;

import ctp.CTPEdge;
import java.util.*;
import java.util.stream.Collectors;
import mdp.ctp.*;

public class CollectionUtils<T> {


    public List<T> flattenList(List<Set<T>> nestedList) {
        List<T> flatList = new ArrayList<T>();
        nestedList.forEach(flatList::addAll);
        return flatList;
    }

    public static Map<String, State> stateToMap(Collection<State> states) {
        return states.stream()
                .collect(Collectors.toMap(State::getId, state -> state));
    }

    public static HashMap<String, CTPEdge> edgeToMap(Collection<CTPEdge> edges) {
        HashMap<String,CTPEdge> statuses = new HashMap<String,CTPEdge>();
        //        edges.stream().map({edg-> statuses.put(edg)});
        //              //
        edges.stream().forEach(st->{statuses.put(st.getEdge().getId(), st);});
        return statuses;
    }

    public  HashMap<String, T> objToHMap(Collection<T> objs ) {
        HashMap<String,T> results = new HashMap<String,T>();
        objs.stream().forEach(st->{results.put(st.toString(), st);});
        return results;
    }

}
