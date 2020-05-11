package com.example.gridgambit;
import com.example.gridgambit.DataManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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

    private static boolean gridLockCheck(Level levelUI) {

        // TODO: Add check of Powerbar at 100

        //start as true, if a single move can be made, assign false
        boolean gridLock = true;

        //assign blocks an array of all of the boards pieces
        GridTextView[] blocks = new GridTextView[Level.LevelInfo.gridSize* Level.LevelInfo.gridSize];
        int blockCounter = 0;

        for(int i = 0; i < Level.LevelInfo.gridSize; i++){
            for(int j = 0; j < Level.LevelInfo.gridSize; j++) {

                LinearLayout row = (LinearLayout)levelUI.gameLayout.getChildAt(i);
                blocks[blockCounter] = (GridTextView) row.getChildAt(j);
                blockCounter++;
            }
        }
        //for each piece
        for (GridTextView block1 : blocks) {

            //for every other piece
            for (GridTextView block : blocks) {

                //if a match can be made
                if (Math.abs(block1.getValue() - block.getValue()) == 1 &&
                        ((block1.xLocation == block.xLocation && Math.abs(block1.yLocation - block.yLocation) == 1) ||
                                (block1.yLocation == block.yLocation && Math.abs(block1.xLocation - block.xLocation) == 1))) {
                    //then the grid is not locked
                    gridLock = false;
                }
            }
        }
        //return whether grid is locked
        return gridLock;
    }

    // TODO: Add wincheck

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void loseCheck(final Context context, Level levelUI, final Activity activity){
        boolean gridLock = gridLockCheck(levelUI);

        if(Level.LevelInfo.currentTurns < 1 || gridLock){
            // TODO: Add lose sound

            LinearLayout loseScreen = new LinearLayout(context);
            Button retryButton = new Button(context);
            retryButton.setText(R.string.retry);
            retryButton.setTextColor(Color.parseColor("#dddddd"));
            retryButton.setBackground(context.getDrawable(R.drawable.endgame_button_background));
            retryButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            final Button nextLevelButton = new Button(context);
            nextLevelButton.setText(R.string.next_level);
            nextLevelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            nextLevelButton.setTextColor(Color.parseColor("#dddddd"));
            nextLevelButton.setBackground(context.getDrawable(R.drawable.endgame_button_background));
            Button mainMenuButton = new Button(context);
            mainMenuButton.setText(R.string.main_menu);
            mainMenuButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            mainMenuButton.setTextColor(Color.parseColor("#dddddd"));
            mainMenuButton.setBackground(context.getDrawable(R.drawable.endgame_button_background));
            TextView loseMessage = new TextView(context);
            loseMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            loseMessage.setGravity(Gravity.CENTER);
            loseMessage.setTextColor(Color.parseColor("#dddddd"));

            retryButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO: release sounds
                    Intent intent = new Intent(v.getContext(), GameScreen.class);
                    context.startActivity(intent);
                    activity.finish();
                }
            });

            nextLevelButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO: release sounds
                    //TODO: save player info in data manager
                    Player.PlayerInfo.levelPassed = false;
                    Intent intent = new Intent(v.getContext(), GameScreen.class);
                    context.startActivity(intent);
                    activity.finish();
                }
            });

            mainMenuButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    context.startActivity(intent);
                    activity.finish();
                }
            });

            if (gridLock) {
                if (!Player.PlayerInfo.levelPassed && !Player.PlayerInfo.isEndless) {
                    loseMessage.setText(R.string.lf_gridlock);
                }
                else{
                    loseMessage.setText(R.string.gridlock);
                }
                //else loss is because of no turns left
            } else {
                loseMessage.setText(R.string.lf_no_more_turns);
            }

            loseScreen.setOrientation(LinearLayout.VERTICAL);
            loseScreen.addView(loseMessage);
            if (!Player.PlayerInfo.levelPassed) {
                loseScreen.addView(retryButton);
            }
            else if (Player.PlayerInfo.levelPassed && Player.PlayerInfo.level < Level.LevelInfo.levelArray.length()-1){
                loseScreen.addView(nextLevelButton);
            }
            float density = context.getResources().getDisplayMetrics().density;
            int layoutMargin = (int) (50 * density + 0.5f);
            loseScreen.addView(mainMenuButton);
            LinearLayout.LayoutParams paramsRetryButton = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            paramsRetryButton.bottomMargin = layoutMargin;
            retryButton.setLayoutParams(paramsRetryButton);
            LinearLayout.LayoutParams paramsLoseMessage = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            paramsLoseMessage.bottomMargin = layoutMargin;
            loseMessage.setLayoutParams(paramsLoseMessage);
            LinearLayout.LayoutParams paramsNextLevelButton = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            paramsNextLevelButton.bottomMargin = layoutMargin;
            nextLevelButton.setLayoutParams(paramsLoseMessage);
            loseScreen.setBackground(context.getDrawable(R.drawable.status_text_background));
            loseScreen.setId(View.generateViewId());
            loseScreen.setClickable(true);
            ConstraintLayout constraintLayout = activity.findViewById(R.id.game_constraint_layout);
            constraintLayout.addView(loseScreen);
            ConstraintSet cs = new ConstraintSet();
            cs.clone(constraintLayout);
            cs.connect(loseScreen.getId(), ConstraintSet.TOP, R.id.info_ui_layout, ConstraintSet.TOP, 0);
            cs.applyTo(constraintLayout);
            loseScreen.setElevation(100);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            loseScreen.setLayoutParams(params);
            loseScreen.setGravity(Gravity.END | Gravity.CENTER);
        }
    }
}
