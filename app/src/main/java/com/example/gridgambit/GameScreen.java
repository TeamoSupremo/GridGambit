package com.example.gridgambit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.json.JSONException;

import java.util.Locale;

public class GameScreen extends AppCompatActivity {

    Level levelUI;
    private static final float ITEM_MARGIN_MODIFIER = 0.05f;
    private static final float ITEM_MARGIN_MODIFIER_TOP = 0.1f;
    private static final float GAME_MARGIN_MODIFIER = 0.025f;
    private Activity gs;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gs = this;
        super.onCreate(savedInstanceState);

        //reset power-ups charge
        Player.PlayerInfo.powerCharge = 0;
        //reset players score
        Level.LevelInfo.currentScore = 0;

        if (Player.PlayerInfo.level == 17){
            Level.LevelInfo.currentScore = 100;
        }
        // Assign UI references
        levelUI = new Level();
        setContentView(R.layout.activity_game_screen);
        levelUI.gameLayout = findViewById(R.id.game_layout);
        levelUI.infoLayout = findViewById(R.id.info_ui_layout);
        levelUI.turnsText = findViewById(R.id.turns_text);
        levelUI.levelText = findViewById(R.id.level_text);
        levelUI.scoreText = findViewById(R.id.score_text);
        levelUI.targetText = findViewById(R.id.target_text);
        levelUI.powerChargeBar = findViewById(R.id.charge_progress_bar);
        levelUI.turnsLevelRow = (LinearLayout) levelUI.turnsText.getParent();
        levelUI.scoreRow = (LinearLayout) levelUI.scoreText.getParent();
        levelUI.soundButton = findViewById(R.id.sound_button);

        // Remove UI elements if endless mode is on
        if(Player.PlayerInfo.isEndless){
            Player.PlayerInfo.level = 0;
            levelUI.turnsLevelRow.removeView(levelUI.turnsText);
            levelUI.turnsLevelRow.removeView(levelUI.levelText);
            levelUI.scoreRow.removeView(levelUI.targetText);
        }

        levelUI.data = DataManager.loadLevels(getApplicationContext());

