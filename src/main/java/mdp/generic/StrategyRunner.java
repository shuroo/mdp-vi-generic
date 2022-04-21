package mdp.generic;

import mdp.UtilityCalculator;

import java.util.HashMap;

public class StrategyRunner {

    public static UtilityCalculator buildMDPForMaman17Q1() {


        HashMap<String, State> states = new HashMap<String, State>();

        State kitchen = new State("kitchen", true, false, 0.0);
        states.put(kitchen.getId(), kitchen);
        State dining = new State("diningRoom", true, false, 0.0);
        states.put(dining.getId(), dining);
        State shower = new State("shower", true, false, 0.0);
        states.put(shower.getId(), shower);
        State attacked = new State("underAttack", true, false, 0.0);
        states.put(attacked.getId(), attacked);
        State dead = new State("dead", false, true, 0.0);
        states.put(dead.getId(), dead);
        State sleeping = new State("sleepingRoom", true, false, 0.0);
        states.put(sleeping.getId(), sleeping);

        Action stay = new Action("S");
        Action horizontal = new Action("H");
        Action vertical = new Action("V");
        Action die = new Action("D");

        HashMap<String, Transition> transitions = new HashMap<String, Transition>();

        // shower transitions:
        Transition t1 = new Transition(shower, kitchen, horizontal, 0.6);
        transitions.put(t1.getTransitionId(), t1);
        Transition t2 = new Transition(shower, sleeping, horizontal, 0.4);
        transitions.put(t2.getTransitionId(), t2);
        Transition t3 = new Transition(shower, sleeping, vertical, 0.6);
        transitions.put(t3.getTransitionId(), t3);
        Transition t4 = new Transition(shower, kitchen, vertical, 0.4);
        transitions.put(t4.getTransitionId(), t4);
        Transition t5 = new Transition(shower, shower, stay, 0.75);
        transitions.put(t5.getTransitionId(), t5);
        Transition t6 = new Transition(shower, attacked, vertical, 0.25);
        transitions.put(t6.getTransitionId(), t6);

        // dead \ under attack transitions:
        Transition t7 = new Transition(attacked, dead, die, 1.0);
        transitions.put(t7.getTransitionId(), t7);

        Transition t8 = new Transition(dead, dead, die, 1.0);
        transitions.put(t8.getTransitionId(), t8);

        // kitchen transitions:
        Transition t9 = new Transition(kitchen, shower, horizontal, 0.6);
        transitions.put(t9.getTransitionId(), t9);
        Transition t10 = new Transition(kitchen, dining, horizontal, 0.4);
        transitions.put(t10.getTransitionId(), t10);
        Transition t11 = new Transition(kitchen , shower, vertical, 0.4);
        transitions.put(t11.getTransitionId(), t11);
        Transition t12 = new Transition(kitchen, dining, vertical, 0.6);
        transitions.put(t12.getTransitionId(), t12);
        Transition t13 = new Transition(kitchen, kitchen, stay, 0.75);
        transitions.put(t13.getTransitionId(), t13);
        Transition t14 = new Transition(kitchen, attacked, vertical, 0.25);
        transitions.put(t14.getTransitionId(), t14);

        // kitchen transitions:
        Transition t15 = new Transition(sleeping, shower, horizontal, 0.4);
        transitions.put(t15.getTransitionId(), t15);
        Transition t16 = new Transition(sleeping, dining, horizontal, 0.6);
        transitions.put(t16.getTransitionId(), t16);
        Transition t17 = new Transition(sleeping , shower, vertical, 0.6);
        transitions.put(t17.getTransitionId(), t17);
        Transition t18 = new Transition(sleeping, dining, vertical, 0.4);
        transitions.put(t18.getTransitionId(), t18);
        Transition t19 = new Transition(sleeping, sleeping, stay, 0.75);
        transitions.put(t19.getTransitionId(), t19);
        Transition t20 = new Transition(sleeping, attacked, vertical, 0.25);
        transitions.put(t20.getTransitionId(), t20);


        // dining transitions:
        Transition t21 = new Transition(dining, kitchen, horizontal, 0.6);
        transitions.put(t21.getTransitionId(), t21);
        Transition t22 = new Transition(dining, sleeping, horizontal, 0.4);
        transitions.put(t22.getTransitionId(), t22);
        Transition t23 = new Transition(dining, sleeping, vertical, 0.6);
        transitions.put(t23.getTransitionId(), t23);
        Transition t24 = new Transition(dining, kitchen, vertical, 0.4);
        transitions.put(t24.getTransitionId(), t24);
        Transition t25 = new Transition(dining, dining, stay, 0.75);
        transitions.put(t25.getTransitionId(), t25);
        Transition t26 = new Transition(dining, attacked, vertical, 0.25);
        transitions.put(t26.getTransitionId(), t26);

        HashMap<String, Action> actions = new HashMap<String, Action>();

        actions.put(stay.getActionId(), stay);
        actions.put(horizontal.getActionId(), horizontal);
        actions.put(vertical.getActionId(), vertical);

        HashMap<String, Reward> rewards = new HashMap<String, Reward>();

        Reward reward2 = new Reward(shower, shower, stay, 0.0);
        rewards.put(reward2.getId(), reward2);

        Reward reward3 = new Reward(dining, dining, stay, 0.0);
        rewards.put(reward3.getId(), reward3);

        Reward reward4 = new Reward(dead, dead, die, 0.0);
        rewards.put(reward4.getId(), reward4);

        Reward reward5 = new Reward(shower, attacked, die, -50.0);
        rewards.put(reward5.getId(), reward5);

        Reward reward6 = new Reward(dining, attacked, die, -50.0);
        rewards.put(reward6.getId(), reward6);

        Reward reward7 = new Reward(kitchen, attacked, die, -50.0);
        rewards.put(reward7.getId(), reward7);

        Reward reward8 = new Reward(sleeping, attacked, die, -50.0);
        rewards.put(reward8.getId(), reward8);

        Reward reward1 = new Reward(shower, kitchen, horizontal, 10.0);
        Reward reward30 = new Reward(shower, kitchen, vertical, 10.0);
        Reward reward11 = new Reward(sleeping, kitchen, horizontal, 10.0);
        Reward reward31 = new Reward(sleeping, kitchen, vertical, 10.0);
        Reward reward12 = new Reward(dining, kitchen, horizontal, 10.0);
        Reward reward32 = new Reward(dining, kitchen, vertical, 10.0);

        rewards.put(reward12.getId(), reward12);
        rewards.put(reward32.getId(), reward32);
        rewards.put(reward1.getId(), reward1);
        rewards.put(reward3.getId(), reward3);
        rewards.put(reward30.getId(), reward30);
        rewards.put(reward11.getId(), reward11);
        rewards.put(reward31.getId(), reward31);

        Reward reward = new Reward(kitchen ,shower, horizontal, 4.0);
        Reward reward40 = new Reward(kitchen ,shower, vertical, 4.0);
        Reward reward21 = new Reward(sleeping, shower, horizontal, 4.0);
        Reward reward41 = new Reward(sleeping, shower, vertical, 4.0);
        Reward reward22 = new Reward(dining, shower, horizontal, 4.0);
        Reward reward42 = new Reward(dining, shower, vertical, 4.0);

        rewards.put(reward.getId(), reward);
        rewards.put(reward40.getId(), reward40);
        rewards.put(reward21.getId(), reward21);
        rewards.put(reward41.getId(), reward41);
        rewards.put(reward22.getId(), reward22);
        rewards.put(reward42.getId(), reward42);

        Reward rewardy = new Reward(kitchen ,dining, horizontal, 2.0);
        Reward rewardy40 = new Reward(kitchen ,dining, vertical, 2.0);
        Reward rewardy21 = new Reward(sleeping, dining, horizontal, 2.0);
        Reward rewardy41 = new Reward(sleeping, dining, vertical, 2.0);
        Reward rewardy22 = new Reward(shower, dining, horizontal, 2.0);
        Reward rewardy42 = new Reward(shower, dining, vertical, 2.0);

        rewards.put(rewardy.getId(), rewardy);
        rewards.put(rewardy40.getId(), rewardy40);
        rewards.put(rewardy21.getId(), rewardy21);
        rewards.put(rewardy41.getId(), rewardy41);
        rewards.put(rewardy22.getId(), rewardy22);
        rewards.put(rewardy42.getId(), rewardy42);

        Reward rewardz = new Reward(kitchen ,sleeping, horizontal, 0.0);
        Reward rewardz40 = new Reward(kitchen ,sleeping, vertical, 0.0);
        Reward rewardz21 = new Reward(dining, sleeping, horizontal, 0.0);
        Reward rewardz41 = new Reward(dining, sleeping, vertical, 0.0);
        Reward rewardz22 = new Reward(shower, sleeping, horizontal, 0.0);
        Reward rewardz42 = new Reward(shower, sleeping, vertical, 0.0);

        rewards.put(rewardz.getId(), rewardz);
        rewards.put(rewardz40.getId(), rewardz40);
        rewards.put(rewardz21.getId(), rewardz21);
        rewards.put(rewardz41.getId(), rewardz41);
        rewards.put(rewardz22.getId(), rewardz22);
        rewards.put(rewardz42.getId(), rewardz42);

        MDP mdp = new MDP(transitions, actions, states, rewards, false);

        Double epsilon = 0.006;
        Double discountFactor = 0.5;
        UtilityCalculator uc = new UtilityCalculator(mdp,epsilon,discountFactor);

        return uc;

    }

