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

    public static Map<String, CTPEdge> edgeToMap(Collection<CTPEdge> edges) {
        return edges.stream()
                .collect(Collectors.toMap(CTPEdge::getId, state -> state));
    }

}
