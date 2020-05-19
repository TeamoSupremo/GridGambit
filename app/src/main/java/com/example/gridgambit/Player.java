package com.example.gridgambit;

import java.util.LinkedHashMap;

public class Player {

    static class PlayerInfo {

        static Achievement achievement;
        static LinkedHashMap<String, Boolean> achievements = new LinkedHashMap<>();
        static boolean isEndless = false;
        static int level = 0;
        static int levelHighest = 0;
        static int soundLevel = 1;
        static boolean levelPassed = false;
        static int powerCharge = 0;
        static boolean powerActivated = false;

        PlayerInfo() {
            achievement = new Achievement();
            achievements = achievement.list;
        }
    }


}