        /**
         * Implementation of example from:
         *
         * https://people.eecs.berkeley.edu/~pabbeel/cs287-fa12/slides/mdps-exact-methods.pdf
         *
         * STATUS: TESTED, WORKING!!!
         *
         * @return
         */
    public static UtilityCalculator buildMDPAndExactSolutionMethodsExample(){


        HashMap<String, State> states = new HashMap<String, State>();

        State s11 = new State("pos_1_1", true, false, 0.0);
        states.put(s11.getId(), s11);
        State s12 = new State("pos_1_2", false, false, 0.0);
        states.put(s12.getId(), s12);
        State s13 = new State("pos_1_3", false, false, 0.0);
        states.put(s13.getId(), s13);
        State s14 = new State("pos_1_4", false, false, 0.0);
        states.put(s14.getId(), s14);
        State s21 = new State("pos_2_1", false, false, 0.0);
        states.put(s21.getId(), s21);
        State s22 = new State("pos_2_2", false, false, 0.0);
        states.put(s22.getId(), s22);
        State s23 = new State("pos_2_3", false, false, 0.0);
        states.put(s23.getId(), s23);
        State s24 = new State("pos_2_4", false, false, -1.0);
        states.put(s24.getId(), s24);
        State s31 = new State("pos_3_1", false, false, 0.0);
        states.put(s31.getId(), s31);
        State s32 = new State("pos_3_2", false, false, 0.0);
        states.put(s32.getId(), s32);
        State s33 = new State("pos_3_3", false, false, 0.0);
        states.put(s33.getId(), s33);
        State s34 = new State("pos_3_4", false, true, 1.0);
        states.put(s34.getId(), s34);

        Action up = new Action("up");
        Action left = new Action("left");
        Action right = new Action("right");
        //Action down = new Action("down");

        HashMap<String, Transition> transitions = new HashMap<String, Transition>();

        // s11 transitions:
        Transition t1 = new Transition(s11, s12, right, 0.8);
        transitions.put(t1.getTransitionId(), t1);
        Transition t2 = new Transition(s11, s21, right, 0.1);
        transitions.put(t2.getTransitionId(), t2);
        Transition t3 = new Transition(s11, s11, right, 0.1);
        transitions.put(t3.getTransitionId(), t3);
        Transition t4 = new Transition(s11, s21, up, 0.8);
        transitions.put(t4.getTransitionId(), t4);
        Transition t5 = new Transition(s11, s12, up, 0.1);
        transitions.put(t5.getTransitionId(), t5);
        Transition t6 = new Transition(s11, s11, up, 0.1);
        transitions.put(t6.getTransitionId(), t6);
        Transition t7 = new Transition(s11, s11, left, 0.9);
        transitions.put(t7.getTransitionId(), t7);
        Transition t8 = new Transition(s11, s21, left, 0.1);
        transitions.put(t8.getTransitionId(), t8);

        // s12 transitions:
        Transition t10 = new Transition(s12, s13, right, 0.8);
        transitions.put(t10.getTransitionId(), t10);
        Transition t12 = new Transition(s12, s12, right, 0.2);
        transitions.put(t12.getTransitionId(), t12);
        Transition t13 = new Transition(s12, s12, up, 0.8);
        transitions.put(t13.getTransitionId(), t13);
        Transition t14 = new Transition(s12, s13, up, 0.1);
        transitions.put(t14.getTransitionId(), t14);
        Transition t15 = new Transition(s12, s11, up, 0.1);
        transitions.put(t15.getTransitionId(), t15);
        Transition t16 = new Transition(s12, s11, left, 0.8);
        transitions.put(t16.getTransitionId(), t16);
        Transition t18 = new Transition(s12, s12, left, 0.2);
        transitions.put(t18.getTransitionId(), t18);

        // s13 transitions:
        Transition t19 = new Transition(s13, s14, right, 0.8);
        transitions.put(t19.getTransitionId(), t19);
        Transition t20 = new Transition(s13, s23, right, 0.1);
        transitions.put(t20.getTransitionId(), t20);
        Transition t21 = new Transition(s13, s13, right, 0.1);
        transitions.put(t21.getTransitionId(), t21);
        Transition t22 = new Transition(s13, s23, up, 0.8);
        transitions.put(t22.getTransitionId(), t22);
        Transition t23 = new Transition(s13, s14, up, 0.1);
        transitions.put(t23.getTransitionId(), t23);
        Transition t24 = new Transition(s13, s12, up, 0.1);
        transitions.put(t24.getTransitionId(), t24);
        Transition t25 = new Transition(s13, s12, left, 0.8);
        transitions.put(t25.getTransitionId(), t25);
        Transition t26 = new Transition(s13, s23, left, 0.1);
        transitions.put(t26.getTransitionId(), t26);
        Transition t27 = new Transition(s13, s13, left, 0.1);
        transitions.put(t27.getTransitionId(), t27);


        // s14 transitions:
        Transition t28 = new Transition(s14, s14, right, 0.8);
        transitions.put(t28.getTransitionId(), t28);
        Transition t29 = new Transition(s14, s24, right, 0.1);
        transitions.put(t29.getTransitionId(), t29);
        Transition t30 = new Transition(s14, s13, right, 0.1);
        transitions.put(t30.getTransitionId(), t30);
        Transition t31 = new Transition(s14, s24, up, 0.8);
        transitions.put(t31.getTransitionId(), t31);
        Transition t32 = new Transition(s14, s14, up, 0.1);
        transitions.put(t32.getTransitionId(), t32);
        Transition t33 = new Transition(s14, s13, up, 0.1);
        transitions.put(t33.getTransitionId(), t33);
        Transition t34 = new Transition(s14, s13, left, 0.8);
        transitions.put(t34.getTransitionId(), t34);
        Transition t35 = new Transition(s14, s24, left, 0.1);
        transitions.put(t35.getTransitionId(), t35);
        Transition t36 = new Transition(s14, s14, left, 0.1);
        transitions.put(t36.getTransitionId(), t36);


        // s21 transitions:
        Transition t37 = new Transition(s21, s21, right, 0.9);
        transitions.put(t37.getTransitionId(), t37);
        Transition t38 = new Transition(s21, s31, right, 0.1);
        transitions.put(t38.getTransitionId(), t38);
        Transition t40 = new Transition(s21, s31, up, 0.8);
        transitions.put(t40.getTransitionId(), t40);
        Transition t41 = new Transition(s21, s21, up, 0.2);
        transitions.put(t41.getTransitionId(), t41);
        Transition t43 = new Transition(s21, s21, left, 0.9);
        transitions.put(t43.getTransitionId(), t43);
        Transition t44 = new Transition(s21, s31, left, 0.1);
        transitions.put(t44.getTransitionId(), t44);

        // s23 transitions:
        Transition t49 = new Transition(s23, s24, right, 0.8);
        transitions.put(t49.getTransitionId(), t49);
        Transition t50 = new Transition(s23, s23, right, 0.1);
        transitions.put(t50.getTransitionId(), t50);
        Transition t56 = new Transition(s23, s33, right, 0.1);
        transitions.put(t56.getTransitionId(), t56);
        Transition t51 = new Transition(s23, s33, up, 0.8);
        transitions.put(t51.getTransitionId(), t51);
        Transition t52 = new Transition(s23, s24, up, 0.1);
        transitions.put(t52.getTransitionId(), t52);
        Transition t53 = new Transition(s23, s23, up, 0.1);
        transitions.put(t53.getTransitionId(), t53);
        Transition t54 = new Transition(s23, s23, left, 0.9);
        transitions.put(t54.getTransitionId(), t54);
        Transition t55 = new Transition(s23, s13, left, 0.1);
        transitions.put(t55.getTransitionId(), t55);
        Transition t57 = new Transition(s23, s33, left, 0.1);
        transitions.put(t57.getTransitionId(), t57);
//
//        // s24 transitions:

        ////// ------   LAST LINE:  --------

        // s31 transitions:
        Transition t73 = new Transition(s31, s32, right, 0.8);
        transitions.put(t73.getTransitionId(), t73);
        Transition t74 = new Transition(s31, s31, right, 0.2);
        transitions.put(t74.getTransitionId(), t74);
        Transition t75 = new Transition(s31, s31, up, 0.9);
        transitions.put(t75.getTransitionId(), t75);
        Transition t76 = new Transition(s31, s32, up, 0.1);
        transitions.put(t76.getTransitionId(), t76);
        Transition t77 = new Transition(s31, s31, left, 0.9);
        transitions.put(t77.getTransitionId(), t77);
        Transition t78 = new Transition(s31, s21, left, 0.1);
        transitions.put(t78.getTransitionId(), t78);

        // s32 transitions:
        Transition t106 = new Transition(s32, s33, right, 0.8);
        transitions.put(t106.getTransitionId(), t106);
        Transition t107 = new Transition(s32, s32, right, 0.2);
        transitions.put(t107.getTransitionId(), t107);

        // todo: "up" and "down" can be removed.
        Transition t108 = new Transition(s32, s32, up, 0.8);
        transitions.put(t108.getTransitionId(), t108);
        Transition t109 = new Transition(s32, s33, up, 0.1);
        transitions.put(t109.getTransitionId(), t109);
        Transition t110 = new Transition(s32, s31, up, 0.1);
        transitions.put(t110.getTransitionId(), t110);
        Transition t111 = new Transition(s32, s31, left, 0.8);
        transitions.put(t111.getTransitionId(), t111);
        Transition t112 = new Transition(s32, s32, left, 0.2);
        transitions.put(t112.getTransitionId(), t112);

        // s33 transitions:
        Transition t83 = new Transition(s33, s34, right, 0.8);
        transitions.put(t83.getTransitionId(), t83);
        Transition t84 = new Transition(s33, s33, right, 0.1);
        transitions.put(t84.getTransitionId(), t84);
        Transition t85 = new Transition(s33, s23, right, 0.1);
        transitions.put(t85.getTransitionId(), t85);
        Transition t86 = new Transition(s33, s33, up, 0.8);
        transitions.put(t86.getTransitionId(), t86);
        Transition t87 = new Transition(s33, s34, up, 0.1);
        transitions.put(t87.getTransitionId(), t87);
        Transition t88 = new Transition(s33, s32, up, 0.1);
        transitions.put(t88.getTransitionId(), t88);
        Transition t89 = new Transition(s33, s32, left, 0.8);
        transitions.put(t89.getTransitionId(), t89);
        Transition t90 = new Transition(s33, s23, left, 0.1);
        transitions.put(t90.getTransitionId(), t90);
        Transition t91 = new Transition(s33, s33, left, 0.1);
        transitions.put(t91.getTransitionId(), t91);

        // s34 transitions:
        //

        HashMap<String, Action> actions = new HashMap<String, Action>();

        actions.put(up.getActionId(), up);
        actions.put(left.getActionId(), left);
        actions.put(right.getActionId(), right);

        HashMap<String, Reward> rewards = new HashMap<String, Reward>();

        for (Action action : actions.values()) {

            for (State s1 : states.values()) {

                for (State s2 : states.values()) {
                    Reward reward = new Reward(s1, s2, action, 0.0);
                    rewards.put(reward.getId(), reward);
                }
            }
        }


        MDP mdp = new MDP(transitions, actions, states, rewards, false);

        Double epsilon = 0.006;
        Double discountFactor = 0.9;
        UtilityCalculator uc = new UtilityCalculator(mdp,epsilon,discountFactor);

        return uc;
    }