        try {
            //assign level info from JSON
            Level.LevelInfo.grid = levelUI.data.getJSONArray("value");
            Level.LevelInfo.currentTurns = levelUI.data.getInt("turns");
            Level.LevelInfo.targetScore = levelUI.data.getInt("score");
            GridUtil.updateUI(levelUI, getApplicationContext());

            Typeface face = Typeface.createFromAsset(getAssets(),
                    "font/akashi.ttf");

            //if the square root of the total number of values is an integer, our grid will be evenly sized
            if (Math.sqrt(Level.LevelInfo.grid.length()) == (int) Math.sqrt(Level.LevelInfo.grid .length())) {

                //assign square root as grid size
                Level.LevelInfo.gridSize = (int) Math.sqrt(Level.LevelInfo.grid.length());
            }

            // Get display metrics for dynamic grid sizing
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            System.out.println(width);

            // Set grid items to be 80% of screen width
            int newWidth = (int) (width * 0.8) / Level.LevelInfo.gridSize;
            int newHeight = (int) (width * 0.8) / Level.LevelInfo.gridSize;
            int textSize = (int) (newWidth / 5);


            // Assign info UI params
            levelUI.infoLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(newWidth, newHeight);

            // Create grid rows
            // TODO: Change to grid size depending on level
            for (int i = 0; i < Level.LevelInfo.gridSize; i++) {
                System.out.println(levelUI.gameLayout);
                System.out.println(i);
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(params);
                row.setOrientation(LinearLayout.HORIZONTAL);

                // Fill grid rows
                for (int j = 0; j < Level.LevelInfo.gridSize; j++) {
                    // TODO: scale gridItem size based on level
                    // TODO: create background for gridItem
                    GridTextView gridItem = new GridTextView(this);
                    gridItem.setText(String.format(Locale.getDefault(), "%d", Level.LevelInfo.grid.getInt(j + (i * Level.LevelInfo.gridSize))));
                    int gridText = j % 2;
                    gridItem.setTextColor(Color.parseColor("#1f1f2a"));
                    // Center text
                    gridItem.setGravity(Gravity.CENTER);
                    gridItem.setTextSize(textSize);
                    gridItem.setBackgroundColor(Color.LTGRAY);
                    gridItem.setMaxLines(1);
                    gridItem.setTypeface(face);
                    gridItem.setLayoutParams(itemParams);
                    itemParams.leftMargin = (int) ((width * ITEM_MARGIN_MODIFIER) / Level.LevelInfo.gridSize);
                    itemParams.rightMargin = (int) ((width * ITEM_MARGIN_MODIFIER) / Level.LevelInfo.gridSize);
                    itemParams.topMargin = (int) ((width * ITEM_MARGIN_MODIFIER_TOP) / Level.LevelInfo.gridSize);
                    gridItem.xLocation = j;
                    gridItem.yLocation = i;
                    gridItem.setOnTouchListener(new gridTouchListener());
                    gridItem.setOnDragListener(new gridDragListener());
                    row.addView(gridItem);
                }
                levelUI.gameLayout.addView(row);
            }
            ConstraintLayout.LayoutParams paramsGameLayout = (ConstraintLayout.LayoutParams) levelUI.gameLayout.getLayoutParams();
            paramsGameLayout.leftMargin = (int) (width * GAME_MARGIN_MODIFIER);
            paramsGameLayout.rightMargin = (int) (width * GAME_MARGIN_MODIFIER);
            paramsGameLayout.bottomMargin = (int) (width * GAME_MARGIN_MODIFIER);

            levelUI.gameLayout.setPadding(
                    (int) (width * GAME_MARGIN_MODIFIER),
                    (int) (width * GAME_MARGIN_MODIFIER),
                    (int) (width * GAME_MARGIN_MODIFIER),
                    (int) (width * GAME_MARGIN_MODIFIER));

            levelUI.gameLayout.setLayoutParams(paramsGameLayout);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LinearLayout chargeBar = findViewById(R.id.charge_bar_layout);
        LinearLayout chargeBarParent = (LinearLayout) chargeBar.getParent();
        //if player is passed level 3 then add the charge bar back
        if(Player.PlayerInfo.level >= 3 && chargeBar.getParent() == null){
            chargeBarParent.addView(chargeBar);
        }
    }

