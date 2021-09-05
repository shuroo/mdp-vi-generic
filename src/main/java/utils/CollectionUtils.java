package utils;

import ctp.CTPEdge;
import java.util.*;
import java.util.stream.Collectors;
import mdp.ctp.*;

public class CollectionUtils<T> {


    public Set<T> flattenSet(List<Set<T>> nestedList) {
        Set<T> flatSet = new HashSet<>();
        nestedList.forEach(flatSet::addAll);
        return flatSet;
    }

    // Function to sort map by Key
    public  void sortMapbykeys(HashMap<String,T> stts)
    {
        ArrayList<String> sortedKeys
                = new ArrayList<String>(stts.keySet());

        Collections.sort(sortedKeys);
/*
        // Display the TreeMap which is naturally sorted
        for (String x : sortedKeys)
            System.out.println("Key = " + x
                    + ", Value = " + stts.get(x));*/
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