    /**
     * Implementation of example 17.2 in the book.
     * @return
     */

    public static UtilityCalculator buildBookExample(){


        HashMap<String, State> states = new HashMap<String, State>();

        State s11 = new State("pos_1_1", true, false, 0.0);
        states.put(s11.getId(), s11);
        State s12 = new State("pos_1_2", false, false, 0.0);
        states.put(s12.getId(), s12);
        State s13 = new State("pos_1_3", false, false, 0.0);
        states.put(s13.getId(), s13);
        State s14 = new State("pos_1_4", false, false, 0.0);
        states.put(s14.getId(), s14);
        State s21 = new State("pos_2_1", false, false, 0.0);
        states.put(s21.getId(), s21);
        State s22 = new State("pos_2_2", false, false, 0.0);
        states.put(s22.getId(), s22);
        State s23 = new State("pos_2_3", false, false, 0.0);
        states.put(s23.getId(), s23);
        State s24 = new State("pos_2_4", false, false, -1.0);
        states.put(s24.getId(), s24);
        State s31 = new State("pos_3_1", false, false, 0.0);
        states.put(s31.getId(), s31);
        State s32 = new State("pos_3_2", false, false, 0.0);
        states.put(s32.getId(), s32);
        State s33 = new State("pos_3_3", false, false, 0.0);
        states.put(s33.getId(), s33);
        State s34 = new State("pos_3_4", false, true, 1.0);
        states.put(s34.getId(), s34);

        Action up = new Action("up");
        Action left = new Action("left");
        Action right = new Action("right");
        Action down = new Action("down");

        HashMap<String, Transition> transitions = new HashMap<String, Transition>();

        // s11 transitions:
        Transition t1 = new Transition(s11, s12, right, 0.8);
        transitions.put(t1.getTransitionId(), t1);
        Transition t2 = new Transition(s11, s21, right, 0.1);
        transitions.put(t2.getTransitionId(), t2);
        Transition t3 = new Transition(s11, s11, right, 0.1);
        transitions.put(t3.getTransitionId(), t3);
        Transition t4 = new Transition(s11, s21, up, 0.8);
        transitions.put(t4.getTransitionId(), t4);
        Transition t5 = new Transition(s11, s12, up, 0.1);
        transitions.put(t5.getTransitionId(), t5);
        Transition t6 = new Transition(s11, s11, up, 0.1);
        transitions.put(t6.getTransitionId(), t6);
        Transition t7 = new Transition(s11, s11, left, 0.9);
        transitions.put(t7.getTransitionId(), t7);
        Transition t8 = new Transition(s11, s21, left, 0.1);
        transitions.put(t8.getTransitionId(), t8);

        // s12 transitions:
        Transition t10 = new Transition(s12, s13, right, 0.8);
        transitions.put(t10.getTransitionId(), t10);
        Transition t12 = new Transition(s12, s12, right, 0.2);
        transitions.put(t12.getTransitionId(), t12);
        Transition t13 = new Transition(s12, s12, up, 0.8);
        transitions.put(t13.getTransitionId(), t13);
        Transition t14 = new Transition(s12, s13, up, 0.1);
        transitions.put(t14.getTransitionId(), t14);
        Transition t15 = new Transition(s12, s11, up, 0.1);
        transitions.put(t15.getTransitionId(), t15);
        Transition t16 = new Transition(s12, s11, left, 0.8);
        transitions.put(t16.getTransitionId(), t16);
        Transition t18 = new Transition(s12, s12, left, 0.2);
        transitions.put(t18.getTransitionId(), t18);

        // s13 transitions:
        Transition t19 = new Transition(s13, s14, right, 0.8);
        transitions.put(t19.getTransitionId(), t19);
        Transition t20 = new Transition(s13, s23, right, 0.1);
        transitions.put(t20.getTransitionId(), t20);
        Transition t21 = new Transition(s13, s13, right, 0.1);
        transitions.put(t21.getTransitionId(), t21);
        Transition t22 = new Transition(s13, s23, up, 0.8);
        transitions.put(t22.getTransitionId(), t22);
        Transition t23 = new Transition(s13, s14, up, 0.1);
        transitions.put(t23.getTransitionId(), t23);
        Transition t24 = new Transition(s13, s12, up, 0.1);
        transitions.put(t24.getTransitionId(), t24);
        Transition t25 = new Transition(s13, s12, left, 0.8);
        transitions.put(t25.getTransitionId(), t25);
        Transition t26 = new Transition(s13, s23, left, 0.1);
        transitions.put(t26.getTransitionId(), t26);
        Transition t27 = new Transition(s13, s13, left, 0.1);
        transitions.put(t27.getTransitionId(), t27);


        // s14 transitions:
        Transition t28 = new Transition(s14, s14, right, 0.8);
        transitions.put(t28.getTransitionId(), t28);
        Transition t29 = new Transition(s14, s24, right, 0.1);
        transitions.put(t29.getTransitionId(), t29);
        Transition t30 = new Transition(s14, s13, right, 0.1);
        transitions.put(t30.getTransitionId(), t30);
        Transition t31 = new Transition(s14, s24, up, 0.8);
        transitions.put(t31.getTransitionId(), t31);
        Transition t32 = new Transition(s14, s14, up, 0.1);
        transitions.put(t32.getTransitionId(), t32);
        Transition t33 = new Transition(s14, s13, up, 0.1);
        transitions.put(t33.getTransitionId(), t33);
        Transition t34 = new Transition(s14, s13, left, 0.8);
        transitions.put(t34.getTransitionId(), t34);
        Transition t35 = new Transition(s14, s24, left, 0.1);
        transitions.put(t35.getTransitionId(), t35);
        Transition t36 = new Transition(s14, s14, left, 0.1);
        transitions.put(t36.getTransitionId(), t36);


        // s21 transitions:
        Transition t37 = new Transition(s21, s21, right, 0.9);
        transitions.put(t37.getTransitionId(), t37);
        Transition t38 = new Transition(s21, s31, right, 0.1);
        transitions.put(t38.getTransitionId(), t38);
        Transition t40 = new Transition(s21, s31, up, 0.8);
        transitions.put(t40.getTransitionId(), t40);
        Transition t41 = new Transition(s21, s21, up, 0.2);
        transitions.put(t41.getTransitionId(), t41);
        Transition t43 = new Transition(s21, s21, left, 0.9);
        transitions.put(t43.getTransitionId(), t43);
        Transition t44 = new Transition(s21, s31, left, 0.1);
        transitions.put(t44.getTransitionId(), t44);

        Transition t46 = new Transition(s21, s11, down, 0.8);
        transitions.put(t46.getTransitionId(), t46);
        Transition t47 = new Transition(s21, s21, down, 0.2);
        transitions.put(t47.getTransitionId(), t47);

        // s23 transitions:
        Transition t49 = new Transition(s23, s24, right, 0.8);
        transitions.put(t49.getTransitionId(), t49);
        Transition t50 = new Transition(s23, s23, right, 0.1);
        transitions.put(t50.getTransitionId(), t50);
        Transition t56 = new Transition(s23, s33, right, 0.1);
        transitions.put(t56.getTransitionId(), t56);
        Transition t51 = new Transition(s23, s33, up, 0.8);
        transitions.put(t51.getTransitionId(), t51);
        Transition t52 = new Transition(s23, s24, up, 0.1);
        transitions.put(t52.getTransitionId(), t52);
        Transition t53 = new Transition(s23, s23, up, 0.1);
        transitions.put(t53.getTransitionId(), t53);
        Transition t54 = new Transition(s23, s23, left, 0.9);
        transitions.put(t54.getTransitionId(), t54);
        Transition t55 = new Transition(s23, s13, left, 0.1);
        transitions.put(t55.getTransitionId(), t55);
        Transition t57 = new Transition(s23, s33, left, 0.1);
        transitions.put(t57.getTransitionId(), t57);
        Transition t58 = new Transition(s23, s13, down, 0.8);
        transitions.put(t58.getTransitionId(), t58);
        Transition t59 = new Transition(s23, s23, down, 0.1);
        transitions.put(t59.getTransitionId(), t59);
        Transition t60 = new Transition(s23, s24, down, 0.1);
        transitions.put(t60.getTransitionId(), t60);
//
//        // s24 transitions:

        ////// ------   LAST LINE:  --------

        // s31 transitions:
        Transition t73 = new Transition(s31, s32, right, 0.8);
        transitions.put(t73.getTransitionId(), t73);
        Transition t74 = new Transition(s31, s31, right, 0.2);
        transitions.put(t74.getTransitionId(), t74);
        Transition t75 = new Transition(s31, s31, up, 0.9);
        transitions.put(t75.getTransitionId(), t75);
        Transition t76 = new Transition(s31, s32, up, 0.1);
        transitions.put(t76.getTransitionId(), t76);
        Transition t77 = new Transition(s31, s31, left, 0.9);
        transitions.put(t77.getTransitionId(), t77);
        Transition t78 = new Transition(s31, s21, left, 0.1);
        transitions.put(t78.getTransitionId(), t78);

        Transition t80 = new Transition(s31, s21, down, 0.8);
        transitions.put(t80.getTransitionId(), t80);
        Transition t81 = new Transition(s31, s31, down, 0.1);
        transitions.put(t81.getTransitionId(), t81);
        Transition t82 = new Transition(s31, s32, down, 0.1);
        transitions.put(t82.getTransitionId(), t82);


        // s32 transitions:
        Transition t106 = new Transition(s32, s33, right, 0.8);
        transitions.put(t106.getTransitionId(), t106);
        Transition t107 = new Transition(s32, s32, right, 0.2);
        transitions.put(t107.getTransitionId(), t107);

        // todo: "up" and "down" can be removed.
        Transition t108 = new Transition(s32, s32, up, 0.8);
        transitions.put(t108.getTransitionId(), t108);
        Transition t109 = new Transition(s32, s33, up, 0.1);
        transitions.put(t109.getTransitionId(), t109);
        Transition t110 = new Transition(s32, s31, up, 0.1);
        transitions.put(t110.getTransitionId(), t110);
        Transition t111 = new Transition(s32, s31, left, 0.8);
        transitions.put(t111.getTransitionId(), t111);
        Transition t112 = new Transition(s32, s32, left, 0.2);
        transitions.put(t112.getTransitionId(), t112);

        Transition t113 = new Transition(s32, s32, down, 0.8);
        transitions.put(t113.getTransitionId(), t113);
        Transition t114 = new Transition(s32, s33, down, 0.1);
        transitions.put(t114.getTransitionId(), t114);
        Transition t115 = new Transition(s32, s31, down, 0.1);
        transitions.put(t115.getTransitionId(), t115);

        // s33 transitions:
        Transition t83 = new Transition(s33, s34, right, 0.8);
        transitions.put(t83.getTransitionId(), t83);
        Transition t84 = new Transition(s33, s33, right, 0.1);
        transitions.put(t84.getTransitionId(), t84);
        Transition t85 = new Transition(s33, s23, right, 0.1);
        transitions.put(t85.getTransitionId(), t85);
        Transition t86 = new Transition(s33, s33, up, 0.8);
        transitions.put(t86.getTransitionId(), t86);
        Transition t87 = new Transition(s33, s34, up, 0.1);
        transitions.put(t87.getTransitionId(), t87);
        Transition t88 = new Transition(s33, s32, up, 0.1);
        transitions.put(t88.getTransitionId(), t88);
        Transition t89 = new Transition(s33, s32, left, 0.8);
        transitions.put(t89.getTransitionId(), t89);
        Transition t90 = new Transition(s33, s23, left, 0.1);
        transitions.put(t90.getTransitionId(), t90);
        Transition t91 = new Transition(s33, s33, left, 0.1);
        transitions.put(t91.getTransitionId(), t91);
        Transition t92 = new Transition(s33, s23, down, 0.8);
        transitions.put(t92.getTransitionId(), t92);
        Transition t93 = new Transition(s33, s31, down, 0.1);
        transitions.put(t93.getTransitionId(), t93);
        Transition t94 = new Transition(s33, s34, down, 0.1);
        transitions.put(t94.getTransitionId(), t94);

        // s34 transitions:
        //

        HashMap<String, Action> actions = new HashMap<String, Action>();

        actions.put(up.getActionId(), up);
        actions.put(left.getActionId(), left);
        actions.put(down.getActionId(), down);
        actions.put(right.getActionId(), right);

        HashMap<String, Reward> rewards = new HashMap<String, Reward>();

        for (Action action : actions.values()) {

            for (State s1 : states.values()) {

                for (State s2 : states.values()) {
                    Reward reward = new Reward(s1, s2, action, -0.04);
                    rewards.put(reward.getId(), reward);
                }
            }
        }


        MDP mdp = new MDP(transitions, actions, states, rewards, false);

        Double epsilon = 0.2;
        Double discountFactor = 0.98;
        UtilityCalculator uc = new UtilityCalculator(mdp,epsilon,discountFactor);

        return uc;
    }