    private static final class gridTouchListener implements View.OnTouchListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @SuppressLint({"ClickableViewAccessibility"})
        @Override
        //start drag
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                return true;
            }
            return false;
        }

    }

    private final class gridDragListener implements View.OnDragListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean onDrag(View v, DragEvent event) {
            GridTextView movingSquare = (GridTextView) event.getLocalState();
            GridTextView goalSquare;
            final int action = event.getAction();

            switch(action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    goalSquare = (GridTextView) v;
                    int nextMatchNumber = movingSquare.matches + 1;
                    switch(movingSquare.matches) {
                        case 0:
                        if (GridUtil.matchCanBeMadeWith(movingSquare, movingSquare, goalSquare, nextMatchNumber)) {
                            movingSquare.firstMatch = goalSquare.getValue();
                            movingSquare.firstMatchObject = goalSquare;
                            movingSquare.matches++;
                            return true;
                        }
                            break;
                        case 1:
                            if (GridUtil.matchCanBeMadeWith(movingSquare, movingSquare.firstMatchObject, goalSquare, nextMatchNumber)) {
                                //references to matching items
                                movingSquare.secondMatch = goalSquare.getValue();
                                movingSquare.secondMatchObject = goalSquare;
                                movingSquare.matches++;
                                return true;
                            }
                            else {
                                movingSquare.matches--;
                            }
                            break;
                        case 2:
                            if (GridUtil.matchCanBeMadeWith(movingSquare, movingSquare.secondMatchObject, goalSquare, nextMatchNumber)) {
                                //references to matching items
                                movingSquare.thirdMatch = goalSquare.getValue();
                                movingSquare.thirdMatchObject = goalSquare;
                                movingSquare.matches++;
                                return true;
                            }
                            else {
                                movingSquare.matches--;
                            }
                            break;
                        case 3:
                            if (GridUtil.matchCanBeMadeWith(movingSquare, movingSquare.thirdMatchObject, goalSquare, nextMatchNumber)) {
                                movingSquare.matches++;
                                return true;
                            } else {
                                movingSquare.matches--;
                            }
                            return true;
                    }
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    // TODO: Matching object needs to be unassigned if receding
                    return true;
                case DragEvent.ACTION_DROP:
                    goalSquare = (GridTextView) v;
                    switch (movingSquare.matches) {

                        case 1:
                            if (GridUtil.matchCanBeMadeWith(movingSquare, movingSquare, goalSquare)) {
                                Level.LevelInfo.currentTurns--;
                                //total value for element and score
                                int sequence = movingSquare.getValue() + movingSquare.firstMatch;
                                String sequenceString = sequence + "";
                                //change value displayed to be the total value of all matches
                                goalSquare.setText(sequenceString);
                                Level.LevelInfo.currentScore += sequence;
                                Player.PlayerInfo.powerCharge += sequence * 4;
                                GridUtil.updateUI(levelUI, getApplicationContext());
                            }
                            break;
                        case 2:
                            if (GridUtil.matchCanBeMadeWith(movingSquare, movingSquare.firstMatchObject, goalSquare)) {
                                Level.LevelInfo.currentTurns--;
                                //total value for element and score
                                int sequence = movingSquare.getValue() + movingSquare.firstMatch + movingSquare.secondMatch;
                                String sequenceString = sequence + "";
                                //change value displayed to be the total value of all matches
                                goalSquare.setText(sequenceString);
                                Level.LevelInfo.currentScore += sequence;
                                Player.PlayerInfo.powerCharge += sequence * 4;
                                GridUtil.updateUI(levelUI, getApplicationContext());
                            }
                            break;
                        case 3:
                            if (GridUtil.matchCanBeMadeWith(movingSquare, movingSquare.secondMatchObject, goalSquare)) {
                                Level.LevelInfo.currentTurns--;
                                //total value for element and score
                                int sequence = movingSquare.getValue() + movingSquare.firstMatch + movingSquare.secondMatch + movingSquare.thirdMatch;
                                String sequenceString = sequence + "";
                                //change value displayed to be the total value of all matches
                                goalSquare.setText(sequenceString);
                                Level.LevelInfo.currentScore += sequence;
                                Player.PlayerInfo.powerCharge += sequence * 4;
                                GridUtil.updateUI(levelUI, getApplicationContext());
                            }
                            break;
                    }
                    //if charge is full and player hasn't activated power-up
                    if (levelUI.powerChargeBar.getProgress() == 100 && !Player.PlayerInfo.powerActivated) {
                        //switch background
                        Button powerButton = findViewById(R.id.power_charge_button);
                        powerButton.setBackground(getDrawable(R.drawable.charge_button_drawable));
                    }
                    //if power is active and player has made a match
                    if (Player.PlayerInfo.powerActivated && movingSquare.matches != 0) {
                        //deactivate power up and reduce charge to 0
                        Player.PlayerInfo.powerActivated = false;
                        Player.PlayerInfo.powerCharge = 0;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            levelUI.powerChargeBar.setProgress(Player.PlayerInfo.powerCharge, true);
                        }
                        //switch UI back to normal
                        Button powerButton = findViewById(R.id.power_charge_button);
                        powerButton.setBackground(getDrawable(R.drawable.noncharge_button_drawable));
                        LinearLayout chargeLayout = findViewById(R.id.charge_bar_layout);
                        chargeLayout.setBackground(getDrawable(R.drawable.value_text_background));
                    }
                    //remove any references to older matches
                    movingSquare.matches = 0;
                    movingSquare.firstMatchObject = null;
                    movingSquare.secondMatchObject = null;
                    movingSquare.thirdMatchObject = null;
                    GridUtil.winCheck(getApplicationContext(), levelUI, gs);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    goalSquare = (GridTextView) v;
                    return true;
                default:
                    break;
            }
            return false;
        }
    }
    public void executeCharge(View view){
        GridUtil.executeCharge(view, levelUI, getApplicationContext());
    }
}
