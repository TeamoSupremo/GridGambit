package com.example.gridgambit;
import com.example.gridgambit.DataManager;
import com.google.android.material.snackbar.Snackbar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
        return (Math.abs(goalItem.getValue() - previousGoalItem.getValue()) == 1 || Player.PlayerInfo.powerActivated) &&
                ((movingItem.xLocation == goalItem.xLocation && Math.abs(movingItem.yLocation - goalItem.yLocation) == movingItem.matches) ||
                        (movingItem.yLocation == goalItem.yLocation && Math.abs(movingItem.xLocation - goalItem.xLocation) == movingItem.matches));
    }

    public static boolean matchCanBeMadeWith(GridTextView movingItem, GridTextView previousGoalItem, GridTextView goalItem, int matchNumber){
        return (Math.abs(goalItem.getValue() - previousGoalItem.getValue()) == 1 || Player.PlayerInfo.powerActivated) &&
                ((movingItem.xLocation == goalItem.xLocation && Math.abs(movingItem.yLocation - goalItem.yLocation) == matchNumber) ||
                        (movingItem.yLocation == goalItem.yLocation && Math.abs(movingItem.xLocation - goalItem.xLocation) == matchNumber));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateUI(Level levelUI, Context context){
        //make sure charge doesn't go over 100
        if(Player.PlayerInfo.powerCharge > 100){
            Player.PlayerInfo.powerCharge = 100;
        }
        if(Player.PlayerInfo.powerCharge < 0){
            Player.PlayerInfo.powerCharge = 0;
        }

        levelUI.scoreText.setText(String.format(Locale.getDefault(),"%s %d", context.getString(R.string.score_text), Level.LevelInfo.currentScore));
        levelUI.powerChargeBar.setProgress(Player.PlayerInfo.powerCharge, true);
        levelUI.powerChargeBar.jumpDrawablesToCurrentState();
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
        // if the player has 100 power, grid is never locked
        if(Player.PlayerInfo.powerCharge == 100){
            return false;
        }

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

    private static void achievementCheck(Context context){
        //if the player beats level 14 and the difficulty is on easy
        if ((!Player.PlayerInfo.isEndless) && Player.PlayerInfo.level >= 14) {
            //award the first achievement
            Player.PlayerInfo.achievements.put("master", true);
        }

        if(Player.PlayerInfo.level == 3 && Level.LevelInfo.currentTurns == 3){
            Player.PlayerInfo.achievements.put("halfLife3Confirmed", true);
        }

        if(Level.LevelInfo.currentTurns == 0){
            Player.PlayerInfo.achievements.put("EZ", true);
        }

        DataManager.savePlayerInfo(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void winCheck(final Context context, final Level levelUI, final Activity activity){

        // If player has reached target score (includes odd cases for 14 and 17)
        if (Level.LevelInfo.currentScore >= Level.LevelInfo.targetScore &&
                (Player.PlayerInfo.level != 14) &&
                (Player.PlayerInfo.level != 17) ||
                (Player.PlayerInfo.level == 14 &&
                        Level.LevelInfo.currentScore <= Level.LevelInfo.targetScore) ||
                (Player.PlayerInfo.level == 17
                        && Level.LevelInfo.currentScore <= Level.LevelInfo.targetScore
                )
        ){
            // Play win sound
            Level.LevelInfo.soundPool.play(
                    Level.LevelInfo.winSoundId,
                    (Player.PlayerInfo.soundLevel * 0.7f),
                    (Player.PlayerInfo.soundLevel * 0.7f),
                    0,
                    0,
                    1
            );
            // Create win screen layout overlay
            final LinearLayout winScreen = new LinearLayout(context);
            Button mainMenuButton = new Button(context);
            mainMenuButton.setText(R.string.main_menu);
            mainMenuButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            mainMenuButton.setTextColor(Color.parseColor("#dddddd"));
            mainMenuButton.setBackground(context.getDrawable(R.drawable.endgame_button_background));
            Button closeButton = new Button(context);
            closeButton.setText("");
            closeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            closeButton.setGravity(Gravity.CENTER);
            closeButton.setTextColor(Color.parseColor("#dddddd"));
            closeButton.setBackground(context.getDrawable(R.drawable.close_drawable));
            TextView winMessage = new TextView(context);
            winMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            winMessage.setTextColor(Color.parseColor("#dddddd"));
            winMessage.setGravity(Gravity.CENTER);
            final Button nextLevelButton = new Button(context);
            nextLevelButton.setText(R.string.next_level);
            nextLevelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            nextLevelButton.setTextColor(Color.parseColor("#dddddd"));
            nextLevelButton.setBackground(context.getDrawable(R.drawable.endgame_button_background));

            mainMenuButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Level.LevelInfo.soundPool.release();
                    Level.LevelInfo.soundPool = null;
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.finish();

                }
            });

            // Set mode to endless if choosing to continue after completing level
            closeButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Player.PlayerInfo.levelPassed = true;
                    Level.LevelInfo.targetScore = 99999;
                    Level.LevelInfo.currentTurns = 99999;
                    LinearLayout row1 = (LinearLayout) levelUI.turnsText.getParent();
                    LinearLayout row2 = (LinearLayout) levelUI.scoreText.getParent();
                    row1.removeView(levelUI.turnsText);
                    row1.removeView(levelUI.levelText);
                    row2.removeView(levelUI.targetText);

                    ((ViewGroup) winScreen.getParent()).removeView(winScreen);

                    LinearLayout nextLevelParent = (LinearLayout) nextLevelButton.getParent();
                    if(Player.PlayerInfo.level < Level.LevelInfo.levelArray.length()-1) {
                        nextLevelParent.removeView(nextLevelButton);
                        float density = v.getContext().getResources().getDisplayMetrics().density;
                        int layoutMargin = (int) (10 * density + 0.5f);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                        params.bottomMargin = layoutMargin;
                        nextLevelButton.setLayoutParams(params);
                        nextLevelButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        row1.addView(nextLevelButton);
                    }
                }
            });

            nextLevelButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Level.LevelInfo.soundPool.release();
                    Level.LevelInfo.soundPool = null;
                    DataManager.savePlayerInfo(context);
                    Player.PlayerInfo.levelPassed = false;
                    Intent intent = new Intent(v.getContext(), GameScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.finish();
                }
            });

            if(Player.PlayerInfo.level < Level.LevelInfo.levelArray.length()-1){

                if(!Player.PlayerInfo.isEndless){

                    if(Player.PlayerInfo.levelHighest < Player.PlayerInfo.level){

                        Player.PlayerInfo.levelHighest = Player.PlayerInfo.level;
                    }
                }
                else {
                    Player.PlayerInfo.level = 0;
                }

                winMessage.setText(String.format(Locale.getDefault(), "%s %d %s", context.getString(R.string.level_text), (Player.PlayerInfo.level+1), " complete!"));
            }
            else{
                winMessage.setText(R.string.finish);
            }

            // If player has passed final level
            if(Player.PlayerInfo.level < Level.LevelInfo.levelArray.length()-1){
                winScreen.setOrientation(LinearLayout.VERTICAL);
                winScreen.addView(closeButton);
                winScreen.addView(winMessage);
                winScreen.addView(nextLevelButton);
                winScreen.addView(mainMenuButton);
                Player.PlayerInfo.level++;
            }
            else{
                winScreen.setOrientation(LinearLayout.VERTICAL);
                winScreen.addView(closeButton);
                winScreen.addView(winMessage);
                winScreen.addView(mainMenuButton);
            }

            winScreen.setBackground(context.getDrawable(R.drawable.status_text_background));
            winScreen.setClickable(true);
            winScreen.setId(View.generateViewId());
            ConstraintLayout constraintLayout = activity.findViewById(R.id.game_constraint_layout);
            constraintLayout.addView(winScreen);
            winScreen.setElevation(100);

            // Display win screen
            float density = context.getResources().getDisplayMetrics().density;
            int layoutMargin = (int) (50 * density + 0.5f);
            ConstraintSet cs = new ConstraintSet();
            cs.clone(constraintLayout);
            cs.connect(winScreen.getId(), ConstraintSet.TOP, R.id.info_ui_layout, ConstraintSet.TOP, 0);
            cs.applyTo(constraintLayout);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            LinearLayout.LayoutParams paramsCloseButton = new LinearLayout.LayoutParams(layoutMargin, layoutMargin);
            LinearLayout.LayoutParams paramsNextLevelButton = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsMainMenuButton = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams paramsWinMessage = new LinearLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            paramsWinMessage.bottomMargin = layoutMargin;
            paramsNextLevelButton.bottomMargin = layoutMargin;
            mainMenuButton.setLayoutParams(paramsMainMenuButton);
            winMessage.setLayoutParams(paramsWinMessage);
            nextLevelButton.setLayoutParams(paramsNextLevelButton);

            winScreen.setLayoutParams(params);
            winScreen.setGravity(Gravity.END | Gravity.CENTER);
            closeButton.setLayoutParams(paramsCloseButton);

            // check if achievements have been earned
            achievementCheck(context);
        }
        else{
            loseCheck(context, levelUI, activity);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void loseCheck(final Context context, Level levelUI, final Activity activity){
        boolean gridLock = gridLockCheck(levelUI);

        if(Level.LevelInfo.currentTurns < 1 || gridLock){
            Level.LevelInfo.soundPool.play(Level.LevelInfo.loseSoundId, Player.PlayerInfo.soundLevel, Player.PlayerInfo.soundLevel, 0, 0, 1);
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
                    Level.LevelInfo.soundPool.release();
                    Level.LevelInfo.soundPool = null;
                    Intent intent = new Intent(v.getContext(), GameScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.finish();
                }
            });

            nextLevelButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Level.LevelInfo.soundPool.release();
                    Level.LevelInfo.soundPool = null;
                    DataManager.savePlayerInfo(context);
                    Player.PlayerInfo.levelPassed = false;
                    Intent intent = new Intent(v.getContext(), GameScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.finish();
                }
            });

            mainMenuButton.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                // else loss is because of no turns left
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

    // Changes colour based on value in gridItem
    public static void updateGridItemColour(GridTextView gridItem){
        // blue
        if(gridItem.getValue() <= 12){
            gridItem.redInBackground = (200 - gridItem.getValue() * 15);
            gridItem.greenInBackground = (200 - gridItem.getValue() * 15);
            gridItem.blueInBackground = (gridItem.getValue() * 8 + 180);

            if(gridItem.blueInBackground >= 255){

                gridItem.blueInBackground = 255;
            }
        }
        // purple
        else if(gridItem.getValue() <= 24){
            gridItem.redInBackground = (gridItem.getValue() * 8);
            gridItem.greenInBackground = 50;
            gridItem.blueInBackground = 255;

        }
        // red
        else if(gridItem.getValue() <= 36){
            gridItem.redInBackground = (50 + gridItem.getValue() * 5);
            gridItem.greenInBackground = 0;
            gridItem.blueInBackground = (int) (255 * (1 - (gridItem.getValue() / 36.0)));
        }
        // orange
        else if(gridItem.getValue() <= 48){
            gridItem.redInBackground = 255;
            gridItem.greenInBackground = ((gridItem.getValue() - 36) * 15);
            gridItem.blueInBackground = 0;

            if(gridItem.greenInBackground >= 255){

                gridItem.greenInBackground = 255;
            }
        }
        // yellow -> white
        else{
            gridItem.redInBackground = 255;
            gridItem.greenInBackground = 180;
            gridItem.blueInBackground = gridItem.getValue() / 10;
        }

        gridItem.getBackground().setColorFilter(Color.rgb(gridItem.redInBackground, gridItem.greenInBackground, gridItem.blueInBackground), PorterDuff.Mode.OVERLAY);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void executeCharge(View view, Level levelUI, Context context){

        if(Player.PlayerInfo.powerActivated && levelUI.powerChargeBar.getProgress() == 100){
            Level.LevelInfo.soundPool.play(Level.LevelInfo.powerDeactivatedSoundId, (Player.PlayerInfo.soundLevel * 0.7f), (Player.PlayerInfo.soundLevel * 0.8f), 0, 0, 1);
            Player.PlayerInfo.powerActivated = false;
            Snackbar powerSB = Snackbar.make(view, "Power Deactivated", Snackbar.LENGTH_SHORT);
            View v = powerSB.getView();
            TextView txtv = v.findViewById(R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            txtv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            powerSB.show();
            view.setBackground(context.getDrawable(R.drawable.value_text_background));
        }

        else if(levelUI.powerChargeBar.getProgress() == 100) {
            Level.LevelInfo.soundPool.play(Level.LevelInfo.powerActivatedSoundId, (Player.PlayerInfo.soundLevel * 0.7f), (Player.PlayerInfo.soundLevel * 0.8f), 0, 0, 1);
            Player.PlayerInfo.powerActivated = true;
            Snackbar powerSB = Snackbar.make(view, "Power Activated", Snackbar.LENGTH_SHORT);
            View v = powerSB.getView();
            TextView txtv = v.findViewById(R.id.snackbar_text);
            txtv.setGravity(Gravity.CENTER_HORIZONTAL);
            txtv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            powerSB.show();
            view.setBackground(context.getDrawable(R.drawable.layout_charge_active));
        }
    }
}