    /**
     * Implementing example from :
     * STATUS: NOT TESTED!!!
     * https://wormtooth.com/20180207-markov-decision-process/
     *
     */

    public static UtilityCalculator buildAnotherExample(){


        HashMap<String, State> states = new HashMap<String, State>();

        State s00 = new State("pos_0_0", true, false, 0.0);
        states.put(s00.getId(), s00);
        State s01 = new State("pos_0_1", false, false, 0.0);
        states.put(s01.getId(), s01);
        State s02 = new State("pos_0_2", false, false, 0.0);
        states.put(s02.getId(), s02);
        State s03 = new State("pos_0_3", false, false, 1.0);
        states.put(s03.getId(), s03);
        State s10 = new State("pos_1_0", false, false, 0.0);
        states.put(s10.getId(), s10);
        State s12 = new State("pos_1_2", false, false, 0.0);
        states.put(s12.getId(), s12);
        State s13 = new State("pos_1_3", false, false, -1.0);
        states.put(s13.getId(), s13);
        State s20 = new State("pos_2_0", false, false, 0.0);
        states.put(s20.getId(), s20);
        State s21 = new State("pos_2_1", false, false, 0.0);
        states.put(s21.getId(), s21);
        State s22 = new State("pos_2_2", false, false, 0.0);
        states.put(s22.getId(), s22);
        State s23 = new State("pos_2_3", false, false, 0.0);
        states.put(s23.getId(), s23);

        Action up = new Action("up");
        Action left = new Action("left");
        Action right = new Action("right");
        Action down = new Action("down");

        HashMap<String, Transition> transitions = new HashMap<String, Transition>();

        // # Policy pi - the agent starts at position 2,0 and goes to position (2,2)
        //pi = {(0, 0): 'R', (0, 1): 'R', (0, 2): 'R', (0, 3): '#',
        //      (1, 0): 'D', (1, 1): '#', (1, 2): 'R', (1, 3): '#',
        //      (2, 0): 'R', (2, 1): 'R', (2, 2): 'U', (2, 3): 'U'}
        //

        // (2, 0): 'R'
        Transition t000 = new Transition(s20, s21, right, 0.8);
        transitions.put(t000.getTransitionId(), t000);

        Transition t001 = new Transition(s20, s10, up, 0.1);
        transitions.put(t001.getTransitionId(), t001);

        Transition t002 = new Transition(s20, s20, down, 0.1);
        transitions.put(t002.getTransitionId(), t002);

        // (2, 1): 'R'
        Transition t010 = new Transition(s21, s22, right, 0.8);
        transitions.put(t010.getTransitionId(), t010);

        // staying in place - can't go up.
        Transition t011 = new Transition(s21, s21, up, 0.1);
        transitions.put(t011.getTransitionId(), t011);

        Transition t012 = new Transition(s21, s21, down, 0.1);
        transitions.put(t012.getTransitionId(), t012);

        // (2, 2): 'U'
        Transition t020 = new Transition(s22, s12, up, 0.8);
        transitions.put(t020.getTransitionId(), t020);

        // staying in place - can't go up.
        Transition t021 = new Transition(s22, s23, right, 0.1);
        transitions.put(t021.getTransitionId(), t021);

        // staying in place - can't go up.
        Transition t029 = new Transition(s22, s21, left, 0.1);
        transitions.put(t029.getTransitionId(), t029);

        // (2, 3): 'U'
        Transition t230 = new Transition(s23, s13, up, 0.8);
        transitions.put(t230.getTransitionId(), t230);

        // staying in place - can't go up.
        Transition t231 = new Transition(s23, s23, right, 0.1);
        transitions.put(t231.getTransitionId(), t231);

        // staying in place - can't go up.
        Transition t232 = new Transition(s23, s22, left, 0.1);
        transitions.put(t232.getTransitionId(), t232);

        /////////////////

        // (1, 0): 'D',
//        Transition t100 = new Transition(s10, s00, up, 0.8);
//        transitions.put(t100.getTransitionId(), t100);

        Transition t100 = new Transition(s10, s20, down, 0.8);
        transitions.put(t100.getTransitionId(), t100);

        Transition t101 = new Transition(s10, s10, right, 0.1);
        transitions.put(t101.getTransitionId(), t101);

        Transition t102 = new Transition(s10, s10, left, 0.1);
        transitions.put(t102.getTransitionId(), t102);

        // (1, 2): 'R'
        Transition t120 = new Transition(s12, s13, right, 0.8);
        transitions.put(t120.getTransitionId(), t120);

        // staying in place - can't go up.
        Transition t121 = new Transition(s12, s22, down, 0.1);
        transitions.put(t121.getTransitionId(), t121);

        Transition t122 = new Transition(s12, s02, up, 0.1);
        transitions.put(t122.getTransitionId(), t122);

       // (1, 3): '#',
//        Transition t130 = new Transition(s13, s03, up, 0.8);
//        transitions.put(t130.getTransitionId(), t130);
//
//        Transition t131 = new Transition(s13, s13, right, 0.1);
//        transitions.put(t131.getTransitionId(), t131);

        /////////////////

        // (0, 0): 'R'

        Transition t200 = new Transition(s00, s01, right, 0.8);
        transitions.put(t200.getTransitionId(), t200);

        Transition t201 = new Transition(s00, s10, down, 0.1);
        transitions.put(t201.getTransitionId(), t201);

        Transition t202 = new Transition(s00, s00, up, 0.1);
        transitions.put(t202.getTransitionId(), t202);

        // (0, 1): 'R'
        Transition t210 = new Transition(s01, s02, right, 0.8);
        transitions.put(t210.getTransitionId(), t210);

        // todo: split this to up and down as well?

        Transition t211 = new Transition(s01, s01, up, 0.1);
        transitions.put(t211.getTransitionId(), t211);

        Transition t212 = new Transition(s01, s01, down, 0.1);
        transitions.put(t212.getTransitionId(), t212);

        // (0, 2): 'R',
        Transition t220 = new Transition(s02, s03, right, 0.8);
        transitions.put(t220.getTransitionId(), t220);

        Transition t221 = new Transition(s02, s12, down, 0.1);
        transitions.put(t221.getTransitionId(), t221);

        Transition t222 = new Transition(s02, s02, up, 0.1);
        transitions.put(t222.getTransitionId(), t222);

     //   (0, 3): '#',
//        // alsoi, right...
//        Transition t233 = new Transition(s03, s03, up, 0.8);
//        transitions.put(t233.getTransitionId(), t233);
//
//        Transition t234 = new Transition(s03, s02, left, 0.1);
//        transitions.put(t234.getTransitionId(), t234);

        /////////////////

        // s34 transitions:
        //

        HashMap<String, Action> actions = new HashMap<String, Action>();

        actions.put(up.getActionId(), up);
        actions.put(left.getActionId(), left);
        actions.put(down.getActionId(), down);
        actions.put(right.getActionId(), right);

        HashMap<String, Reward> rewards = new HashMap<String, Reward>();

        for (Action action : actions.values()) {

            for (State s1 : states.values()) {

                for (State s2 : states.values()) {
                    if(s2.getId() == "pos_0_3"){
                        Reward reward = new Reward(s1, s2, action, 1.0);
                        rewards.put(reward.getId(), reward);
                    }
                    else if(s2.getId() == "pos_1_3"){
                        Reward reward = new Reward(s1, s2, action, -1.0);
                        rewards.put(reward.getId(), reward);
                    }
                    else {
                        Reward reward = new Reward(s1, s2, action, -0.02);
                        rewards.put(reward.getId(), reward);
                    }
                }
            }
        }


        MDP mdp = new MDP(transitions, actions, states, rewards, false);

        Double epsilon = 0.0001;
        Double discountFactor = 0.99;
        UtilityCalculator uc = new UtilityCalculator(mdp,epsilon,discountFactor);

        return uc;
    }

    //0.99

    // ** implementing Example 17.2 in the book **

    public static void main(String[] args) {

        //UtilityCalculator uc = buildBookExample();

        //UtilityCalculator uc = buildMDPAndExactSolutionMethodsExample();

       // UtilityCalculator uc = buildAnotherExample();

        UtilityCalculator uc = buildMDPForMaman17Q1();
        uc.setOptimalPolicy();
    }
}
