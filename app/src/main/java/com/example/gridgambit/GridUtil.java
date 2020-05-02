package com.example.gridgambit;

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
}
