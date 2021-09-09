package mdp.ctp;

import mdp.generic.UtilityCalculator;
import mdp.generic.MDP;
import org.jgrapht.graph.Graph;

public class Strategy {


    /**
     * For subGraph
     *
     * @param gr
     * @param epsilon
     * @param discountFactor
     * @return
     */

    public static MDP constructStrategyFromGraph(Graph gr, Double epsilon, Double discountFactor) {
        MDPFromGraph mdp = new MDPFromGraph(gr);

        UtilityCalculator uc = new UtilityCalculator(mdp, epsilon, discountFactor);
        MDP mdpNew = uc.setOptimalPolicy();
        for (mdp.generic.State s : mdpNew.getStates().values()) {
            System.out.println(s.getId() + ",,," + s.getBestAction());
        }

        return (MDPFromGraph) mdpNew;
    }

    public static MDPFromGraph constructStrategyFromGraph(String url, Double epsilon, Double discountFactor) {

        Graph gr = new Graph("/home/shiris/IdeaProjects/mdpvigeneric/src/main/data/graphs_data/very_basic_mdp_example_graphs/small_graph_81_states.json");

        System.out.println(gr.toString());
        MDPFromGraph mdp = new MDPFromGraph(gr);

        UtilityCalculator uc = new UtilityCalculator(mdp, epsilon, discountFactor);
        MDP mdpNew = uc.setOptimalPolicy();
        for (mdp.generic.State s : mdpNew.getStates().values()) {
            System.out.println(s.getId() + ",,," + s.getBestAction());
        }

        return (MDPFromGraph) mdpNew;
    }
}
