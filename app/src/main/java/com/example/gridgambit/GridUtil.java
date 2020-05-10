package com.example.gridgambit;

import android.content.Context;

import java.util.Locale;

public class GridUtil {
    public static boolean matchCanBeMadeWith(GridTextView movingItem, GridTextView previousGoalItem, GridTextView goalItem){
        return Math.abs(goalItem.getValue() - previousGoalItem.getValue()) == 1 &&
                ((movingItem.xLocation == goalItem.xLocation && Math.abs(movingItem.yLocation - goalItem.yLocation) == movingItem.matches) ||
                        (movingItem.yLocation == goalItem.yLocation && Math.abs(movingItem.xLocation - goalItem.xLocation) == movingItem.matches));
    }

    public static boolean matchCanBeMadeWith(GridTextView movingItem, GridTextView previousGoalItem, GridTextView goalItem, int matchNumber){
        return Math.abs(goalItem.getValue() - previousGoalItem.getValue()) == 1 &&
                ((movingItem.xLocation == goalItem.xLocation && Math.abs(movingItem.yLocation - goalItem.yLocation) == matchNumber) ||
                        (movingItem.yLocation == goalItem.yLocation && Math.abs(movingItem.xLocation - goalItem.xLocation) == matchNumber));
    }

    public static void updateUI(Level levelUI, Context context){

        levelUI.scoreText.setText(String.format(Locale.getDefault(),"%s %d", context.getString(R.string.score_text), Level.LevelInfo.currentScore));
        try {
            levelUI.turnsText.setText(String.format(Locale.getDefault(),"%s %d", context.getString(R.string.turns_text), Level.LevelInfo.currentTurns));
            levelUI.targetText.setText(String.format(Locale.getDefault(),"%s %d", context.getString(R.string.target_text), Level.LevelInfo.targetScore));
            levelUI.levelText.setText(String.format(Locale.getDefault(), "%s %d", context.getString(R.string.level_text), Player.PlayerInfo.level + 1));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
