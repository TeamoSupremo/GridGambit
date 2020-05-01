package com.example.gridgambit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Locale;

public class GameScreen extends AppCompatActivity {

    Level levelUI;
    private static final float ITEM_MARGIN_MODIFIER = 0.05f;
    private static final float ITEM_MARGIN_MODIFIER_TOP = 0.1f;
    private static final float GAME_MARGIN_MODIFIER = 0.025f;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Assign UI references
        levelUI = new Level();
        setContentView(R.layout.activity_game_screen);
        levelUI.gameLayout = findViewById(R.id.game_layout);
        levelUI.infoLayout = findViewById(R.id.info_ui_layout);
        levelUI.turnsText = findViewById(R.id.turns_text);
        levelUI.levelText = findViewById(R.id.level_text);
        levelUI.scoreText = findViewById(R.id.score_text);
        levelUI.targetText = findViewById(R.id.target_text);
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

        // Get display metrics for dynamic grid sizing
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        System.out.println(width);

        // Set grid items to be 80% of screen width
        int newWidth = (int) (width * 0.8) / 4;
        int newHeight = (int) (width * 0.8) / 4;
        int textSize = (int) (newWidth / 5);
        Level.LevelInfo.gridSize = 4;


        // Assign info UI params
        levelUI.infoLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(newWidth, newHeight);

        // Create grid rows
        // TODO: Change to grid size depending on level
        for (int i = 0; i < 4; i++) {
            System.out.println(levelUI.gameLayout);
            System.out.println(i);
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(params);
            row.setOrientation(LinearLayout.HORIZONTAL);

            // Fill grid rows
            for (int j = 0; j < 4; j++) {
                // TODO: scale gridItem size based on level
                // TODO: create background for gridItem
                GridTextView gridItem = new GridTextView(this);
                // TODO: set gridItem text based on level
                gridItem.setText("1");
                gridItem.setTextColor(Color.parseColor("#1f1f2a"));
                // Center text
                gridItem.setGravity(Gravity.CENTER);
                gridItem.setTextSize(textSize);
                gridItem.setBackgroundColor(Color.LTGRAY);
                gridItem.setMaxLines(1);
                gridItem.setLayoutParams(itemParams);
                itemParams.leftMargin = (int) ((width * ITEM_MARGIN_MODIFIER) / 4);
                itemParams.rightMargin = (int) ((width * ITEM_MARGIN_MODIFIER) / 4);
                itemParams.topMargin = (int) ((width * ITEM_MARGIN_MODIFIER_TOP) / 4);
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

    private static final class gridDragListener implements View.OnDragListener {

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
                    movingSquare.firstMatch = goalSquare.getValue();
                    movingSquare.firstMatchObject = goalSquare;
                    // TODO: Matching with multiple objects
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    // TODO: Matching object needs to be unassigned if receding
                    return true;
                case DragEvent.ACTION_DROP:
                    goalSquare = (GridTextView) v;
                    Level.LevelInfo.currentTurns--;
                    int sequence = movingSquare.getValue() + movingSquare.firstMatch;
                    String sequenceString = sequence + "";
                    goalSquare.setText(sequenceString);
                    Level.LevelInfo.currentScore += sequence;
                    movingSquare.matches = 0;
                    movingSquare.firstMatchObject = null;
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
}
